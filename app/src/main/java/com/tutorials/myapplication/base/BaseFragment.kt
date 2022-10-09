package com.tutorials.myapplication.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tutorials.myapplication.ComponentFactory
import com.tutorials.myapplication.di.fragment.FragmentComponent


abstract class BaseFragment : Fragment() {
    private var fragmentComponent: FragmentComponent? = null


    fun getDaggerActivity() = activity as BaseActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(getFragmentComponent())
    }

    protected abstract fun inject(fragmentComponent: FragmentComponent)

    fun getFragmentComponent(): FragmentComponent {
        if (fragmentComponent == null) {
            fragmentComponent =
                ComponentFactory.createFragmentComponent(
                    this,
                    getDaggerActivity().getActivityComponent()
                )
        }

        return fragmentComponent as FragmentComponent
    }


}