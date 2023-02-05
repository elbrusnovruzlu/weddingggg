package com.elno.wedding.data.local

import android.content.Context
import android.content.SharedPreferences
import com.elno.wedding.domain.model.VendorModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class LocalDataStore(context: Context?) {


    private val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("dataFile", Context.MODE_PRIVATE)

    private val editor = sharedPreferences?.edit()

    fun addToList(item: VendorModel?) {
        val currentList = getList()
        currentList.add(item)
        setList(currentList)
    }

    fun removeFromList(item: VendorModel?) {
        val currentList = getList()
        currentList.remove(item)
        setList(currentList)
    }

    private fun setList(list: List<VendorModel?>) {
        val gson = Gson()
        val json = gson.toJson(list)
        editor?.putString("myList", json)
        editor?.commit()
    }

    fun getList(): MutableList<VendorModel?> {
        var arrayItems: List<VendorModel> = listOf()
        val serializedObject = sharedPreferences?.getString("myList", null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<VendorModel?>?>() {}.type
            arrayItems = gson.fromJson(serializedObject, type)
        }
        return arrayItems.toMutableList()
    }
}