package com.tutorials.myapplication


import android.graphics.Color
import android.os.Bundle
import com.tutorials.myapplication.base.BaseActivity
import com.tutorials.myapplication.databinding.ActivityMainBinding
import com.tutorials.myapplication.di.activity.ActivityComponent
import com.tutorials.myapplication.router.Router
import javax.inject.Inject

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        router.showHomeFragment()

//        val toolbar = binding.toolbar
//        toolbar.setTitleTextColor(Color.BLACK)
//        setSupportActionBar(toolbar)
    }

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }


}