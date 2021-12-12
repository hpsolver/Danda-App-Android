package com.sammyekaran.danda.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryBeanList {

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    inner class Datum {

        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("dial_code")
        @Expose
        var dialCode: String? = null
        @SerializedName("code")
        @Expose
        var code: String? = null

    }
}