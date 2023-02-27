package com.elno.wedding.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elno.wedding.R
import com.elno.wedding.common.Constants.FAVOURITE_LIST
import com.elno.wedding.common.UtilityFunctions
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.domain.model.VendorModel

class VendorAdapter(
    private val onClick: (item: VendorModel?) -> Unit,
    private val onEmptyResult: (isEmpty: Boolean) -> Unit
) :  RecyclerView.Adapter<VendorAdapter.ViewHolder>(), Filterable {

    private var list = mutableListOf<VendorModel?>()
    private var filterList = mutableListOf<VendorModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: MutableList<VendorModel?>) {
        list = newList
        filterList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_offer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filterList[position]
        holder.bind(item, onClick)
    }

    override fun getItemCount(): Int {
        return filterList.size
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
            type.text =UtilityFunctions.getType( itemView.context, item?.type)
            price.text = itemView.context.getString(R.string.price_starts_at, item?.minPrice.toString())
            favButton.isChecked = LocalDataStore(itemView.context).getList<String>(FAVOURITE_LIST).contains(item?.id) == true
            cardView.setOnClickListener {
                onClick(item)
            }
            favButton.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    LocalDataStore(itemView.context).addToList(item?.id, FAVOURITE_LIST)
                }
                else {
                    LocalDataStore(itemView.context).removeFromList(item?.id, FAVOURITE_LIST)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) { list }
                else {
                    val resultList = ArrayList<VendorModel?>()
                    list.forEach{
                        if (it?.title?.lowercase()?.contains(charSearch.lowercase()) == true) {
                            resultList.add(it)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<VendorModel?>
                onEmptyResult(filterList.isEmpty())
                notifyDataSetChanged()
            }
        }
    }


}