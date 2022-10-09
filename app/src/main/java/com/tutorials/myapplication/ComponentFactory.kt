package com.tutorials.myapplication

import com.tutorials.myapplication.base.BaseActivity
import com.tutorials.myapplication.base.BaseFragment
import com.tutorials.myapplication.base.TemplateApplication
import com.tutorials.myapplication.di.activity.ActivityComponent
import com.tutorials.myapplication.di.application.ApplicationComponent
import com.tutorials.myapplication.di.fragment.FragmentComponent


object ComponentFactory {
    fun createApplicationComponent(templateApplication: TemplateApplication): ApplicationComponent {
        return ApplicationComponent.Initializer.init(templateApplication)
    }

    fun createActivityComponent(baseActivity: BaseActivity, applicationComponent: ApplicationComponent): ActivityComponent {
        return ActivityComponent.Initializer.init(baseActivity, applicationComponent)
    }

    fun createFragmentComponent(baseFragment: BaseFragment, activityComponent: ActivityComponent): FragmentComponent {
        return FragmentComponent.Initializer.init(baseFragment,activityComponent)
    }
}