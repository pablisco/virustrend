package org.virustrend.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import org.virustrend.Cases
import org.virustrend.Country
import org.virustrend.GlobalTotal
import org.virustrend.domain.AppEvent.ChangeCountry
import org.virustrend.domain.AppEvent.StartMapScreen
import org.virustrend.domain.Async.*
import org.virustrend.query.Query

@FlowPreview
@ExperimentalCoroutinesApi
class StateMachine {

    private var currentState = AppState(screen = Loading.Fresh)
    private val stateChannel: Channel<AppState> = Channel(Channel.CONFLATED)

    val states: Flow<AppState>
        get() = stateChannel.receiveAsFlow().saveEach().restoreLast()

    private fun Flow<AppState>.restoreLast(): Flow<AppState> = onStart { emit(currentState) }

    private fun Flow<AppState>.saveEach(): Flow<AppState> =
        onEach { currentState = it }

    suspend fun notify(event: AppEvent) {
        when (event) {
            is StartMapScreen -> {
                currentState.into { loading() }
                try {
                    val total = query(Query.AllCountries)
                    currentState into { idleMapScreen(total) }
                } catch (e: Exception) {
                    currentState into { failed(e) }
                }
            }
            is AppEvent.Reload -> {

            }
            is ChangeCountry -> {
                event.countrySelection.takeIf { it != currentState.countrySelection }?.let { country ->
                    currentState into {
                        copy(
                            countrySelection = event.countrySelection,
                            screen = screen.changeCountry(country)
                        )
                    }
                }
            }
            is AppEvent.ChangeMetric -> {
                val worldMap = currentState.screen.maybeData.takeAs<Screen.WorldMap>()
                worldMap?.selectedMetric
                    ?.let { metric -> event.metric.takeIf { it != metric } }
                    ?.let { metric ->
                        currentState into {
                            copy(screen = Idle(worldMap.copy(selectedMetric = metric)))
                        }
                    }
            }
        }.exhaustive
    }

    private suspend infix fun AppState.into(transform: suspend AppState.() -> AppState) {
        stateChannel.send(transform())
    }
}

private suspend fun Async<Screen>.changeCountry(countrySelection: CountrySelection): Async<Screen> {
    val total = query(Query.AllCountries)
    return map { screen ->
        when (screen) {
            is Screen.WorldMap -> {
                screen.copy(
                    cases = total.countryCases.find { it.country == countrySelection.maybeCountry }?.cases
                        ?: error("Missing country ${countrySelection.maybeCountry}")
                )
            }
        }
    }
}



val Any?.exhaustive: Any? get() = this
inline fun <reified T> Any?.takeAs(): T? = (this as? T)
inline fun <reified T> Any?.requireAs(): T = (this as? T) ?: error("Expected $this to be ${T::class}")

sealed class AppEvent {
    object StartMapScreen : AppEvent()
    object Reload : AppEvent()
    data class ChangeCountry(val countrySelection: CountrySelection) : AppEvent()
    data class ChangeMetric(val metric: DataMetric) : AppEvent()
}

data class AppState(
    val screen: Async<Screen> = Loading.Fresh,
    val countries: Async<List<CountrySelection>> = Loading.Fresh,
    val countrySelection: CountrySelection = CountrySelection.All
) {

    internal fun loading(): AppState =
        copy(screen = screen.maybeData?.let { Loading.WithData(it) } ?: Loading.Fresh)

    internal fun failed(error: Exception): AppState =
        copy(screen = screen.maybeData?.let { Failed.OnReload(error, it) } ?: Failed.OnLoad(error))

}

sealed class Screen {
    data class WorldMap(
        val cases: Cases,
        val countryToMetric: List<Pair<Country, Int>>,
        val selectedMetric: DataMetric
    ) : Screen()
}

sealed class CountrySelection {

    object All : CountrySelection()
    data class Target(val country: Country) : CountrySelection()

    val maybeCountry: Country? get() = takeAs<Target>()?.country

}

private fun AppState.idleMapScreen(total: GlobalTotal): AppState {
    val metric = screen.maybeData?.maybeMetric ?: DataMetric.default
    return copy(
        countries = Idle(total.countryCases.map { it.country }.asSelectableCountries()),
        screen = Idle(
            data = Screen.WorldMap(
                countryToMetric = total.countryCases.map { it.country to it.cases[metric] },
                cases = total.cases,
                selectedMetric = metric
            )
        )
    )
}

private val Screen.maybeMetric get() = when(this) {
    is Screen.WorldMap -> selectedMetric
}

private fun List<Country>.asSelectableCountries(): List<CountrySelection> =
    listOf(CountrySelection.All) + map { CountrySelection.Target(it) }