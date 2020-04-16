package org.virustrend.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import org.virustrend.Country
import org.virustrend.CountryCases
import org.virustrend.GlobalTotal
import org.virustrend.country
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
                currentState into { loading() }
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
                event.country.takeIf { it != currentState.selectedCountry }?.let {
                    currentState into { copy(selectedCountry = event.country) }
                }
            }
            is AppEvent.ChangeMetric -> {
                event.metric.takeIf { it != currentState.metric }?.let {
                    currentState into { copy(metric = event.metric) }
                }
            }
        }.extensive
    }

    private val Any?.extensive: Any? get() = this

    private suspend infix fun AppState.into(block: AppState.() -> AppState) {
        stateChannel.send(block())
    }
}

sealed class AppEvent {
    object StartMapScreen : AppEvent()
    object Reload : AppEvent()
    data class ChangeCountry(val country: SelectableCountry) : AppEvent()
    data class ChangeMetric(val metric: DataMetric) : AppEvent()
}

data class AppState(
    val screen: Async<Screen>,
    val metric: DataMetric = DataMetric.Confirmed,
    val countries: Async<List<SelectableCountry>> = Loading.Fresh,
    val selectedCountry: SelectableCountry = SelectableCountry.None
)

enum class DataMetric {
    Confirmed, Deaths, Recovered, Active
}

sealed class Async<out T>(val maybeData: T? = null) {
    sealed class Loading<T>(maybeData: T? = null) : Async<T>(maybeData) {
        object Fresh : Loading<Nothing>()
        data class WithData<T>(val data: T) : Loading<T>(data)
    }

    data class Idle<T>(val data: T) : Async<T>(data)
    sealed class Failed<T>(maybeData: T? = null) : Async<T>(maybeData) {
        data class OnLoad(val error: Exception) : Failed<Nothing>()
        data class OnReload<T>(val error: Exception, val data: T) : Failed<T>(data)
    }
}

sealed class Screen {
    data class Map(val casesByCountry: List<CountryCases>, val total: GlobalTotal) : Screen()
}

sealed class SelectableCountry {

    object None : SelectableCountry()
    data class Some(val country: Country) : SelectableCountry()

}

private fun AppState.loading(): AppState =
    copy(screen = screen.maybeData?.let { Loading.WithData(it) } ?: Loading.Fresh)

private fun AppState.failed(error: Exception): AppState =
    copy(screen = screen.maybeData?.let { Failed.OnReload(error, it) } ?: Failed.OnLoad(error))

private fun AppState.idleMapScreen(total: GlobalTotal): AppState = copy(
    countries = Idle(total.countryCases.mapNotNull { it.country }.asSelectableCountries()),
    screen = Idle(
        data = Screen.Map(
            casesByCountry = total.countryCases,
            total = total
        )
    )
)

private fun List<Country>.asSelectableCountries(): List<SelectableCountry> =
    listOf(SelectableCountry.None) + map { SelectableCountry.Some(it) }