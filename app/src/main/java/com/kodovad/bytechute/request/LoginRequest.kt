package com.kodovad.bytechute.request

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.kodovad.bytechute.model.Channel
import com.kodovad.bytechute.response.LoginResponse
import org.json.JSONObject
import org.jsoup.nodes.Element
import java.nio.charset.Charset

class LoginRequest (
    private val username: String,
    private val password: String,
    private val CSRFToken: String,
    private val listener: Response.Listener<LoginResponse>,
    errorListener: Response.ErrorListener) : Request<LoginResponse>
    (Method.POST, "https://www.bitchute.com/accounts/login/", errorListener) {

    override fun getParams(): MutableMap<String, String>
            = mutableMapOf("username" to username, "password" to password, "csrfmiddlewaretoken" to CSRFToken)

    override fun getHeaders(): Map<String, String> = mapOf("Referer" to url)

    override fun deliverResponse(response: LoginResponse) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<LoginResponse> {

        var loginSuccess = false

        //  get raw response
        val rawResponse = String(
            response?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

        val jsonResponse = JSONObject(rawResponse)

        if (jsonResponse.has("success"))
            loginSuccess = jsonResponse.getBoolean("success")

        return Response.success(
            LoginResponse(loginSuccess),
            HttpHeaderParser.parseCacheHeaders(response))
    }
}