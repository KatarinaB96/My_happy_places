package com.tutorials.myapplication.base

import android.app.Application
import android.content.Context
import com.tutorials.myapplication.ComponentFactory
import com.tutorials.myapplication.di.application.ApplicationComponent


class TemplateApplication : Application() {
    private lateinit var applicationComponent: ApplicationComponent


    companion object {
        fun from(context: Context): TemplateApplication {
            return context.applicationContext as TemplateApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = ComponentFactory.createApplicationComponent(this)
        applicationComponent.inject(this)
    }

    fun getApplicationComponent() = applicationComponent

}