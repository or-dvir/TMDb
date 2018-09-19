package com.example.ordvir.tmdb.vvm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.ordvir.tmdb.model.CastMember
import com.example.ordvir.tmdb.model.Movie
import com.example.ordvir.tmdb.other.*

class ActivityDetailsViewModel(app: Application) : AndroidViewModel(app)
{
    companion object
    {
        //must be shorter than 23 characters
        private const val TAG = "DetailsViewModel"
    }

    //make variable globally readable but only privately writable
    var mMovie: Movie? = null
        private set

    val mRuntime = MutableLiveData<Int>()
    val mCast = MutableLiveData<List<CastMember>>()

    fun setMovie(movie: Movie)
    {
        mMovie = movie
        if(mMovie != null)
        {
            getRuntime()
            getCast()
        }
    }

    private fun getCast()
    {
        //if we get here, then mMovie is for sure not null
        tmdbRequestAsync(TAG,
                         getTMDbClient().getCast(mMovie!!.id),
                         TMDbCallback<ServerResponseCast>().apply {
                             onSuccess = { originalCall, result, requestCode ->
                                 mCast.value = result.cast
                             }

                             onErrorOrExceptionOrNullBody = { originalCall,
                                                              serverErrorCode,
                                                              exception,
                                                              requestCode ->

                                 var text = "original call was $originalCall"
                                 serverErrorCode?.let {
                                     text += "\nerror code: $serverErrorCode"
                                 }

                                 Log.e(TAG, text, exception)

                                 //so we trigger the observer
                                 mCast.value = null
                             }
                         })
    }

    private fun getRuntime()
    {
        //if we get here, then mMovie is for sure not null
        tmdbRequestAsync(TAG,
                         getTMDbClient().getRuntime(mMovie!!.id),
                         TMDbCallback<ServerResponseRuntime>().apply {
                             onSuccess = { originalCall, result, requestCode ->
                                 mRuntime.value = result.runtime
                             }

                             onErrorOrExceptionOrNullBody = { originalCall,
                                                              serverErrorCode,
                                                              exception,
                                                              requestCode ->
                                 //just log the error.
                                 //if runtime is not available, it will simply
                                 //be hidden from the user

                                 var text = "original call was $originalCall"
                                 serverErrorCode?.let {
                                     text += "\nerror code: $serverErrorCode"
                                 }

                                 Log.e(TAG, text, exception)
                             }
                         })
    }
}