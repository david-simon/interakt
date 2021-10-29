package xyz.davidsimon.interakt.field

import xyz.davidsimon.interakt.PromptResult

interface StatefulField<T, R: StatefulField.RenderState>: PromptField<T> {
    interface RenderState {
        var isSubmitted: Boolean
    }

    fun onSubmit(state: R) {
        state.isSubmitted = true
    }

    fun initState(pr: PromptResult): R
}