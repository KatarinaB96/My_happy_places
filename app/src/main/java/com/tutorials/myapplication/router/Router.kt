package com.tutorials.myapplication.router

interface Router {
    fun showHomeFragment()

    fun showAddFragment()

    fun showDetailsFragment(id:Int,title:String,date:String, description:String,image:String,location:String)

    fun routerPopBack()

    fun showQRGenerator()
}