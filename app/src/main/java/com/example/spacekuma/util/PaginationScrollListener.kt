package com.example.spacekuma.util

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean
    abstract fun loadMoreItems()


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        if (totalItemCount -1  == lastVisibleItemPosition) {
            Log.e("onScrolled", "END!")
        }
    }
}