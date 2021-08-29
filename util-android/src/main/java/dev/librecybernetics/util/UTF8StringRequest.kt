package dev.librecybernetics.util

import java.nio.charset.StandardCharsets

class UTF8StringRequest(
    method: Int,
    url: String,
    response: com.android.volley.Response.Listener<String>,
    error: com.android.volley.Response.ErrorListener
) : com.android.volley.toolbox.StringRequest(method, url, response, error) {
    override fun parseNetworkResponse(response: com.android.volley.NetworkResponse?): com.android.volley.Response<String> {
        val parsed = String(response!!.data, StandardCharsets.UTF_8)
        return com.android.volley.Response.success(
            parsed,
            com.android.volley.toolbox.HttpHeaderParser.parseCacheHeaders(response)
        )
    }
}