package org.virustrend.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.virustrend.Country
import org.virustrend.CountryCases
import org.virustrend.GlobalTotal
import org.virustrend.domain.ContentQuery.GetAllWorldData
import org.virustrend.domain.VirusTrendEvent.ChangeCountry
import org.virustrend.domain.VirusTrendEvent.Start
import org.virustrend.domain.VirusTrendState.*

@FlowPreview
@ExperimentalCoroutinesApi
class VirusTrendStateMachine {

    private var currentState: VirusTrendState = Loading()
    private val stateChannel: Channel<VirusTrendState> = Channel(Channel.CONFLATED)

    val states: Flow<VirusTrendState>
        get() = stateChannel
            .consumeAsFlow()
            .onEach { currentState = it }
            .onStart { emit(currentState) }

    suspend fun notify(event: VirusTrendEvent) {
        with(stateChannel) {
            when (event) {
                is Start -> {
                    send(Loading(currentState.content))
                    try {
                        val content = Content.query(GetAllWorldData)
                        send(Idle(content))
                    } catch (e: Exception) {
                        send(Failed(e, currentState.content))
                    }
                }
                is ChangeCountry -> {
                    if (currentState.content?.selectedCountry != event.country) {
                        send(currentState.copy {
                            content?.copy(selectedCountry = event.country)
                        })
                    }
                }
            }
        }
    }

}

sealed class VirusTrendEvent {
    object Start : VirusTrendEvent()
    data class ChangeCountry(val country: SelectableCountry) : VirusTrendEvent()
}

sealed class VirusTrendState {

    abstract val content: Content?

    data class Loading(override val content: Content? = null) : VirusTrendState()
    data class Idle(override val content: Content?) : VirusTrendState()
    data class Failed(val error: Exception, override val content: Content?) : VirusTrendState()

    fun copy(block: VirusTrendState.() -> Content?): VirusTrendState = when (this) {
        is Loading -> copy(content = block())
        is Idle -> copy(content = block())
        is Failed -> copy(content = block())
    }

}

data class Content(
    val selectedCountry: SelectableCountry,
    val countries: List<SelectableCountry>,
    val casesByCountry: List<CountryCases>,
    val total: GlobalTotal
) {

    companion object

}

sealed class SelectableCountry {

    object None : SelectableCountry()
    data class Some(val country: Country) : SelectableCountry()

}