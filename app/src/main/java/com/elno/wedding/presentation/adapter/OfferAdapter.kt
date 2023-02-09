package com.elno.wedding.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elno.wedding.R
import com.elno.wedding.common.Constants.FAVOURITE_LIST
import com.elno.wedding.common.UtilityFunctions
import com.elno.wedding.common.UtilityFunctions.getScreenWidth
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.domain.model.VendorModel


class OfferAdapter(private val onClick: (categoryModel: VendorModel?) -> Unit): RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    private val list = arrayListOf<VendorModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: ArrayList<VendorModel?>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_offer, parent, false)
        view.layoutParams.width = (getScreenWidth() / 2.5f).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, onClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        private val name: TextView = view.findViewById(R.id.name)
        private val price: TextView = view.findViewById(R.id.price)
        private val type: TextView = view.findViewById(R.id.type)
        private val favButton: ToggleButton = view.findViewById(R.id.favButton)
        private val cardView: CardView = view.findViewById(R.id.cardView)

        fun bind(item: VendorModel?, onClick: (categoryModel: VendorModel?) -> Unit) {
            item?.imageUrl.let {
                Glide.with(itemView.context)
                    .load(it)
                    .into(imageView)
            }
            name.text = item?.title
            type.text = itemView.context.getString(UtilityFunctions.getType(item?.type))
            price.text = itemView.context.getString(R.string.price_starts_at, item?.minPrice.toString())
            favButton.isChecked = LocalDataStore(itemView.context).getList<VendorModel>(FAVOURITE_LIST).contains(item) == true
            cardView.setOnClickListener {
                onClick(item)
            }
            favButton.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    LocalDataStore(itemView.context).addToList(item, FAVOURITE_LIST)
                }
                else {
                    LocalDataStore(itemView.context).removeFromList(item, FAVOURITE_LIST)
                }
            }
        }

    }


}