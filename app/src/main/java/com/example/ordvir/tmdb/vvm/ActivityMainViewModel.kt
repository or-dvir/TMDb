package com.example.ordvir.tmdb.vvm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.example.ordvir.tmdb.model.Movie
import com.example.ordvir.tmdb.other.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class ActivityMainViewModel(app: Application) : AndroidViewModel(app)
{
    companion object
    {
        const val TAG = "ActivityMainViewModel"
        const val FIRST_PAGE = 1
        const val CODE_NEXT_PAGE = 1000
    }

    //make variable globally readable but only privately writable
    var mCurrentPage: Int = FIRST_PAGE
        private set
    val mTopMovies = MutableLiveData<List<Movie>>()

    init
    {
        launch (UI){
            SConfiguration.initialize()
            refresh()
        }
    }

    fun nextPage()
    {
        mCurrentPage++
        getShowsForCurrentPage(CODE_NEXT_PAGE)
    }

    fun refresh()
    {
        mCurrentPage = FIRST_PAGE

        //in this case the request code does not matter.
        //we only care about whether we are loading the next page
        getShowsForCurrentPage(-1)
    }

    private fun getShowsForCurrentPage(requestCode: Int)
    {
        tmdbRequestAsync(TAG,
                         getTMDbClient().getTopRated(mCurrentPage),
                         TMDbCallback<ServerResponseTopRated>().apply {
                             onSuccess = { originalCall, result, requestCode ->
                                 mTopMovies.value = result.results
                             }

                             onErrorOrExceptionOrNullBody = { originalCall,
                                                              serverErrorCode,
                                                              exception,
                                                              requestCode ->

                                 //if we failed loading the next page,
                                 //revert mCurrentPage so that when the user tries again,
                                 //we don't skip pages
                                 if (requestCode == CODE_NEXT_PAGE)
                                 {
                                     mCurrentPage--

                                     //just to be safe
                                     if (mCurrentPage < FIRST_PAGE)
                                         mCurrentPage = FIRST_PAGE
                                 }

                                 //so we trigger the observer
                                 mTopMovies.value = null
                             }
                         }, requestCode)
    }
}