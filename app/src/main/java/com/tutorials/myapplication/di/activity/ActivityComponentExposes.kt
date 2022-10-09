package com.tutorials.myapplication.di.activity

import com.tutorials.myapplication.di.activity.module.ActivityModule
import com.tutorials.myapplication.di.application.ApplicationComponentExposes
import com.tutorials.myapplication.router.RouterModule
import okhttp3.Route

interface ActivityComponentExposes : ActivityModule.Exposes, ApplicationComponentExposes, RouterModule.Exposes
