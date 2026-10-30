package com.movify.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.movify.app.R
import com.movify.app.models.Result

class MoviesAdapter : ListAdapter<Result, MoviesAdapter.MoviesHolder>(DIFF_CALLBACK) {
    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesHolder =
        MoviesHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_movies, parent, false))

    override fun onBindViewHolder(holder: MoviesHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: MoviesHolder) {
        Glide.with(holder.itemView).clear(holder.image)
        super.onViewRecycled(holder)
    }

    class MoviesHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvTitle)
        private val overview: TextView = view.findViewById(R.id.tvOverView)
        private val releaseDate: TextView = view.findViewById(R.id.tvReleaseDate)
        val image: ImageView = view.findViewById(R.id.ivMovie)

        fun bind(movie: Result) {
            title.text = movie.title
            overview.text = movie.overview
            releaseDate.text = movie.releaseDate
            val poster = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500/$it" }
            Glide.with(itemView)
                .load(poster)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(image)
        }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Result>() {
            override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean =
                oldItem.id?.let { it == newItem.id } ?: (oldItem === newItem)

            override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean =
                oldItem == newItem
        }
    }
}
