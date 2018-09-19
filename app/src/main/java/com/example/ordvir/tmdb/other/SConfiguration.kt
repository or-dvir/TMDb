package com.example.ordvir.tmdb.other

object SConfiguration
{
    private const val TAG = "SConfiguration"

    private var mBaseUrl: String? = null

    //theoretically the system can clear mBaseUrl and if that happens
    //every call to getBaseUrl() from that moment forward will return null until we
    //call initialize() again.
    //this can also happen if there is no internet connection, and the app stays open until
    //we get a connection and try to refresh the list.
    //however for the simplicity of this exercise, this should be good enough
    suspend fun initialize()
    {
        tmdbRequestAsync(TAG,
                         getTMDbClient().getConfiguration(),
                         TMDbCallback<ServerResponseConfiguration>().apply {
                             onSuccess = { originalCall, result, requestCode ->
                                 mBaseUrl = result.imageConfig?.base_url
                             }

                             //no need to handle error.
                             //mBaseUrl will remain null
                         }).join()
    }

    fun getBaseUrl() = mBaseUrl
}