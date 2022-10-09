package com.tutorials.myapplication.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tutorials.myapplication.R
import com.tutorials.myapplication.databinding.PlaceItemBinding

class PlaceAdapter(val clickListener: (place: PlaceScreenModel) -> (Unit)) :
    androidx.recyclerview.widget.ListAdapter<PlaceScreenModel, PlaceAdapter.PlaceViewHolder>(PlacesListItemDiffUtilCallBack()) {

    inner class PlaceViewHolder(private val binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(placeScreenModel: PlaceScreenModel) {
            if (placeScreenModel.image.isNullOrEmpty()) {
                binding.imageOfPlace.setImageResource(R.drawable.ic_baseline_image_24)
            } else {
                binding.imageOfPlace.setImageURI(Uri.parse(placeScreenModel.image))
            }

            binding.titleOfPlace.text = placeScreenModel.title

        }
    }

    class PlacesListItemDiffUtilCallBack : DiffUtil.ItemCallback<PlaceScreenModel>() {
        override fun areItemsTheSame(oldItem: PlaceScreenModel, newItem: PlaceScreenModel): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PlaceScreenModel, newItem: PlaceScreenModel): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(
            PlaceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val placeScreenModel = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener(placeScreenModel)
        }
        holder.setItem(getItem(position))
    }
}