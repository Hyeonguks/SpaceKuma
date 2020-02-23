package com.example.spacekuma

import androidx.recyclerview.widget.RecyclerView

interface ItemDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}