package com.tutorials.myapplication.di.fragment


import com.tutorials.myapplication.di.fragment.module.FragmentModule
import com.tutorials.myapplication.di.scopes.FragmentScope
import com.tutorials.myapplication.base.BaseFragment
import com.tutorials.myapplication.di.activity.ActivityComponent
import com.tutorials.myapplication.di.fragment.module.FragmentMapperModule
import com.tutorials.myapplication.di.fragment.module.FragmentViewModelModule
import dagger.Component

@FragmentScope
@Component(
    dependencies = [ActivityComponent::class],
    modules = [
        FragmentModule::class,
        FragmentViewModelModule::class,
        FragmentMapperModule::class
    ]
)


interface FragmentComponent : FragmentComponentInjects, FragmentComponentExposes {
    object Initializer {
        fun init(
//            baseActivity: BaseActivity,
            baseFragment: BaseFragment,
            activityComponent: ActivityComponent
        ): FragmentComponent =
            DaggerFragmentComponent.builder()
                .activityComponent(activityComponent)
                .fragmentModule(FragmentModule(baseFragment))
//                .fragmentMapperModule(FragmentMapperModule())
                .fragmentViewModelModule(FragmentViewModelModule(baseFragment))
                .build()
    }


}