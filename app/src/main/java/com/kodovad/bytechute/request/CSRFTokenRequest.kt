package com.kodovad.bytechute.request

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class CSRFTokenRequest (
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener) : Request<String> (Method.GET, "https://www.bitchute.com/accounts/login/", errorListener) {

    override fun deliverResponse(response: String) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {

        return Response.success("", HttpHeaderParser.parseCacheHeaders(response))
    }
}