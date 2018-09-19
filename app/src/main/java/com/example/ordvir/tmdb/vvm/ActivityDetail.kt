package com.example.ordvir.tmdb.vvm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.ordvir.tmdb.R
import com.example.ordvir.tmdb.model.CastMember
import com.example.ordvir.tmdb.model.CastMemberAdapterItem
import com.example.ordvir.tmdb.model.Movie
import com.example.ordvir.tmdb.other.createAndShowSimpleDialog
import com.example.ordvir.tmdb.other.getUrlImage
import com.example.ordvir.tmdb.other.setHomeUpEnabled
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IInterceptor
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail : AppCompatActivity()
{
    companion object
    {
        const val TAG = "ActivityDetail"
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
    }

    private lateinit var mAdapterRv: FastAdapter<CastMemberAdapterItem>
    private lateinit var mAdapterCast: ModelAdapter<CastMember, CastMemberAdapterItem>
    private lateinit var mViewModel: ActivityDetailsViewModel

    //NOTE:
    //this activity will require a different layout for landscape orientation
    //for simplicity of this exercise, it is locked to portrait orientation only

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = getString(R.string.detailsTitle)
        setHomeUpEnabled(true)
        setupAdapter()

        mViewModel = ViewModelProviders.of(this).get(ActivityDetailsViewModel::class.java)

        try
        {
            val movie = intent.getSerializableExtra(EXTRA_MOVIE) as Movie
            mViewModel.setMovie(movie)
        }

        catch (e: Exception)
        {
            Log.e(TAG, e.message, e)
        }

        mViewModel.mMovie.apply {

            if(this == null)
            {
                createAndShowSimpleDialog(R.string.errorLoadingMovie)
                return
            }

            Picasso.get()
                .load(getUrlImage(backdrop_path))
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .fit()
                .centerInside()
                .into(iv_backdrop)

            val title = title ?: getString(R.string.unknownTitle)
            val year = getYear() ?: getString(R.string.unknownYear)
            val titleAndYear = "$title\n$year"

            tv_titleAndYear.text = titleAndYear
            tv_overview.text = overview ?: getString(R.string.overviewUnavailable)
            tv_rating.text = vote_average?.toString() ?: "?"
        }

        rv_cast.apply {
            adapter = mAdapterRv
            layoutManager = LinearLayoutManager(this@ActivityDetail,
                                                LinearLayoutManager.HORIZONTAL,
                                                false)
        }

        mViewModel.mRuntime.observe(this, Observer {

            if(it != null)
            {
                iv_clock.visibility = View.VISIBLE
                tv_runtime.visibility = View.VISIBLE

                val runtimeText = "$it ${getString(R.string.min)}"
                tv_runtime.text = runtimeText
            }
        })

        mViewModel.mCast.observe(this, Observer {
            progressBar.visibility = View.GONE

            if(it == null || it.isEmpty())
            {
                createAndShowSimpleDialog(R.string.errorLoadingCast)
                return@Observer
            }

            mAdapterCast.setNewList(it)
        })
    }

    private fun setupAdapter()
    {
        mAdapterCast = ModelAdapter(IInterceptor { CastMemberAdapterItem(it) })

        mAdapterRv = FastAdapter<CastMemberAdapterItem>()
            .addAdapter(0, mAdapterCast)
            .withSelectable(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if(item.itemId == android.R.id.home)
        {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
