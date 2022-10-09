package com.tutorials.myapplication.router

import androidx.fragment.app.FragmentManager
import com.tutorials.myapplication.R
import com.tutorials.myapplication.ui.AddPlaceFragment
import com.tutorials.myapplication.ui.DetailsFragment.Companion.createInstance
import com.tutorials.myapplication.ui.HomeFragment
import com.tutorials.myapplication.ui.QRGeneratorFragment

class RouterImpl(private val fragmentManager: FragmentManager) : Router {

    override fun showHomeFragment() {
        val fragmentHome = HomeFragment()
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_main, fragmentHome)
            commit()
        }
    }

    override fun showAddFragment() {
        val fragmentAddPlace = AddPlaceFragment()
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_main, fragmentAddPlace)
            addToBackStack(null)
            commit()
        }
    }
    override fun showDetailsFragment(id:Int,title:String,date:String, description:String,image:String,location:String) {

        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_main,  createInstance(id, title,image,description,location,date))
            setReorderingAllowed(true)
            addToBackStack(null)
            commit()
        }
    }

    override fun routerPopBack() {
        fragmentManager.popBackStack()
    }

    override fun showQRGenerator() {
        val qrGeneratorFragment = QRGeneratorFragment()
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_main, qrGeneratorFragment)
            addToBackStack(null)
            commit()
        }
    }
}