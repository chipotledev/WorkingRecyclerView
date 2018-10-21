package com.kiwiik.business.workingrecyclerview.builders

import com.kiwiik.business.workingrecyclerview.WorkingRecyclerView

/** Represents the data structure for an Empty View
 * @author Savir Guev
 * @author https://github.com/chipotledev
 * @version 1.0
 * @since 1.0
 */
data class RecyclerEmptyView(
        
        /**
         * Title for Empty View
         */
        val title: String?,

        /**
         * Message for Empty View
         */
        val message: String?,

        /**
         * Image Resource for Empty View
         */
        val resource: Int,

        /**
         * Text for the button on Empty View
         */
        val actionText : String?,

        /**
         * Callback to communicate the button actions on Parent View
         */
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