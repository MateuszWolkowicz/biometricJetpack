package com.wolkowiczmateusz.biometricjetpack.repos.presentation

import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity

class GithubRepoListAdapter: PagedListAdapter<GithubRepoEntity, GithubRepoListViewHolder>(
    DIFF_CALLBACK
) {

    var itemSelected: (householdItem: GithubRepoEntity) -> Unit = {}

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): GithubRepoListViewHolder {
        return GithubRepoListViewHolder.create(
            parent
        )
    }

    override fun onBindViewHolder(@NonNull holder: GithubRepoListViewHolder, position: Int) {
        val githubRepoEntity :GithubRepoEntity? = getItem(position)
        holder.bindTo(githubRepoEntity)
        holder.itemView.setOnClickListener {
            if (githubRepoEntity != null) {
                itemSelected.invoke(githubRepoEntity)
            }
        }
    }
}

val DIFF_CALLBACK: DiffUtil.ItemCallback<GithubRepoEntity> =
    object : DiffUtil.ItemCallback<GithubRepoEntity>() {

        override fun areItemsTheSame(oldItem: GithubRepoEntity, newItem: GithubRepoEntity): Boolean {
            return oldItem.repoId == newItem.repoId
        }

        override fun areContentsTheSame(oldItem: GithubRepoEntity, newItem: GithubRepoEntity): Boolean {
            return oldItem == newItem
        }
    }

