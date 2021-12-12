package com.sammyekaran.danda.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.model.CountryBeanList


class CountryListAdapter( countryList: List<CountryBeanList.Datum>,itemClick:ItemClick) :
    RecyclerView.Adapter<CountryListAdapter.ViewHolder>() {


    var countryList: List<CountryBeanList.Datum>
    var  itemClick:ItemClick

    init {
        this.countryList = countryList
        this.itemClick=itemClick
    }

    override fun getItemCount(): Int {
        return countryList!!.size
    }

    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item_country, viewGroup, false))
    }

    override fun onBindViewHolder(@NonNull viewHolder: ViewHolder, i: Int) {
        viewHolder.textViewName.setText(countryList[i].name)
        viewHolder.textViewCode.setText(countryList[i].dialCode)
        viewHolder.itemView.setOnClickListener{itemClick.onItemClick(countryList[i].name!!, countryList[i].dialCode!!,countryList[i].code!!)}
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(temp: List<CountryBeanList.Datum>) {
        this.countryList = temp
        notifyDataSetChanged()

    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var textViewName: TextView
        internal var textViewCode: TextView

        init {
            textViewName = itemView.findViewById(R.id.textViewName)
            textViewCode = itemView.findViewById(R.id.textViewCode)
        }
    }

    interface ItemClick{
        fun onItemClick(countryName:String,countryCode:String,iso:String)
    }
}
