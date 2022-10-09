package com.tutorials.myapplication.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tutorials.myapplication.ComponentFactory
import com.tutorials.myapplication.di.activity.ActivityComponent


abstract class BaseActivity : AppCompatActivity() {
    private var activityComponent: ActivityComponent? = null

    private fun getTemplateApplication() = TemplateApplication.from(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(getActivityComponent())
    }

    fun getActivityComponent(): ActivityComponent {
        if (activityComponent == null) {
            activityComponent = ComponentFactory.createActivityComponent(
                this,
                getTemplateApplication().getApplicationComponent()
            )
        }

        return activityComponent as ActivityComponent
    }

    protected abstract fun inject(activityComponent: ActivityComponent)

}