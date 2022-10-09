package com.tutorials.myapplication.di.fragment


import com.tutorials.myapplication.di.fragment.module.FragmentModule
import com.tutorials.myapplication.di.activity.ActivityComponentExposes
import com.tutorials.myapplication.di.fragment.module.FragmentViewModelModule


interface FragmentComponentExposes : FragmentModule.Exposes, ActivityComponentExposes, FragmentViewModelModule.Exposes
//    , FragmentMapperModule.Exposes