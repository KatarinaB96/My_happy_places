package com.tutorials.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tutorials.myapplication.R
import com.tutorials.myapplication.base.BaseFragment
import com.tutorials.myapplication.databinding.FragmentHomeBinding
import com.tutorials.myapplication.di.fragment.FragmentComponent
import com.tutorials.myapplication.router.Router
import com.tutorials.myapplication.ui.viewmodel.PlaceViewModel
import javax.inject.Inject

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var placeViewModel: PlaceViewModel

    private var placeAdapter: PlaceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeViewModel.getPlaces()


        initAdapter()
        observeData()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.addPlace -> router.showAddFragment()
                R.id.qrGenerator -> router.showQRGenerator()
            }
            true
        }
    }

    private fun onItemClicked(placeScreenModel: PlaceScreenModel) {
        router.showDetailsFragment(
            placeScreenModel.id,
            placeScreenModel.title,
            placeScreenModel.date,
            placeScreenModel.description,
            placeScreenModel.image ?: "",
            placeScreenModel.location
        )
    }

    private fun initAdapter() {
        placeAdapter = PlaceAdapter { onItemClicked(it) }
        binding.placesList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.placesList.adapter = placeAdapter
    }

    private fun observeData() {
        placeViewModel.places.observe(requireActivity()) {
            placeAdapter?.submitList(it)
            if (it.isNotEmpty()) {
                binding.noPlaces.visibility = View.GONE
            } else {
                binding.noPlaces.visibility = View.VISIBLE
            }
        }
    }

}