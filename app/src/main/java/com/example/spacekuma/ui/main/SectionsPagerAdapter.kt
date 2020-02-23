package com.example.spacekuma.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.spacekuma.fragments.ChatFragment
import com.example.spacekuma.fragments.HomeFragment
import com.example.spacekuma.fragments.MyPageFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(
    fragm: FragmentManager,
    Num : Int,
    ID : String,
    Name : String,
    Pic : String,
    Date : String,
    Token : String
) : FragmentPagerAdapter(fragm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val homeFragment : HomeFragment = HomeFragment()
    val dashboardFragment : ChatFragment = ChatFragment()
    val myPageFragment: MyPageFragment = MyPageFragment()
    var CurrentFragment :Fragment = homeFragment


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> homeFragment
            1 -> dashboardFragment
            2 -> myPageFragment
            else -> CurrentFragment
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "게시판"
            1 -> "채팅"
            else -> "내정보"
        }
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }
}