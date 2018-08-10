package com.kiwiik.business.workingrecyclerview.builders

import com.kiwiik.business.workingrecyclerview.WorkingRecyclerView

data class RecyclerEmptyView(
        val title: String?,
        val message: String?,
        val resource: Int,
        val actionText : String?,
        val callback : WorkingRecyclerView.EmptyButtonCallback?
){

    class Builder {

        var title: String? = null
            private set

        var message: String? = null
            private set

        var resource: Int = 0
            private set

        var actionText: String? = null
            private set

        var callback: WorkingRecyclerView.EmptyButtonCallback? = null
            private set

        fun title(title: String?) = apply { this.title = title}

        fun message(message: String?) = apply { this.message = message }

        fun resource(resource: Int) = apply { this.resource = resource }

        fun actionText(actionText: String?) = apply { this.actionText = actionText }

        fun callback(callback: WorkingRecyclerView.EmptyButtonCallback?) = apply { this.callback = callback}

        fun build() = RecyclerEmptyView(
                title,
                message,
                resource,
                actionText,
                callback
        )
    }
}