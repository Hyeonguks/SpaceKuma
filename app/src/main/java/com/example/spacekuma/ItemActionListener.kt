package com.example.spacekuma

interface ItemActionListener {
    fun onItemMoved(from: Int, to: Int)
    fun onItemSwiped(position: Int)
}