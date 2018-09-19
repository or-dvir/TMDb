package com.example.ordvir.tmdb.model

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.ordvir.tmdb.R
import com.example.ordvir.tmdb.other.getUrlImage
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_cast_member.view.*
import org.jetbrains.anko.Bold
import org.jetbrains.anko.append
import org.jetbrains.anko.buildSpanned

class CastMemberAdapterItem(castMember: CastMember)
    : ModelAbstractItem<CastMember, CastMemberAdapterItem, CastMemberAdapterItem.ViewHolder>(castMember)
{
    override fun getType() = R.id.fastadapter_id_cast
    override fun getLayoutRes() = R.layout.list_item_cast_member
    override fun getViewHolder(v: View) = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>)
    {
        super.bindView(holder, payloads)

        val view = holder.view
        val context = view.context

        Picasso.get()
            .load(getUrlImage(model.profile_path))
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .fit()
            .centerInside()
            .into(view.iv_poster)

        view.tv_nameAndCharacter.text =
                buildSpanned {
                    append(model.actorName ?: context.getString(R.string.unknown), Bold)
                    model.characterName?.let { append ("\n$it") }
                }
    }

    override fun unbindView(holder: ViewHolder)
    {
        super.unbindView(holder)

        holder.view.apply {
            tv_nameAndCharacter.text = ""
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