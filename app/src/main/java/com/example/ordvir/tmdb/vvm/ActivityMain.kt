package com.example.ordvir.tmdb.vvm

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager
import com.example.ordvir.tmdb.R
import com.example.ordvir.tmdb.model.Movie
import com.example.ordvir.tmdb.model.MovieAdapterItem
import com.example.ordvir.tmdb.other.createAndShowLoadingDialog
import com.example.ordvir.tmdb.other.createAndShowSimpleDialog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IInterceptor
import com.mikepenz.fastadapter.adapters.ModelAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class ActivityMain : AppCompatActivity()
{
    private lateinit var mAdapterRv: FastAdapter<MovieAdapterItem>
    private lateinit var mAdapterMovies: ModelAdapter<Movie, MovieAdapterItem>
    @Suppress("DEPRECATION")
    private var mProgDiag: ProgressDialog? = null
    private lateinit var mViewModel: ActivityMainViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.topRatedMovies)
        setupAdapter()

        rv_topRatedMovies.apply {
            //requesting focus so the keyboard does not automatically open
            requestFocus()
            adapter = mAdapterRv
            layoutManager = LinearLayoutManager(this@ActivityMain,
                                                LinearLayoutManager.VERTICAL,
                                                false)
            addItemDecoration(DividerItemDecoration(this@ActivityMain, DividerItemDecoration.VERTICAL))
        }

        //when creating mViewModel it immediately sends a network request,
        //so show loading dialog
        showLoadingDialog()
        mViewModel = ViewModelProviders.of(this).get(ActivityMainViewModel::class.java)

        mViewModel.mTopMovies.observe(this, Observer {
            refreshContainer.isRefreshing = false
            mProgDiag?.dismiss()
            if(it == null)
            {
                createAndShowSimpleDialog(R.string.errorLoadingMovies)
                return@Observer
            }

            if(it.isEmpty())
            {
                toast(R.string.noMoreMovies)
                return@Observer
            }

            mAdapterMovies.add(it)
        })

        searchView.setOnQueryTextListener(
            object : android.support.v7.widget.SearchView.OnQueryTextListener
            {
                override fun onQueryTextSubmit(query: String): Boolean
                {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean
                {
                    newText?.apply {
                        //this returns the adapter to normal
                        if (isBlank())
                            mAdapterMovies.filter("")
                        else
                            mAdapterMovies.filter(newText.trim())
                    }

                    return true
                }
            })

        refreshContainer.setOnRefreshListener {
            mAdapterMovies.clear()
            mViewModel.refresh()
        }
    }

    private fun hideKeyboard()
    {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(currentFocus != null)
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }

    private fun setupAdapter()
    {
        mAdapterMovies = ModelAdapter(IInterceptor { MovieAdapterItem(it) })

        mAdapterMovies.itemFilter.withFilterPredicate { item, constraint ->
            if(constraint.isNullOrBlank())
                true
            else
                item.model.title?.contains(constraint!!.trim(),true) ?: true
        }

        mAdapterRv = FastAdapter<MovieAdapterItem>()
            .addAdapter(0, mAdapterMovies)
            .withSelectable(false)
            .withOnClickListener { v, adapter, item, position ->

                startActivity(intentFor<ActivityDetail>
                                  (ActivityDetail.EXTRA_MOVIE to item.model))
                true
            }
    }

    private fun showLoadingDialog()
    {
        if (mProgDiag == null)
            mProgDiag = createAndShowLoadingDialog()

        mProgDiag?.apply { if(!isShowing) show()}
    }

    override fun onResume()
    {
        super.onResume()
        rv_topRatedMovies.addOnScrollListener(object : RecyclerView.OnScrollListener()
                                              {
                                                  override fun onScrolled(recyclerView: RecyclerView?,
                                                                          dx: Int,
                                                                          dy: Int)
                                                  {
                                                      //we reached the bottom of the list.
                                                      //only do this if not filtered (search query is blank),
                                                      //and if not refreshing
                                                      if (recyclerView != null &&
                                                          !recyclerView.canScrollVertically(1) &&
                                                          searchView.query.isBlank() &&
                                                          !refreshContainer.isRefreshing)
                                                      {
                                                          //the api has a limit where the maximum
                                                          //page is 1000
                                                          if (mViewModel.mCurrentPage + 1 >= 1000)
                                                              toast(R.string.noMoreMovies)
                                                          else
                                                          {
                                                              showLoadingDialog()
                                                              mViewModel.nextPage()
                                                          }
                                                      }
                                                  }
                                              })
    }

    override fun onPause()
    {
        rv_topRatedMovies.clearOnScrollListeners()
        super.onPause()
    }

    override fun onDestroy()
    {
        //this is to prevent "view not attached to window" exception
        mProgDiag?.dismiss()
        mProgDiag = null
        super.onDestroy()
    }
}
