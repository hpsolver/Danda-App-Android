package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentCountryListBinding
import com.sammyekaran.danda.model.CountryBeanList
import com.sammyekaran.danda.view.adapter.CountryListAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_country_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream

class CountryListFragment : BaseFragment<FragmentCountryListBinding>(),CountryListAdapter.ItemClick {

    lateinit var mBinding: FragmentCountryListBinding
    lateinit var countryJsonString: String
    lateinit var itemClick: CountryListAdapter.ItemClick
    var filterList: MutableList<CountryBeanList.Datum> = mutableListOf()
    lateinit var listAdapter: CountryListAdapter
    lateinit var countryList: List<CountryBeanList.Datum>
    lateinit var countryBeanList: CountryBeanList


    override fun getLayoutId(): Int {
        return R.layout.fragment_country_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = getViewDataBinding()
        countryJsonString = readJSONFromAsset()
        itemClick = this
        listner()
        setCountryAdapter()
    }

    private fun listner() {
        editTextSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })

        imageButtonBack?.setOnClickListener({findNavController().popBackStack() })

    }

    private fun setCountryAdapter() {

        var jsonObject: JSONObject? = null
        try {
            val jsonArray = JSONArray(countryJsonString)
            jsonObject = JSONObject()
            jsonObject.put("data", jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        countryBeanList = Gson().fromJson(jsonObject!!.toString(), CountryBeanList::class.java)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        countryList = countryBeanList.data!!
        listAdapter = CountryListAdapter(countryList, itemClick)
        recyclerView.adapter = listAdapter

    }


    fun readJSONFromAsset(): String {
        var json = ""
        try {
            val inputStream: InputStream = resources.openRawResource(com.sammyekaran.danda.R.raw.countrycodes)
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    fun filter(text: String) {
        filterList = ArrayList()
        for (d in countryList) { //or use .equal(text) with you want equal match //use .toLowerCase() for better matches
            if (d.name!!.toLowerCase().contains(text.toLowerCase().trim())) {
                filterList.add(d)
            }
        }
        //update recyclerview
        listAdapter.updateList(filterList)
    }

    override fun onItemClick(countryName:String,countryCode:String,iso:String) {
        RegisterFragment.sCountryCode = countryCode.replace("+", "")
        RegisterFragment.sIso = iso
        RegisterFragment.sCountryCode = countryCode.replace("+", "")

        ForgotPassword.sIso = iso
        ForgotPassword.sCountryCode = countryCode.replace("+", "")

        findNavController().popBackStack()

    }



}