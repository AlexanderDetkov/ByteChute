package com.kodovad.bytechute.repository

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.kodovad.bytechute.request.*
import com.kodovad.bytechute.response.ExtendResponse
import com.kodovad.bytechute.response.HomeResponse
import com.kodovad.bytechute.response.LoginResponse
import com.kodovad.bytechute.response.SearchResponse
import com.kodovad.bytechute.utils.networking.VolleyRequestSingleton
import com.kodovad.bytechute.utils.singleton.SingletonHolder
import java.net.CookieStore


class BitChuteRepository private constructor(private val applicationContext: Context) {
    companion object : SingletonHolder<BitChuteRepository, Context>(::BitChuteRepository)

    //  networking singletons
    private val requestQueue: RequestQueue by lazy {
        VolleyRequestSingleton.getInstance(applicationContext).requestQueue
    }
    private val persistentCookieStore: CookieStore by lazy {
        VolleyRequestSingleton.getInstance(applicationContext).persistentCookieStore
    }



    //  Account Management
    fun login(usr: String, pwd: String, loginListener: Response.Listener<LoginResponse>, errorListener: Response.ErrorListener){
        val csrfToken: String? = getCSRFTokenFromCookies()

        //  check if cookie has csrfToken
        if (csrfToken != null)
            requestQueue.add(LoginRequest(usr, pwd, csrfToken, loginListener, errorListener))
        else
            //  get csrfToken from request
            requestCSRFToken { login(usr, pwd, loginListener, errorListener) }
    }
    //  TODO(Logout)


    //  Home Page
    fun setHomePage(category: String?, requestListener: Response.Listener<HomeResponse>, errorListener: Response.ErrorListener){
        requestQueue.add(HomeRequest(category, requestListener, errorListener))
    }
    fun extendHomePage(tabName: String, category: String?, offset: Int, lastID: String,
                       requestListener: Response.Listener<ExtendResponse>, errorListener: Response.ErrorListener) {
        val csrfToken: String? = getCSRFTokenFromCookies()

        //  check if cookie has csrfToken
        if (csrfToken != null)
            requestQueue.add(ExtendRequest(tabName, category, offset, lastID, csrfToken, requestListener, errorListener))
        else
            //  get csrfToken from request
            requestCSRFToken { extendHomePage(tabName, category, offset, lastID, requestListener, errorListener) }
    }

    //  Search
    fun setSearchResults(query: String, pageNumber: Int, filter: String?,
                        responseListener: Response.Listener<SearchResponse>, errorListener: Response.ErrorListener){
        //  use non singleton to prevent cookies
        //  TODO(Use Singleton with cookie blocking)
        Volley.newRequestQueue(applicationContext).add(SearchRequest(query, pageNumber, filter, responseListener, errorListener)
        )
    }

    //  CSRFToken getters
    private fun getCSRFTokenFromCookies(): String?{
        for (cookie in persistentCookieStore.cookies)
            if (cookie.name == "csrftoken")
                return cookie.value
        return null
    }
    private fun requestCSRFToken(function: () -> (Unit)) {
        requestQueue.add(CSRFTokenRequest(
            Response.Listener {

                //  check if CSRFToken is in cookie
                if (getCSRFTokenFromCookies() != null)
                    function.invoke()
                else
                    TODO("Error in getting CSRFToken")
            }, Response.ErrorListener {
                TODO("Error in getting CSRFToken")
            }
        ))
    }


    fun onDestroy(){

        //  cancel all volley requests
        requestQueue.cancelAll {true}
    }
}