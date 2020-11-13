package com.kodovad.bytechute.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.android.volley.*
import com.kodovad.bytechute.model.bundle.HomeBundle
import com.kodovad.bytechute.model.bundle.SearchBundle
import com.kodovad.bytechute.repository.BitChuteRepository

class BitChuteViewModel(application: Application) : AndroidViewModel(application) {
    private val applicationContext = application.applicationContext

    //  BitChute repository singleton
    private val bitChuteRepository: BitChuteRepository by lazy {
        BitChuteRepository.getInstance(applicationContext)
    }

    //  Account Management
    fun login(){

        //  get usr and pwd from shared preference
        val sharedPreferences = applicationContext.getSharedPreferences("creds", Context.MODE_PRIVATE)
        val usr = sharedPreferences.getString("usr", null)
        val pwd = sharedPreferences.getString("pwd", null)

        //  check if has credentials
        if (usr != null && pwd != null)

            //  has credentials
            bitChuteRepository.login(usr, pwd,
                Response.Listener { loginResponse ->


                    if (loginResponse.loginSuccess)
                        //  successful login
                        TODO("Successful Login")
                    else {
                        //  unsuccessful login
                        //  deleting invalid creds
                        val editor = applicationContext.getSharedPreferences("creds", Context.MODE_PRIVATE).edit()
                        editor.remove("usr")
                        editor.remove("pwd")
                        editor.apply()

                        TODO("Notify user of invalid creds")


                    }
                }, Response.ErrorListener { error ->
                    Log.d("here", "Error: ${error.message}")

                    if (error is NetworkError || error is NoConnectionError || error is TimeoutError){
                        TODO("Internet Error In Logging In")
                    }else{
                        TODO("Unknown Error In Logging In")
                    }

                })
        else
            TODO("No Creds")

    }

    //  Home Page
    val homeBundle = HomeBundle()
    fun getHomePage(category: String?){

        //  reset home bundle on category change
        if (category != homeBundle.category)
            homeBundle.resetHomeBundle(category)

        homeBundle.setHomeIsUpdating(true)

        bitChuteRepository.setHomePage(category,
            Response.Listener { homeResponse ->
                homeBundle.setHomeBundle(homeResponse)

                homeBundle.setHomeIsUpdating(false)

                Log.d("here", homeResponse.popularVideos.first().title)

                //FIXME("Set Home Page")

            }, Response.ErrorListener { error ->

                homeBundle.setHomeIsUpdating(false)

                TODO("Home Page Error")
            })
    }
    fun extendHomePage(tabName: String, category: String?){
        val relatedVideos = homeBundle.getVideoLiveData(tabName).value

        //  check if valid for extension
        if (relatedVideos != null && relatedVideos.isNotEmpty()
            && !homeBundle.getIsUpdating(tabName) && !homeBundle.getIsFullyExtended(tabName)) {
            homeBundle.setIsUpdating(tabName, true)

            //TODO(Fix Video ID Formater)
            val videoID = relatedVideos.last().href.replace("video/","").replace("/","")

            bitChuteRepository.extendHomePage(tabName, category, relatedVideos.size, videoID, Response.Listener { extendHomeResponse ->

                //  success extending
                homeBundle.setIsUpdating(tabName, false)
                homeBundle.extendRelated(tabName, extendHomeResponse)

                }, Response.ErrorListener { error ->

                //  failed extending
                homeBundle.setIsUpdating(tabName, false)
                TODO("Handle Error")

                })
        }
    }

    //  Search
    val searchBundle = SearchBundle()
    fun searchQuery(query: String, filter: String?){

        //  reset search bundle on search query change
        if (query != searchBundle.currentSearchQuery || filter != searchBundle.currentFilter) {
            Log.d("here", "reseting")
            searchBundle.resetSearch(query, filter)
        }

        Log.d("here", "${searchBundle.getSearchIsUpdating().value}   ${searchBundle.getSearchIsFullyExtended().value}")
        if (searchBundle.getSearchIsUpdating().value == false && searchBundle.getSearchIsFullyExtended().value == false) {
            //  increment page number
            searchBundle.pageNumber++

            //  set is updating
            searchBundle.searchIsUpdating.value = true

            bitChuteRepository.setSearchResults(query, searchBundle.pageNumber, searchBundle.currentFilter,
                    Response.Listener { searchResponse ->

                        Log.d("here", searchResponse.searchResults.first().toString())

                        //  successful search
                        searchBundle.searchIsUpdating.value = false
                        searchBundle.setSearchResponse(searchResponse)

                        //TODO("Successful Search")

                    },
                    Response.ErrorListener { error ->

                        //  failed search
                        searchBundle.searchIsUpdating.value = false

                        TODO("Search Error: ${error.message.toString()}")
                    })
        }
    }

    override fun onCleared(){
        super.onCleared()

        //  destroy repository
        bitChuteRepository.onDestroy()
    }
}