package dev.librecybernetics.coopcycle.util

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import java.nio.charset.StandardCharsets

class UTF8StringRequest(
    method: Int,
    url: String,
    response: Response.Listener<String>,
    error: Response.ErrorListener
) : StringRequest(method, url, response, error) {
    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        val parsed = String(response!!.data, StandardCharsets.UTF_8)
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
    }
}
