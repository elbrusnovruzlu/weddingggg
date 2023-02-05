package com.elno.wedding.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.elno.wedding.R
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.domain.model.VendorModel

class VendorAdapter(
    private val context: Context,
    private val onClick: (item: VendorModel?) -> Unit
) : BaseAdapter() {

    private val vendorList: MutableList<VendorModel?> = arrayListOf()

    fun setData(list: MutableList<VendorModel?>) {
        vendorList.clear()
        vendorList.addAll(list)
        notifyDataSetChanged()
    }

    // below method is use to return the count of course list
    override fun getCount(): Int {
        return vendorList.size
    }

    // below function is use to return the item of grid view.
    override fun getItem(position: Int): VendorModel? {
        return vendorList[position]
    }

    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return 0
    }

    // in below function we are getting individual item of grid view.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: layoutInflater.inflate(R.layout.item_offer, null)


        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val name = view.findViewById<TextView>(R.id.name)
        val price = view.findViewById<TextView>(R.id.price)
        val type = view.findViewById<TextView>(R.id.type)
        val favButton = view.findViewById<ToggleButton>(R.id.favButton)
        val cardView = view.findViewById<CardView>(R.id.cardView)

        val item = getItem(position)

        item?.imageUrl.let {
            Glide.with(context)
                .load(it)
                .into(imageView)
        }
        name.text = item?.title
        type.text = item?.type
        price.text = context.getString(R.string.price_starts_at, item?.minPrice.toString())
        favButton.isChecked = LocalDataStore(context).getList().contains(item) == true
        cardView.setOnClickListener {
            onClick(item)
        }
        favButton.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                LocalDataStore(context).addToList(item)
            }
            else {
                LocalDataStore(context).removeFromList(item)
            }
        }
        return view
    }
}