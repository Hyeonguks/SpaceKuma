package com.example.spacekuma.Test

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.data.Feed_Media_Uri_Model
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.test_page_item.view.*

// PagerAdapter 상속
class ViewPagerAdapter(val media_List : ArrayList<Feed_Media_Uri_Model>,val context: Context) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.test_page_item, container, false)
        container.addView(view)

        if (media_List[position].View_Type == 1) {
            view.ImageView_Feed.visibility = View.VISIBLE
            view.exoPlayerView.visibility = View.GONE
            GlideApp.with(context)
                .load(context.getString(R.string.address)+media_List[position].FileName)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.ImageView_Feed)
        } else if (media_List[position].View_Type == 2) {
            view.exoPlayerView.visibility = View.VISIBLE
            val exoPlayerView = view.findViewById<PlayerView>(R.id.exoPlayerView)
            var player : SimpleExoPlayer? = null

            if (player == null) {
                player = ExoPlayerFactory.newSimpleInstance(context)
                exoPlayerView.player = player
                val defaultHttpDataSourceFactory =
                    DefaultHttpDataSourceFactory(context.getString(R.string.app_name))
                val mediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                    .createMediaSource(Uri.parse(context.getString(R.string.address)+media_List[position].FileName))
                player!!.prepare(mediaSource)
            }
        } else {

        }


        return view
//        return super.instantiateItem(container, position)
    }


    // instantiateItem 오버라이드
    // 페이지 생성과 데이터 바인딩을 모두 맡음
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return media_List.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

}