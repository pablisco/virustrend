package org.virustrend.android

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
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
import org.virustrend.android.utils.whenSvgElementReady
import org.virustrend.color.ColorRange
import org.virustrend.color.colorAt
import org.virustrend.color.toPlatformColor
import org.virustrend.country
import org.virustrend.domain.*
import org.virustrend.domain.SelectableCountry.None
import org.virustrend.domain.SelectableCountry.Some
import org.virustrend.domain.VirusTrendEvent.ChangeCountry
import org.virustrend.domain.VirusTrendEvent.Start
import org.virustrend.domain.VirusTrendState.*

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel by viewModels<MainViewModel>()

    private val loadingViews: List<View> by lazy { listOf(loadingView) }
    private val idleViews: List<View> by lazy {
        listOf(countrySelection, mapView, globalCasesView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.states.onEach { it.render() }.launchIn(lifecycleScope)
    }

    private suspend fun VirusTrendState.render() = withContext(Main) {
        loadingViews.visibleOrGoneWhen { this@render is Loading }
        idleViews.visibleOrGoneWhen { this@render is Idle }
        if (this@render is Failed) {
            Snackbar.make(mainRoot, "", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") { viewModel.trigger(Start) } // TODO: add retry event
                .show()
        }
        content?.render()
        content?.casesByCountry?.render(
            selectedCountry = content?.selectedCountry ?: None
        )
        countrySelection.with(content?.countries ?: emptyList())
            .onEach { country ->
                Log.e("Main", "selectedCountry: $country")
                viewModel.trigger(ChangeCountry(country))
            }
            .launchIn(lifecycleScope)
    }


    private suspend fun List<CountryCases>.render(
        selectedCountry: SelectableCountry
    ) = withContext(Main) {
        val cases = sortedBy { it.cases.confirmed }
        Sharp.loadAsset(assets, "world.svg")
            .whenSvgElementReady { id, paint ->
                val position: Double =
                    cases.indexOfFirst { it.country?.code == id }
                        .takeIf { it > 0 }
                        ?.let { it / size.toDouble() } ?: 0.0
                paint?.color = ColorRange.whiteToRed.colorAt(position).toPlatformColor()
                if (selectedCountry is Some && selectedCountry.country.code == id) {
                    paint?.color = Color.GREEN
                }
            }.into(mapView)
    }

    private suspend fun Content.render() = withContext(Main) {
        when(val selection = selectedCountry) {
            is None -> total.cases
            is Some -> total.countryCases.find { it.country == selection.country }?.cases
        }?.let { (confirmed, deaths, recovered, active) ->
            confirmedView.count = confirmed
            deathsView.count = deaths
            recoveredView.count = recovered
            activeView.count = active
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
            is None -> "All Countries"
            is Some -> it.country.countryName
        }
    }
    val itemPosition = selectedItemPosition
    adapter = ArrayAdapter(context, R.layout.item_country, labels)
    setSelection(itemPosition)
    return selection.map { position ->
        if (position == 0) None else countries[position]
    }
}

@ExperimentalCoroutinesApi
private val AdapterView<*>.selection: Flow<Int>
    get() = callbackFlow {
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                offer(0)
            }

            override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) {
                offer(position)
            }
        }
        onItemSelectedListener = listener
        awaitClose {
            onItemSelectedListener = null
        }
    }

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


