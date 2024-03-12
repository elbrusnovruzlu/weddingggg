package com.elno.wedding.presentation.search

import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.common.Static
import com.elno.wedding.databinding.FragmentCategoriesBinding
import com.elno.wedding.domain.model.CategoryModel
import com.elno.wedding.presentation.adapter.CategoryAdapter
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>(FragmentCategoriesBinding::inflate), SearchView.OnQueryTextListener {

    private var adapter: CategoryAdapter? = null

    override fun setupViews() {
        adapter = CategoryAdapter(
            false,
            requireContext(),
            { onCategoryClick(it) },
            { onEmptyResult(it) },
        )
        binding.gridView.adapter = adapter
        binding.gridView.layoutManager = GridLayoutManager(context, 2)
        Static.categories?.let { adapter?.submitList(it) }
    }

    override fun setupListeners() {
        binding.favourite.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }
        binding.searchView.setOnQueryTextListener(this)
    }

    private fun onCategoryClick(model: CategoryModel?) {
        findNavController().navigate(R.id.searchFragment, bundleOf(Constants.CATEGORY_TYPE to model?.type))
    }

    private fun onEmptyResult(isEmpty: Boolean) {
        binding.emptyLayout.isVisible = isEmpty
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter?.filter?.filter(newText)
        return false
    }

    override fun onDestroyView() {
        binding.searchView.setOnQueryTextListener(null)
        super.onDestroyView()
    }
}