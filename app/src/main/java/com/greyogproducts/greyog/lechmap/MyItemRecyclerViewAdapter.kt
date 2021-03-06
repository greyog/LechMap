package com.greyogproducts.greyog.lechmap

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


import com.greyogproducts.greyog.lechmap.ItemFragment.OnListFragmentInteractionListener
import com.greyogproducts.greyog.lechmap.stonedata.StoneListContent.StoneItem

import kotlinx.android.synthetic.main.fragment_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [StoneItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(val context: Context,
        private val mValues: List<StoneItem>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val mOnMapImgClickListener: View.OnClickListener?

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as StoneItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item, false)
        }
        mOnMapImgClickListener = View.OnClickListener { v ->
            val item = v.tag as StoneItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item, true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = context.getString(R.string.stone_number) + item.name
        holder.mContentView.text = ""

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
        with(holder.mSetFocus) {
            tag = item
            setOnClickListener(mOnMapImgClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content
        val mSetFocus: ImageView = mView.set_focus

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
