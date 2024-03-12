package com.elno.wedding.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elno.wedding.R
import com.elno.wedding.common.UtilityFunctions.dpToPx
import com.elno.wedding.common.UtilityFunctions.getLocalizedTextFromMap
import com.elno.wedding.common.UtilityFunctions.getScreenWidth
import com.elno.wedding.domain.model.CategoryModel
import com.elno.wedding.domain.model.VendorModel


class CategoryAdapter(
    private val isHorizontal: Boolean,
    private val context: Context,
    private val onClick: (categoryModel: CategoryModel?) -> Unit,
    private val onEmptyResult: (isEmpty: Boolean) -> Unit
): RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    private val list = mutableListOf<CategoryModel?>()
    private var filteredList = mutableListOf<CategoryModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: ArrayList<CategoryModel?>) {
        list.clear()
        list.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_category, parent, false)
        if(isHorizontal) view.layoutParams.width = ((getScreenWidth() - dpToPx(parent.context, 20))/ 3.5).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.bind(item, onClick)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        private val categoryName: TextView = view.findViewById(R.id.categoryName)
        private val cardView: CardView = view.findViewById(R.id.cardView)

        fun bind(item: CategoryModel?, onClick: (categoryModel: CategoryModel?) -> Unit) {
            item?.icon.let {
                Glide.with(itemView.context)
                    .load(it)
                    .into(imageView)
            }
            categoryName.text = getLocalizedTextFromMap(itemView.context, item?.name)
            cardView.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredList = if (charSearch.isEmpty()) { list }
                else {
                    val resultList = ArrayList<CategoryModel?>()
                    list.forEach{
                        if (getLocalizedTextFromMap(context, it?.name).lowercase().contains(charSearch.lowercase())) {
                            resultList.add(it)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<CategoryModel?>
                onEmptyResult(filteredList.isEmpty())
                notifyDataSetChanged()
            }
        }
    }


}