package org.virustrend.web.views

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.menu.mMenuItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.css.LinearDimension
import kotlinx.css.margin
import org.virustrend.domain.AppEvent
import org.virustrend.domain.AppState
import org.virustrend.domain.CountrySelection
import react.RBuilder
import styled.css

@ExperimentalCoroutinesApi
internal fun RBuilder.toolbar(state: AppState): Flow<AppEvent.ChangeCountry> {
    val channel = Channel<AppEvent.ChangeCountry>(Channel.CONFLATED)
    mAppBar(position = MAppBarPosition.relative, color = MColor.default) {
        mToolbar {
            state.countries.maybeData?.also { countries ->
                mSelect(
                    value = state.countrySelection.code,
                    onChange = { event, _ ->
                        val code = event.target.asDynamic().value.toString()
                        println("selected event: ${JSON.stringify(event.target)}")
                        val selectedCountry = countries.firstOrNull { it.code == code } ?: CountrySelection.All
                        channel.offer(AppEvent.ChangeCountry(selectedCountry))
                    }
                ) {
                    css {
                        margin(LinearDimension.auto)
                    }
                    attrs.asDynamic().disableUnderline = true
                    countries.forEach { mMenuItem(value = it.code) { +it.label } }
                }
            }
        }
    }
    return channel.receiveAsFlow()
}

private val CountrySelection.label: String
    get() = if (this is CountrySelection.Target) country.countryName else "All Countries"

private val CountrySelection.code: String
    get() = if (this is CountrySelection.Target) country.code else "ALL"