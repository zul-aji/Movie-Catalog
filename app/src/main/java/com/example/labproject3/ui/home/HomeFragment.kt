package com.example.labproject3.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.labproject3.LoginActivity
import com.example.labproject3.R
import com.example.labproject3.data.PassedData
import com.example.labproject3.data.TokenPreferences
import com.example.labproject3.data.network.APIRequest
import com.example.labproject3.data.network.Resource
import com.example.labproject3.data.repository.MoviesRepo
import com.example.labproject3.data.response.MoviesItem
import com.example.labproject3.databinding.FragmentHomeBinding
import com.example.labproject3.ui.base.BaseFragment
import com.example.labproject3.ui.details.MovieDetailsActivity
import com.example.labproject3.ui.startNewActivity
import com.example.labproject3.ui.visible

class HomeFragment :
    BaseFragment<HomeViewModel, FragmentHomeBinding, MoviesRepo>(),
    FavouriteAdapter.RemoveFavListener {

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var userID: String
    private lateinit var galAdapter: GalleryAdapter
    private var favIdList = ArrayList<MoviesItem>()
    private val galList = ArrayList<MoviesItem>()
    private var page: Int = 1
    private var isLoading = false

    override fun getViewModel() = HomeViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)
    override fun getFragmentRepository() = MoviesRepo(dataSource.buildAPI(requireContext(), APIRequest::class.java))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tokenPreferences = TokenPreferences(requireContext())
        binding.favRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.galleryRecycler.layoutManager = LinearLayoutManager(activity)

        val token = "Bearer " + tokenPreferences.getToken()
        userID = "mt"

        binding.rvProgress.visible(false)

        viewModel.getUser(token)
        viewModel.getUserResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                    userID = it.value.id
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    tokenPreferences.removeToken()
                    requireActivity().startNewActivity(LoginActivity::class.java)
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else if (it.errorMessage == null) { "Token Expired" }
                        else { "Fail: ${it.errorMessage}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    tokenPreferences.removeToken()
                    requireActivity().startNewActivity(LoginActivity::class.java)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.getFavorite(token)
        viewModel.getFavoriteResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                    if (it.value.movies.isEmpty()) {
                        binding.noFav.visible(true)
                    } else {
                        binding.noFav.visible(false)
                        val listFav = ArrayList<MoviesItem>()
                        it.value.movies.forEachIndexed { _, moviesItem ->
                            val favorite = MoviesItem(
                                id = moviesItem.id,
                                name = moviesItem.name,
                                poster = moviesItem.poster,
                                year = moviesItem.year,
                                country = moviesItem.country,
                                genres = moviesItem.genres,
                                reviews = moviesItem.reviews,
                            )
                            listFav.add(favorite)
                            favIdList.add(favorite)

                            val favAdapter = FavouriteAdapter(listFav, token, userID, requireContext(), this)
                            binding.favRecycler.adapter = favAdapter
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.galleryRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = (binding.galleryRecycler.layoutManager as LinearLayoutManager).childCount
                val pastVisibleItem = (binding.galleryRecycler.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val total = galList.size

                if (!isLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total){
                        isLoading = true
                        page++
                        viewModel.getGallery(page)
                        galAdapter.notifyDataSetChanged()
                        isLoading = false
                    }
                }
            }
        })

        viewModel.getGalleryResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.value.movies.forEachIndexed { index, moviesItem ->
                        if (index == 0 && it.value.pageInfo.currentPage == 1) {
                            Glide.with(requireContext())
                                .load(it.value.movies[index].poster)
                                .into(binding.featuredPoster)
                            binding.featuredTitle.text = it.value.movies[index].name
                            binding.button.setOnClickListener { view ->
                                val nextPage = Intent(requireContext(), MovieDetailsActivity::class.java)
                                nextPage.putExtra(PassedData.MOVIE_ID, it.value.movies[index].id)
                                nextPage.putExtra(PassedData.TOKEN, token)
                                nextPage.putExtra(PassedData.USER_ID, userID)

                                val isFavorite = favIdList.any { movie -> movie.id == it.value.movies[index].id }
                                if (isFavorite) {
                                    nextPage.putExtra(PassedData.IS_FAV, "true")
                                } else {
                                    nextPage.putExtra(PassedData.IS_FAV, "false")
                                }
                                startActivity(nextPage)
                            }
                        } else {
                            val gallery = MoviesItem(
                                id = moviesItem.id,
                                name = moviesItem.name,
                                poster = moviesItem.poster,
                                year = moviesItem.year,
                                country = moviesItem.country,
                                genres = moviesItem.genres,
                                reviews = moviesItem.reviews,
                            )
                            galList.add(gallery)

                            galAdapter = GalleryAdapter(galList, favIdList, token, userID, requireContext())
                            binding.galleryRecycler.adapter = galAdapter
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        if (it.isNetworkError){ "Network Error" }
                        else { "Fail: ${it.errorMessage.toString()}" },
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Loading -> {
                    binding.rvProgress.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        galList.clear()
        page = 1

        tokenPreferences = TokenPreferences(requireContext())
        val token = "Bearer " + tokenPreferences.getToken()

        viewModel.getUser(token)
        viewModel.getFavorite(token)
        viewModel.getGallery(page)
    }

    override fun removeFavMovie(id: String) {
        tokenPreferences = TokenPreferences(requireContext())
        val token = "Bearer " + tokenPreferences.getToken()

        viewModel.removeFavorite(token, id)
        viewModel.removeFavoriteResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visible(false)
                }
                is Resource.Failure -> {
                    binding.progressCircle.visible(false)
                    if (it.isNetworkError){
                        onResume()
                        Toast.makeText(
                            requireContext(),"Removed from favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(), "Fail: ${it.errorMessage.toString()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Resource.Loading -> {
                    binding.progressCircle.visible(true)
                }
                else -> {
                    binding.progressCircle.visible(false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_err),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}