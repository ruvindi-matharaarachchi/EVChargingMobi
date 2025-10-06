package com.evcharge.mobile.data.remote

import android.util.Log
import com.evcharge.mobile.BuildConfig
import com.evcharge.mobile.util.Json
import com.evcharge.mobile.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpClient {
    
    companion object {
        private const val TAG = "HttpClient"
        private const val CONNECT_TIMEOUT = 10000 // 10 seconds
        private const val READ_TIMEOUT = 15000 // 15 seconds
    }
    
    private val baseUrl = BuildConfig.BASE_URL
    private var authToken: String? = null
    
    fun setAuthToken(token: String?) {
        authToken = token
    }
    
    suspend fun checkBackendHealth(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(baseUrl + "/api/health")
                val connection = createConnection(url, "GET")
                
                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    readResponse(connection)
                } else {
                    readErrorResponse(connection)
                }
                
                connection.disconnect()
                
                if (responseCode in 200..299) {
                    Result.Success(response)
                } else {
                    Result.Error("Backend health check failed: HTTP $responseCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend health check failed", e)
                when {
                    e.message?.contains("Connection refused") == true -> 
                        Result.Error("Backend server is not running. Please start the .NET API server.")
                    e.message?.contains("timeout") == true -> 
                        Result.Error("Connection timeout. Check if backend server is running.")
                    e.message?.contains("UnknownHostException") == true -> 
                        Result.Error("Cannot reach backend server. Check network connection.")
                    else -> Result.Error("Backend health check failed: ${e.message}")
                }
            }
        }
    }
    
    suspend fun get(path: String, params: Map<String, String> = emptyMap()): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildUrl(path, params)
                val connection = createConnection(url, "GET")
                
                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    readResponse(connection)
                } else {
                    readErrorResponse(connection)
                }
                
                connection.disconnect()
                
                if (responseCode in 200..299) {
                    Result.Success(response)
                } else {
                    Result.Error("HTTP $responseCode: $response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "GET request failed", e)
                when {
                    e.message?.contains("Connection refused") == true -> 
                        Result.Error("Backend server is not running. Please start the .NET API server.")
                    e.message?.contains("timeout") == true -> 
                        Result.Error("Connection timeout. Check if backend server is running.")
                    e.message?.contains("UnknownHostException") == true -> 
                        Result.Error("Cannot reach backend server. Check network connection.")
                    else -> Result.Error("Network error: ${e.message}")
                }
            }
        }
    }
    
    suspend fun post(path: String, body: Any? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(baseUrl + path)
                val connection = createConnection(url, "POST")
                
                if (body != null) {
                    val jsonBody = when (body) {
                        is LoginRequest -> Json.encodeLoginRequest(body)
                        is BookingRequest -> Json.encodeBookingRequest(body)
                        is OwnerUpdateRequest -> Json.encodeOwnerUpdateRequest(body)
                        else -> throw IllegalArgumentException("Unsupported body type: ${body::class.simpleName}")
                    }
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Length", jsonBody.length.toString())
                    
                    val writer = OutputStreamWriter(connection.outputStream)
                    writer.write(jsonBody)
                    writer.flush()
                    writer.close()
                }
                
                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    readResponse(connection)
                } else {
                    readErrorResponse(connection)
                }
                
                connection.disconnect()
                
                if (responseCode in 200..299) {
                    Result.Success(response)
                } else {
                    Result.Error("HTTP $responseCode: $response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "POST request failed", e)
                when {
                    e.message?.contains("Connection refused") == true -> 
                        Result.Error("Backend server is not running. Please start the .NET API server.")
                    e.message?.contains("timeout") == true -> 
                        Result.Error("Connection timeout. Check if backend server is running.")
                    e.message?.contains("UnknownHostException") == true -> 
                        Result.Error("Cannot reach backend server. Check network connection.")
                    else -> Result.Error("Network error: ${e.message}")
                }
            }
        }
    }
    
    suspend fun put(path: String, body: Any? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(baseUrl + path)
                val connection = createConnection(url, "PUT")
                
                if (body != null) {
                    val jsonBody = when (body) {
                        is LoginRequest -> Json.encodeLoginRequest(body)
                        is BookingRequest -> Json.encodeBookingRequest(body)
                        is OwnerUpdateRequest -> Json.encodeOwnerUpdateRequest(body)
                        else -> throw IllegalArgumentException("Unsupported body type: ${body::class.simpleName}")
                    }
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Length", jsonBody.length.toString())
                    
                    val writer = OutputStreamWriter(connection.outputStream)
                    writer.write(jsonBody)
                    writer.flush()
                    writer.close()
                }
                
                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    readResponse(connection)
                } else {
                    readErrorResponse(connection)
                }
                
                connection.disconnect()
                
                if (responseCode in 200..299) {
                    Result.Success(response)
                } else {
                    Result.Error("HTTP $responseCode: $response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "PUT request failed", e)
                when {
                    e.message?.contains("Connection refused") == true -> 
                        Result.Error("Backend server is not running. Please start the .NET API server.")
                    e.message?.contains("timeout") == true -> 
                        Result.Error("Connection timeout. Check if backend server is running.")
                    e.message?.contains("UnknownHostException") == true -> 
                        Result.Error("Cannot reach backend server. Check network connection.")
                    else -> Result.Error("Network error: ${e.message}")
                }
            }
        }
    }
    
    suspend fun delete(path: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(baseUrl + path)
                val connection = createConnection(url, "DELETE")
                
                val responseCode = connection.responseCode
                val response = if (responseCode in 200..299) {
                    readResponse(connection)
                } else {
                    readErrorResponse(connection)
                }
                
                connection.disconnect()
                
                if (responseCode in 200..299) {
                    Result.Success(response)
                } else {
                    Result.Error("HTTP $responseCode: $response")
                }
            } catch (e: Exception) {
                Log.e(TAG, "DELETE request failed", e)
                when {
                    e.message?.contains("Connection refused") == true -> 
                        Result.Error("Backend server is not running. Please start the .NET API server.")
                    e.message?.contains("timeout") == true -> 
                        Result.Error("Connection timeout. Check if backend server is running.")
                    e.message?.contains("UnknownHostException") == true -> 
                        Result.Error("Cannot reach backend server. Check network connection.")
                    else -> Result.Error("Network error: ${e.message}")
                }
            }
        }
    }
    
    private fun buildUrl(path: String, params: Map<String, String>): URL {
        val urlBuilder = StringBuilder(baseUrl + path)
        
        if (params.isNotEmpty()) {
            urlBuilder.append("?")
            params.entries.forEachIndexed { index, entry ->
                if (index > 0) urlBuilder.append("&")
                urlBuilder.append(URLEncoder.encode(entry.key, "UTF-8"))
                urlBuilder.append("=")
                urlBuilder.append(URLEncoder.encode(entry.value, "UTF-8"))
            }
        }
        
        return URL(urlBuilder.toString())
    }
    
    private fun createConnection(url: URL, method: String): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = CONNECT_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        
        authToken?.let { token ->
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        
        return connection
    }
    
    private fun readResponse(connection: HttpURLConnection): String {
        val inputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val response = StringBuilder()
        
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        
        reader.close()
        inputStream.close()
        return response.toString()
    }
    
    private fun readErrorResponse(connection: HttpURLConnection): String {
        return try {
            val errorStream = connection.errorStream
            if (errorStream != null) {
                val reader = BufferedReader(InputStreamReader(errorStream))
                val response = StringBuilder()
                
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                
                reader.close()
                errorStream.close()
                response.toString()
            } else {
                "Unknown error"
            }
        } catch (e: Exception) {
            "Error reading response: ${e.message}"
        }
    }
}
