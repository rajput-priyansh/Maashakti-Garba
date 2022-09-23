package com.vibs.maashakti.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vibs.maashakti.api.models.ParticipantData
import com.vibs.maashakti.databinding.ItemPlayerPassBinding
import com.vibs.maashakti.utils.ViewExtensions.capitalizeWords

interface PassCardListener {
    fun onDelete(item: ParticipantData)
    fun onDetail(item: ParticipantData)
}

open class PassCardAdapter(private val listener: PassCardListener) :
    RecyclerView.Adapter<PassCardAdapter.DataViewHolder>(), Filterable {
    
    var photosList: ArrayList<ParticipantData> = ArrayList()
    var photosListFiltered: ArrayList<ParticipantData> = ArrayList()

    var onItemClick: ((ParticipantData) -> Unit)? = null

    inner class DataViewHolder(private val itemViewBinding: ItemPlayerPassBinding) : RecyclerView.ViewHolder(itemViewBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(photosListFiltered[adapterPosition])
            }
            itemViewBinding.ivDelete.setOnClickListener {
                listener.onDelete(photosListFiltered[adapterPosition])
            }

            itemViewBinding.root.setOnClickListener {
                listener.onDetail(photosListFiltered[adapterPosition])
            }
        }

        fun bind(result: ParticipantData) {
            itemViewBinding.tvName.text = (result.userName ?: "").capitalizeWords()
            itemViewBinding.tvPhone.text = result.userContactNo ?: ""
            if (result.giftBy.isNullOrEmpty()) {

                itemViewBinding.tvGiftBy.visibility = View.GONE
            } else {
                itemViewBinding.tvGiftBy.visibility = View.VISIBLE
                itemViewBinding.tvGiftBy.text = result.passType
            }

            result.userPhotoUrl?.let { url ->

                Glide.with(itemViewBinding.ivProfileImage.context).load(url).into(itemViewBinding.ivProfileImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemBinding = ItemPlayerPassBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(itemBinding)
    }
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(photosListFiltered[position])
    }

    override fun getItemCount(): Int = photosListFiltered.size

    fun addData(list: List<ParticipantData>) {
        photosList = list as ArrayList<ParticipantData>
        photosListFiltered = photosList
        notifyDataSetChanged()
    }
        
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                photosListFiltered = if (charString.isEmpty()) photosList else {
                    val filteredList = ArrayList<ParticipantData>()
                    photosList
                        .filter {
                            (it.userName?.lowercase()?.contains(constraint!!) == true) ||
                                    (it.userContactNo?.contains(constraint!!) == true)

                        }
                        .forEach { filteredList.add(it) }
                    filteredList

                }
                return FilterResults().apply { values = photosListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                photosListFiltered = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<ParticipantData>
                notifyDataSetChanged()
            }
        }
    }
}