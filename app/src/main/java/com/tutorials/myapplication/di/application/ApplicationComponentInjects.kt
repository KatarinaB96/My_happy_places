package com.tutorials.myapplication.di.application

import com.tutorials.myapplication.base.TemplateApplication


interface ApplicationComponentInjects {
    fun inject(application: TemplateApplication)
}