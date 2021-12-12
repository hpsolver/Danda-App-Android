package com.sammyekaran.danda.model.tagSuggestion

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TagSuggestion {
    @SerializedName("response")
    @Expose
    var response: Response? = null

}