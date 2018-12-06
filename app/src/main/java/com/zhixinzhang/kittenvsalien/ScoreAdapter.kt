package com.zhixinzhang.kittenvsalien

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.score_list_item.view.*

class ScoreAdapter(val scores: List<Pair<String, Int>>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.score_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNameType?.text = scores[position].first
        holder.tvScoreType?.text = scores[position].second.toString()
    }

    override fun getItemCount(): Int {
        return scores.size
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvNameType = view.tv_name_type
    val tvScoreType = view.tv_score_type
}

