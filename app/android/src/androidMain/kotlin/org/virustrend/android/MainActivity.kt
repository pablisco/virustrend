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
import org.virustrend.Country
import org.virustrend.R
import org.virustrend.android.utils.whenSvgElementReady
import org.virustrend.color.ColorRange
import org.virustrend.color.colorAt
import org.virustrend.color.toPlatformColor
import org.virustrend.domain.*
import org.virustrend.domain.AppEvent.ChangeCountry
import org.virustrend.domain.AppEvent.StartMapScreen
import org.virustrend.domain.CountrySelection.All
import org.virustrend.domain.CountrySelection.Target

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel by viewModels<MainViewModel>()

    private val loadingViews: List<View> by lazy { listOf(loadingView) }
    private val idleViews: List<View> by lazy {
        listOf(countrySelectionView, mapView, globalCasesView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.onEach { it.render() }.launchIn(lifecycleScope)
    }

    private suspend fun AppState.render() {
        loadingViews.visibleOrGoneWhen { screen is Async.Loading.Fresh }
        idleViews.visibleOrGoneWhen { screen is Async.Idle<*> }
        screen.takeAs<Async.Failed<*>>()?.also {
            Log.e("Main", it.error.message, it.error)
            Snackbar.make(mainRoot, "", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") { viewModel.notify(StartMapScreen) }
                .show()
        }
        screen.maybeData?.render(countrySelection)
        countrySelectionView.render(this)
            .onEach { viewModel.notify(ChangeCountry(it)) }
            .launchIn(lifecycleScope)
    }

    private suspend fun Screen.WorldMap.render(countrySelection: CountrySelection) {
        countryToMetric.render(countrySelection = countrySelection)
        val (confirmed, deaths, recovered, active) = cases
        confirmedView.count = confirmed
        deathsView.count = deaths
        recoveredView.count = recovered
        activeView.count = active
    }

    private suspend fun Screen.render(countrySelection: CountrySelection) {
        when (this) {
            is Screen.WorldMap -> this.render(countrySelection)
        }
    }

    private suspend fun List<Pair<Country, Int>>.render(
        countrySelection: CountrySelection
    ) = withContext(Main) {
        val cases = sortedBy { (_, count) -> count }
        Sharp.loadAsset(assets, "world.svg")
            .whenSvgElementReady { id, paint ->
                val position: Double =
                    cases.indexOfFirst { (country, _) -> country.code == id }.takeIf { it > 0 }
                        ?.let { it / size.toDouble() } ?: 0.0
                paint?.color = ColorRange.whiteToRed.colorAt(position).toPlatformColor()
                if (countrySelection is Target && countrySelection.country.code == id) {
                    paint?.color = Color.GREEN
                }
            }.into(mapView)
    }

}

private fun View.visibleOrGoneWhen(block: () -> Boolean) {
    visibility = if (block()) View.VISIBLE else View.GONE
}

private fun List<View>.visibleOrGoneWhen(block: () -> Boolean) {
    forEach { it.visibleOrGoneWhen(block) }
}

@ExperimentalCoroutinesApi
private fun AdapterView<*>.render(appState: AppState): Flow<CountrySelection> {
    val countries = appState.countries.maybeData.orEmpty()
    adapter = ArrayAdapter(context, R.layout.item_country, countries.map { it.label })
    setSelection(countries.indexOf(appState.countrySelection))
    return selection.map { position ->
        if (position == 0) All else countries[position]
    }
}

private val CountrySelection.label
    get() = when (this) {
        is All -> "All Countries"
        is Target -> country.countryName
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
    private val stateMachine: StateMachine = StateMachine()
) : ViewModel() {

    init {
        notify(StartMapScreen)
    }

    val state: Flow<AppState> get() = stateMachine.states

    fun notify(event: AppEvent) {
        viewModelScope.launch(IO) {
            stateMachine.notify(event)
        }
    }

}


