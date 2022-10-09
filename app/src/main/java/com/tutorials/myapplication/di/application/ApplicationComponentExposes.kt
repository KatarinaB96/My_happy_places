package com.tutorials.myapplication.di.application

import com.tutorials.myapplication.di.application.module.ApplicationModule
import com.tutorials.myapplication.di.application.module.DataModule

interface ApplicationComponentExposes : ApplicationModule.Exposes,
    DataModule.Exposes
//    , MapperModule.Exposes

