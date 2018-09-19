package com.example.ordvir.tmdb.model

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.ordvir.tmdb.R
import com.example.ordvir.tmdb.other.getUrlImage
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_movie.view.*

class MovieAdapterItem(movie: Movie)
    : ModelAbstractItem<Movie, MovieAdapterItem, MovieAdapterItem.ViewHolder>(movie)
{
    override fun getType() = R.id.fastadapter_id_movie
    override fun getLayoutRes() = R.layout.list_item_movie
    override fun getViewHolder(v: View) = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>)
    {
        super.bindView(holder, payloads)

        val view = holder.view
        val context = view.context

        Picasso.get()
            .load(getUrlImage(model.poster_path))
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .fit()
            .centerInside()
            .into(view.iv_poster)

        val title = model.title ?: context.getString(R.string.unknownTitle)
        val year = model.getYear() ?: context.getString(R.string.unknownYear)
        val titleAndYear = "$title\n$year"

        view.tv_titleAndYear.text = titleAndYear
        view.tv_overview.text = model.overview ?: context.getString(R.string.overviewUnavailable)

        //if the rating is not available (null), text such as "unknown"
        //would be too long to show under the star.
        //option 1 - show a short text to imply that the rating is unknown (such as "?")
        //option 2 - make the star entirely invisible and show nothing.
        //for consistency of the list, i chose option 1
        view.tv_rating.text = model.vote_average?.toString() ?: "?"
    }

    override fun unbindView(holder: ViewHolder)
    {
        super.unbindView(holder)

        holder.view.apply {
            tv_titleAndYear.text = ""
            tv_overview.text = ""
            tv_rating.text = ""
            iv_poster.setImageDrawable(null)
            Picasso.get().cancelRequest(iv_poster)
        }
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    //even though this class is empty, we cannot directly use RecyclerView.ViewHolder
    //because it is abstract and cannot create instances of it
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}