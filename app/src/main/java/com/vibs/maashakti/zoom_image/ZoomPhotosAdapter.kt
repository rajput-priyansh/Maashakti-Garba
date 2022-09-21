package com.vibs.maashakti.zoom_image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vibs.maashakti.R

class ZoomPhotosAdapter(
    private val context: Context,
    private var list: ArrayList<String>
) : RecyclerView.Adapter<ZoomPhotosAdapter.RVHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVHolder {
        return RVHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_zoom_photo, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class RVHolder(val binding: View) : RecyclerView.ViewHolder(binding) {
        fun bind(dataItem: String) {
            Glide.with(context).load(dataItem).into(binding.findViewById(R.id.zivPhoto))
        }
    }
}