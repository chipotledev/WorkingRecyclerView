package com.kiwiik.business.workingrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.widget.FrameLayout
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IntDef
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiwiik.business.workingrecyclerview.builders.RecyclerEmptyView
import com.kiwiik.business.workingrecyclerview.databinding.WorkingRecyclerViewBinding
import com.kiwiik.business.workingrecyclerview.utils.EndlessRecyclerViewScrollListener

class WorkingRecyclerView : FrameLayout {

    private val LOG_TAG = WorkingRecyclerView::class.java.simpleName

    private lateinit var workingRecyclerBinding: WorkingRecyclerViewBinding
    private var emptyViews = SparseArray<RecyclerEmptyView>()

    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var infiniteScrollCallback : InfiniteScrollCallback
    private var itemsPerPage : Int = 0

    companion object {

        /**
         * STATE LOADING DATA
         */
        const val STATE_LOADING = 1

        /**
         * STATE ERROR LOADING DATA
         */
        const val STATE_ERROR = 2

        /**
         * STATE EMPTY DATA
         */
        const val STATE_EMPTY = 3

        /**
         * STATE LOADED DATA
         */
        const val STATE_LOADED = 4
    }

    // Store a member variable for the listener
    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    @IntDef(STATE_LOADING, STATE_ERROR, STATE_EMPTY, STATE_LOADED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RecyclerState

    // State references
    var currentState = STATE_EMPTY


    /**
     * Base constructor for the view
     */
    constructor(context: Context) : super(context) {

        // We prepare the recycler view
        if (!isInEditMode) {
            setUp(context)
        }
    }

    /**
     * Style set constructor
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // We prepare the recycler view
        if (!isInEditMode) {
            setUp(context)
        }
    }

    /**
     * Style set constructor
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {

        // We prepare the recycler view
        if (!isInEditMode) {
            setUp(context)
        }
    }

    /**
     * Setting up the layout contents
     */
    private fun setUp(context: Context) {
        // We inflate the layout setup
        val layoutInflater = LayoutInflater.from(context)

        // Setup of the view
        workingRecyclerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.working_recycler_view,
                null /* parent */, true /* attachToRoot */)
        workingRecyclerBinding.presenter = this@WorkingRecyclerView

        // Adding the view to the layout
        addView(workingRecyclerBinding.root)

    }

    fun registerEmptyMessage(id: Int, recyclerEmptyView: RecyclerEmptyView) {
        this.emptyViews.put(id, recyclerEmptyView)
    }

    fun activateInfiniteScroll(linearLayoutManager: LinearLayoutManager, infiniteScrollCallback: InfiniteScrollCallback, itemsPerPage: Int) {

        //retain values
        this.linearLayoutManager = linearLayoutManager
        this.infiniteScrollCallback = infiniteScrollCallback
        this.itemsPerPage = itemsPerPage

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager, itemsPerPage) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                infiniteScrollCallback.loadMore(page)
            }
        }
        // Adds the scroll listener to RecyclerView
        if(scrollListener != null) {
            this.workingRecyclerBinding.workingRecycler.addOnScrollListener(scrollListener!!)
        }
    }

    /**
     * Updates the current state of the recycler view
     */
    fun updateRecyclerState(@RecyclerState state: Int) {
        // Updating the current reference
        currentState = state
        workingRecyclerBinding.state = currentState
    }

    /**
     * Shows an empty message
     * @param id : The identifier of the empty view
     *
     * @throws IllegalArgumentException if the id has not been registered before
     */
    fun showEmptyMessage(id: Int){

        //Check if the tag has been registered
        if(this.emptyViews.indexOfKey(id) < 0){
            throw IllegalArgumentException("The id $id has not been registered.")
        }

        currentState = STATE_EMPTY
        workingRecyclerBinding.state = currentState
        workingRecyclerBinding.currentId = id

        // Updating the image visibility according to the resource value
        workingRecyclerBinding.errorTitle = emptyViews.get(id).title
        workingRecyclerBinding.errorMessage = emptyViews.get(id).message
        workingRecyclerBinding.errorResource = emptyViews.get(id).resource
        workingRecyclerBinding.btnRetry.text = emptyViews.get(id).actionText
    }


    /**
     * Setting the image for the title state
     */
    fun setErrorResource(resource: Int) {
        workingRecyclerBinding.errorResource = resource
    }

    /**
     * Adds an item decorator for the recycler view
     */
    fun addItemDecorator(itemDecorator: RecyclerView.ItemDecoration) {
        workingRecyclerBinding.workingRecycler.addItemDecoration(itemDecorator)
    }

    /**
     * Obtains the number of spans that can be hold in this recycler view
     * This is method is to be used for the set of the GridLayoutManager and
     * only considers the width of the child objects
     */
    fun getMaxSpanCount(childWidth: Int, childPadding: Int, recyclerWidth: Int): Int {
        val itemWidth = childWidth + 2 * childPadding // Getting the actual width of an object

        // We just divide to the possible capacity of items
        var spanCount = 1
        var itemsWidth = itemWidth

        // We iterate till we fill the recycler view
        while (itemsWidth < recyclerWidth) {
            if (itemsWidth + itemWidth <= recyclerWidth) {
                Log.d(LOG_TAG, "Adding item")

                itemsWidth += itemWidth
                spanCount++
            } else {
                break
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Width of the recycler view: $recyclerWidth")
            Log.d(LOG_TAG, "Number of spans: $spanCount")
            Log.d(LOG_TAG, "Width of a single item: $itemWidth")
        }

        return spanCount
    }

    fun refreshToTop(){
        this.getRecyclerView().smoothScrollToPosition(0)
        if(scrollListener == null) {
            this.activateInfiniteScroll(linearLayoutManager,infiniteScrollCallback,itemsPerPage)
        }
        this.scrollListener?.resetState()
    }

    /**
     * Setting the adapter for the main recycler view
     */
    fun setAdapter(adapter: RecyclerView.Adapter<*>?, layoutManager: RecyclerView.LayoutManager) {

        updateRecyclerState(WorkingRecyclerView.STATE_LOADED)

        // Setting the related objects
        workingRecyclerBinding.workingRecycler.layoutManager = layoutManager
        if (adapter != null) {
            workingRecyclerBinding.workingRecycler.adapter = adapter
        }
    }

    /**
     * Obtains the recycler instance of the working recycler
     */
    fun getRecyclerView(): RecyclerView {
        return workingRecyclerBinding.workingRecycler
    }

    fun actionClicked(id: Int){
        this.emptyViews.get(id).callback?.onEmptyActionClicked(id)
    }

    /**
     * Recycler callback for the load more (Infinite Scroll)
     */
    interface InfiniteScrollCallback {
        fun loadMore(page: Int)
    }

    interface EmptyButtonCallback{
        fun onEmptyActionClicked(id: Int)
    }


}