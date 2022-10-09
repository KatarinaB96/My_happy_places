package com.tutorials.myapplication.di.activity



import com.tutorials.myapplication.base.BaseActivity
import com.tutorials.myapplication.di.activity.module.ActivityModule
import com.tutorials.myapplication.di.application.ApplicationComponent
import com.tutorials.myapplication.di.scopes.ActivityScope
import com.tutorials.myapplication.router.RouterModule
import dagger.Component


@ActivityScope
@Component(
    dependencies = [(ApplicationComponent::class)],
    modules = [
        ActivityModule::class,
        RouterModule::class
    ]
)

interface ActivityComponent : ActivityComponentInjects, ActivityComponentExposes {

    object Initializer {
        fun init(
            baseActivity: BaseActivity,
            applicationComponent: ApplicationComponent
        ): ActivityComponent =
            DaggerActivityComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(ActivityModule(baseActivity))
//                .routerModule(RouterModule(baseActivity))
//                .activityViewModelModule(ActivityViewModelModule(baseActivity))
                .build()
    }
}