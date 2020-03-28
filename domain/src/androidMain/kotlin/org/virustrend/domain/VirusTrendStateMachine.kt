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
import org.virustrend.domain.ContentRepository.Query.GetAllWorldData
import org.virustrend.domain.VirusTrendEvent.Start
import org.virustrend.domain.VirusTrendState.*

@FlowPreview
@ExperimentalCoroutinesApi
class VirusTrendStateMachine(
    private val repository: ContentRepository = ContentRepository
) {

    var currentState: VirusTrendState = Initial
        private set

    private val stateChannel: Channel<VirusTrendState> = Channel(Channel.CONFLATED)

    val states: Flow<VirusTrendState>
        get() = stateChannel
            .consumeAsFlow()
            .onEach { currentState = it }
            .onStart { emit(currentState) }

    suspend fun notify(event: VirusTrendEvent) = with(stateChannel) {
        when {
            currentState is Initial && event is Start -> {
                send(Loading())
                try {
                    val content = repository.query(GetAllWorldData)
                    send(Idle(content))
                } catch (e: Exception) {
                    send(Failed(e, currentState.content))
                }
            }
        }
    }

}

sealed class VirusTrendEvent {
    object Start : VirusTrendEvent()
}

sealed class VirusTrendState {

    abstract val content: Content?

    object Initial : VirusTrendState() {
        override val content: Content? = null
    }

    data class Loading(override val content: Content? = null) : VirusTrendState()
    data class Idle(override val content: Content?) : VirusTrendState()
    data class Failed(val error: Exception, override val content: Content?) : VirusTrendState()


    fun withContent(content: Content?): VirusTrendState = when (this) {
        is Initial -> Initial
        is Loading -> copy(content = content)
        is Idle -> copy(content = content)
        is Failed -> copy(content = content)
    }

}

data class Content(
    val countries: List<Country>,
    val casesByCountry: List<CountryCases>,
    val total: GlobalTotal
)