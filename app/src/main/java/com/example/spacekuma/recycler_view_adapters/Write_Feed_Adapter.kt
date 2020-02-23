package com.example.spacekuma.recycler_view_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.spacekuma.*

import com.example.spacekuma.data.Feed_Media_Uri_Model
import com.example.spacekuma.databinding.WriteFeedImageItemBinding
import com.example.spacekuma.databinding.WriteFeedVideoItemBinding
import kotlinx.android.synthetic.main.write_feed_image_item.view.*
import kotlinx.android.synthetic.main.write_feed_video_item.view.*


class Write_Feed_Adapter(val context: Context, val writefeedList: ArrayList<Feed_Media_Uri_Model>, val listener: ItemDragListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemActionListener {

    override fun onItemMoved(from: Int, to: Int) {
//        if (from == to) {
//            return
//        }
//
//        val fromItem = writefeedList.removeAt(from)
//        writefeedList.add(to, fromItem)
//        notifyItemMoved(from, to)
        swapItems(from,to)
    }

    override fun onItemSwiped(position: Int) {
        writefeedList.removeAt(position)
        notifyItemRemoved(position)
    }

    companion object {
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
        const val TYPE_THREE = 3
    }

    inner class Image_ViewHolder(val binding: WriteFeedImageItemBinding, listener: ItemDragListener) : RecyclerView.ViewHolder(binding.root) {

        fun bind(writefeedMediaUriModel: Feed_Media_Uri_Model, context: Context) {
            binding.setVariable(BR.imagetype,writefeedMediaUriModel)

            binding.root.Btn_Swap.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(this)
                }
                false
            }

            GlideApp.with(context)
                .load(writefeedMediaUriModel.Feed_Media_Uri)
                .transition(withCrossFade())
                .centerCrop()
                .into(binding.root.Feed_Image)
        }
    }

    inner class Video_ViewHolder(val binding: WriteFeedVideoItemBinding,listener: ItemDragListener) : RecyclerView.ViewHolder(binding.root) {

        fun bind(writefeedMediaUriModel: Feed_Media_Uri_Model, context: Context) {
            binding.setVariable(BR.videotype,writefeedMediaUriModel)

            binding.root.Btn_Swap2.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(this)
                }
                false
            }

            GlideApp.with(context)
                .load(writefeedMediaUriModel.Feed_Media_Uri)
                .transition(withCrossFade())
                .centerCrop()
                .into(binding.root.VideoThumnail)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ONE -> {
                Image_ViewHolder(
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.write_feed_image_item, parent, false),listener
                )
            }
            TYPE_TWO -> {
                Video_ViewHolder(
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.write_feed_video_item, parent, false),listener
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Image_ViewHolder -> {
                holder.bind(writefeedList[position],context)
            }
            is Video_ViewHolder -> {
                holder.bind(writefeedList[position],context)
            }
        }

    }

    override fun getItemCount(): Int {
        return writefeedList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (writefeedList[position].View_Type) {
            1 -> TYPE_ONE
            2 -> TYPE_TWO
            3 -> TYPE_THREE
            else -> TYPE_ONE
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                writefeedList[i] = writefeedList.set(i+1, writefeedList[i])
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                writefeedList[i] = writefeedList.set(i-1, writefeedList[i])
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }


}