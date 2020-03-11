package com.wolkowiczmateusz.biometricjetpack.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.wolkowiczmateusz.biometricjetpack.BaseFragment
import com.wolkowiczmateusz.biometricjetpack.R
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import kotlinx.android.synthetic.main.github_repo_fragment.*


class GithubRepoFragment : BaseFragment() {

    private lateinit var githubRepoId: String
    private lateinit var githubRepoViewModel: GithubRepoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.github_repo_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        githubRepoViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        mainViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        githubRepoId = if (savedInstanceState != null) {
            savedInstanceState.getString(GITHUB_REPO_ID)!!
        } else {
            val safeArgs =
                GithubRepoFragmentArgs.fromBundle(
                    requireArguments()
                )
            safeArgs.githubRepoId
        }

        observeRepo()
        githubRepoViewModel.getGithubRepositoryById(githubRepoId)
    }

    private fun observeRepo() {
        githubRepoViewModel.repo.observe(viewLifecycleOwner, Observer {
            showRepoInfo(it)
        })
    }

    private fun showRepoInfo(it: GithubRepoEntity) {
        tv_title.text = it.name
        tv_description.text = it.description
        repo_stars.text = it.stars.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(GITHUB_REPO_ID, githubRepoId)
    }

    companion object {
        const val GITHUB_REPO_ID = "GITHUB_REPO_ID"
    }
}
