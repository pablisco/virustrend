package org.virustrend.android

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.pixplicity.sharp.Sharp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.virustrend.CountryCases
import org.virustrend.R
import org.virustrend.android.utils.toColorBetween
import org.virustrend.android.utils.whenSvgElementReady
import org.virustrend.country
import org.virustrend.domain.*
import org.virustrend.domain.SelectableCountry.Some
import org.virustrend.domain.VirusTrendEvent.ChangeCountry
import org.virustrend.domain.VirusTrendEvent.Start
import org.virustrend.domain.VirusTrendState.*
import java.text.NumberFormat

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel by viewModels<MainViewModel>()

    private val loadingViews: List<View> by lazy { listOf(loadingView) }
    private val idleViews: List<View> by lazy {
        listOf(countrySelection, mapView, globalCasesViews)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.states.onEach { it.render() }.launchIn(lifecycleScope)
    }

    private suspend fun VirusTrendState.render() = withContext(Main) {
        loadingViews.visibleOrGoneWhen { this@render is Loading }
        idleViews.visibleOrGoneWhen { this@render is Idle }
        if (this@render is Failed) {
            // TODO Render error with SnackBar
        }
        content?.render()
        content?.casesByCountry?.render(
            selectedCountry = content?.selectedCountry ?: SelectableCountry.None
        )
        countrySelection.with(content?.countries ?: emptyList())
            .onEach { country -> viewModel.trigger(ChangeCountry(country)) }
            .launchIn(lifecycleScope)
    }


    private suspend fun List<CountryCases>.render(
        selectedCountry: SelectableCountry
    ) = withContext(Main) {
        Sharp.loadResource(resources, R.raw.world)
            .whenSvgElementReady { id, paint ->
                paint?.color = sortedBy { it.cases.confirmed }
                    .indexOfFirst { it.country?.code == id }
                    .takeIf { it > 0 }
                    ?.let { it / size.toFloat() }
                    ?.asRedGradient() ?: Color.WHITE
                if (selectedCountry is Some && selectedCountry.country.code == id) {
                    paint?.color = Color.BLUE
                }
            }.into(mapView)
    }

    private suspend fun Content.render() = withContext(Main) {
        total.cases.let { (confirmed, deaths, recovered, active) ->
            confirmedView.setText(confirmed.formatted())
            deathsView.setText(deaths.formatted())
            recoveredView.setText(recovered.formatted())
            activeView.setText(active.formatted())
        }
    }

}

private fun View.visibleOrGoneWhen(block: () -> Boolean) {
    visibility = if (block()) View.VISIBLE else View.GONE
}

private fun List<View>.visibleOrGoneWhen(block: () -> Boolean) {
    forEach { it.visibleOrGoneWhen(block) }
}

@ExperimentalCoroutinesApi
private fun AdapterView<*>.with(countries: List<SelectableCountry>): Flow<SelectableCountry> {
    val labels = countries.map {
        when (it) {
            is SelectableCountry.None -> "All Countries"
            is Some -> it.country.countryName
        }
    }
    adapter = ArrayAdapter(context, R.layout.item_country, labels)
    return selection.map { position ->
        if (position == 0) SelectableCountry.None else countries[position - 1]
    }
}

@ExperimentalCoroutinesApi
private val AdapterView<*>.selection: Flow<Int>
    get() = callbackFlow {
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
//                offer(0)
            }

            override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) {
                offer(position)
            }
        }
        onItemSelectedListener = listener
        awaitClose {
            if(onItemSelectedListener === listener) {
                onItemSelectedListener = null
            }
        }
    }

private fun Int.formatted(): String = NumberFormat.getNumberInstance().format(this)

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel(
    private val stateMachine: VirusTrendStateMachine = VirusTrendStateMachine()
) : ViewModel() {

    init {
        trigger(Start)
    }

    val states get() = stateMachine.states

    fun trigger(event: VirusTrendEvent) {
        viewModelScope.launch(IO) { stateMachine.notify(event) }
    }

}

@ColorInt
private fun Float.asRedGradient(): Int = toColorBetween(Color.WHITE, Color.RED)


