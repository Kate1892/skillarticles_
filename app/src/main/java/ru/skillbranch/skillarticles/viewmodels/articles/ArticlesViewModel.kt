package ru.skillbranch.skillarticles.viewmodels.articles

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.ArticlesDataSource
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.NavCommand
import ru.skillbranch.skillarticles.viewmodels.VMState

class ArticlesViewModel(savedStateHandle: SavedStateHandle) : BaseViewModel<ArticlesState>(
    ArticlesState(), savedStateHandle
), IArticlesViewModel {
    private val repository: ArticlesRepository = ArticlesRepository()
    val articles: LiveData<List<ArticleItem>> = repository.findArticles()

    private val _articleQuery = MutableLiveData<String?>(null)
    val articleQuery: LiveData<String?> = _articleQuery

    private var dataSource: ArticlesDataSource? = null

    @OptIn(ExperimentalPagingApi::class)
    val articlesPager: LiveData<PagingData<ArticleItem>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10, //50
            prefetchDistance = 30,
            enablePlaceholders = false
        ),
        remoteMediator = repository.makeArticlesMediator(query = _articleQuery.value),
        pagingSourceFactory = {
            repository.makeArticleDataSource(query = _articleQuery.value).also { dataSource = it }
        }
    )
        .liveData
        .cachedIn(viewModelScope)


    override fun navigateToArticle(articleItem: ArticleItem) {
        articleItem.run {
            val options = NavOptions.Builder()
                .setEnterAnim(androidx.navigation.ui.R.animator.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.animator.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.animator.nav_default_pop_exit_anim)
            navigate(
                NavCommand.Builder(
                    R.id.page_article,
                    bundleOf(
                        "article_id" to id,
                        "author" to author,
                        "author_avatar" to authorAvatar,
                        "category" to category,
                        "category_icon" to categoryIcon,
                        "poster" to poster,
                        "title" to title,
                        "date" to date
                    ),
                    options.build()
                )
            )
        }
    }

    override fun checkBookmark(articleItem: ArticleItem, checked: Boolean) {

    }

    fun setSearchQuery(query: String?) {
        _articleQuery.postValue(query)
    }

    fun handleSearch() {
        dataSource?.invalidate()
    }
}

data class ArticlesState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true,
    val isBookmark: Boolean = false,
    val selectedCategories: List<String> = emptyList(),
    val isHashtagSearch: Boolean = false,
    val tags: List<String> = emptyList()
) : VMState