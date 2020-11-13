package com.kodovad.bytechute.model.bundle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kodovad.bytechute.model.Channel
import com.kodovad.bytechute.model.Tag
import com.kodovad.bytechute.model.Video
import com.kodovad.bytechute.response.ExtendResponse
import com.kodovad.bytechute.response.HomeResponse

data class HomeBundle(

        var category: String? = null,

        //  data
        val popularVideos: MutableLiveData<List<Video>> = MutableLiveData(),
        val popularChannels: MutableLiveData<List<Channel>> = MutableLiveData(),
        val subscribedVideos: MutableLiveData<List<Video>> = MutableLiveData(),
        val trendingDayVideos: MutableLiveData<List<Video>> = MutableLiveData(),
        val trendingWeekVideos: MutableLiveData<List<Video>> = MutableLiveData(),
        val trendingMonthVideos: MutableLiveData<List<Video>> = MutableLiveData(),
        val trendingTags: MutableLiveData<List<Tag>> = MutableLiveData(),
        val allVideos: MutableLiveData<List<Video>> = MutableLiveData(),

        //  progress
        var popularIsUpdating: MutableLiveData<Boolean> = MutableLiveData(),
        var subscribedIsUpdating: MutableLiveData<Boolean> = MutableLiveData(),
        var trendingIsUpdating: MutableLiveData<Boolean> = MutableLiveData(),
        var allIsUpdating: MutableLiveData<Boolean> = MutableLiveData(),

        //  extending
        var allIsFullyExtended: MutableLiveData<Boolean> = MutableLiveData(),
        var popularIsFullyExtended: MutableLiveData<Boolean> = MutableLiveData()
){
    //  getters
    fun getPopularVideos (): LiveData<List<Video>> = popularVideos
    fun getPopularChannels (): LiveData<List<Channel>> = popularChannels
    fun getSubscribedVideos (): LiveData<List<Video>> = subscribedVideos
    fun getTrendingDayVideos (): LiveData<List<Video>> = trendingDayVideos
    fun getTrendingWeekVideos (): LiveData<List<Video>> = trendingWeekVideos
    fun getTrendingMonthVideos (): LiveData<List<Video>> = trendingMonthVideos
    fun getTrendingTags (): LiveData<List<Tag>> = trendingTags
    fun getAllVideos (): LiveData<List<Video>> = allVideos

    fun getPopularIsUpdating() : LiveData<Boolean> = popularIsUpdating
    fun getSubscribedIsUpdating() : LiveData<Boolean> = subscribedIsUpdating
    fun getTrendingIsUpdating() : LiveData<Boolean> = trendingIsUpdating
    fun getAllIsUpdating() : LiveData<Boolean> = allIsFullyExtended

    fun getPopularIsFullyExtended() : LiveData<Boolean> = popularIsFullyExtended
    fun getAllIsFullyExtended() : LiveData<Boolean> = allIsUpdating

    fun getVideoLiveData(tabName: String) : LiveData<List<Video>> = when(tabName){
        "popular" -> getPopularVideos()
        "subscribed" -> getSubscribedVideos()
        "trendingDay" -> getTrendingDayVideos()
        "trendingWeek" -> getTrendingWeekVideos()
        "trendingMonth" -> getTrendingMonthVideos()
        "all" -> getAllVideos()
        else -> TODO("No Related Home Live Data")
    }

    fun getIsUpdating(tabName: String) : Boolean = when(tabName){
        "popular" -> getPopularIsUpdating().value ?: false
        "subscribed" -> getSubscribedIsUpdating().value ?: false
        "trendingDay" -> getTrendingIsUpdating().value ?: false
        "trendingWeek" ->getTrendingIsUpdating().value ?: false
        "trendingMonth" -> getTrendingIsUpdating().value ?: false
        "all" -> getAllIsUpdating().value ?: false
        else -> TODO("No Related Home Live Data")
    }

    fun getIsFullyExtended(tabName: String) : Boolean = when(tabName){
        "popular" -> popularIsFullyExtended.value ?: false
        "all" -> allIsFullyExtended.value ?: false
        else -> TODO("No Related Home Live Data")
    }

    //  setters
    fun setHomeIsUpdating(value: Boolean){
        popularIsUpdating.value = value
        trendingIsUpdating.value = value
        allIsUpdating.value = value
    }

    fun setIsUpdating(tabName: String, value: Boolean) = when(tabName){
        "popular" -> popularIsUpdating.value = value
        "subscribed" -> subscribedIsUpdating.value = value
        "trendingDay" -> trendingIsUpdating.value = value
        "trendingWeek" -> trendingIsUpdating.value = value
        "trendingMonth" -> trendingIsUpdating.value = value
        "all" -> allIsUpdating.value = value
        else -> TODO("No Related Home Live Data")
    }

    fun setHomeBundle(homeResponse: HomeResponse){
        popularVideos.value = homeResponse.popularVideos
        popularChannels.value = homeResponse.popularChannels
        subscribedVideos.value = homeResponse.subscribedVideos
        trendingDayVideos.value = homeResponse.trendingDayVideos
        trendingWeekVideos.value = homeResponse.trendingWeekVideos
        trendingMonthVideos.value = homeResponse.trendingMonthVideos
        trendingTags.value = homeResponse.trendingTags
        allVideos.value = homeResponse.allVideos
    }

    fun extendRelated(tabName: String, extendResponse: ExtendResponse) {
        when(tabName) {
            "popular" -> {
                popularVideos.postValue(getPopularVideos().value?.plus(extendResponse.videos))
                popularIsFullyExtended.postValue(extendResponse.isFullyExtended)
            }
            "all" -> {
                allVideos.postValue(getAllVideos().value?.plus(extendResponse.videos))
                allIsFullyExtended.postValue(extendResponse.isFullyExtended)
            }
        }
    }

    fun resetHomeBundle(categoryChange: String?){
        category = categoryChange
        popularVideos.value = emptyList()
        popularChannels.value = emptyList()
        subscribedVideos.value = emptyList()
        trendingDayVideos.value = emptyList()
        trendingWeekVideos.value = emptyList()
        trendingMonthVideos.value = emptyList()
        trendingTags.value = emptyList()
        allVideos.value = emptyList()
        allIsFullyExtended.value = false
        popularIsFullyExtended.value = false
    }
}