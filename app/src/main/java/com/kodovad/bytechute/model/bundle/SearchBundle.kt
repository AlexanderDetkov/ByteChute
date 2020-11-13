package com.kodovad.bytechute.model.bundle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kodovad.bytechute.response.SearchResponse

data class SearchBundle (
        var currentSearchQuery: String = "",
        var currentFilter: String? = null,
        var pageNumber: Int = 0,    //  start at 0 as incrementing immediately

        //  data
        var searchResults: MutableLiveData<List<Any>> = MutableLiveData(),

        //  progress
        var searchIsUpdating: MutableLiveData<Boolean> = MutableLiveData(),

        //  extending
        var searchIsFullyExtended: MutableLiveData<Boolean> = MutableLiveData()
){

    //  getters

    fun getSearchResults() : LiveData<List<Any>> = searchResults

    fun getSearchIsUpdating() : LiveData<Boolean> = searchIsUpdating

    fun getSearchIsFullyExtended() : LiveData<Boolean> = searchIsFullyExtended

    //  setters
    fun setSearchResponse(searchResponse: SearchResponse){
        searchResults.postValue(getSearchResults().value?.plus(searchResponse.searchResults))
        searchIsFullyExtended.postValue(searchResponse.isFullyExtended)
    }

    fun resetSearch(newQuery: String, newFilter: String?){
        currentSearchQuery = newQuery
        currentFilter = newFilter
        pageNumber = 0
        searchResults.value = emptyList()
        searchIsUpdating.value = false
        searchIsFullyExtended.value = false
    }

}