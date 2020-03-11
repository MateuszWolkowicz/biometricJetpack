package com.wolkowiczmateusz.biometricjetpack.repos.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.wolkowiczmateusz.biometricjetpack.R
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity

class GithubRepoListViewHolder private constructor(
    itemView: View
): RecyclerView.ViewHolder(itemView) {

    private var title: TextView = itemView.findViewById(R.id.tv_title)
    private var description: TextView = itemView.findViewById(R.id.tv_description)
    private var stars: TextView = itemView.findViewById(R.id.repo_stars)

    fun bindTo(githubRepoEntity: GithubRepoEntity?) {
        if (githubRepoEntity != null) {
            title.text = githubRepoEntity.name
            description.text = githubRepoEntity.description ?: ""
            stars.text = githubRepoEntity.stars.toString()
        } else {
            val resources = itemView.resources
            title.text = resources.getString(R.string.loading)
            description.text = resources.getString(R.string.loading)
            stars.text = resources.getString(R.string.loading)
        }
    }

    companion object {
        fun create(parent: ViewGroup): GithubRepoListViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false)
            return GithubRepoListViewHolder(
                view
            )
        }
    }
}
