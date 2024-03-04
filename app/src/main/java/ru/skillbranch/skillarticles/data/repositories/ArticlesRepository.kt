package ru.skillbranch.skillarticles.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import ru.skillbranch.skillarticles.data.LocalDataHolder
import ru.skillbranch.skillarticles.data.NetworkDataHolder
import ru.skillbranch.skillarticles.extensions.toArticleItem
import ru.skillbranch.skillarticles.viewmodels.articles.ArticleItem

class ArticlesRepository(
    private val local: LocalDataHolder = LocalDataHolder,
    private val network: NetworkDataHolder = NetworkDataHolder,
) {
    fun findArticles(): LiveData<List<ArticleItem>> = local.findArticles()

    fun makeArticleDataSource(query: String? = null) = ArticlesDataSource(local, query)

    fun makeArticlesMediator(query: String? = null) =
        ArticlesMediator(network = network, local = local, query = query)
}

@OptIn(ExperimentalPagingApi::class)
class ArticlesMediator(
    val network: NetworkDataHolder,
    val local: LocalDataHolder,
    val query: String? = null
) : RemoteMediator<Int, ArticleItem>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleItem>
    ): MediatorResult {
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    val article = network.loadArticles(null, state.config.pageSize, query)
                    local.insertArticles(article.map { it.toArticleItem() })
                    MediatorResult.Success(endOfPaginationReached = false)
                }

                LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    val articles =
                        network.loadArticles(
                            lastItem?.id?.toInt()?.inc(),
                            state.config.pageSize,
                            query
                        )
                    local.insertArticles(articles.map { it.toArticleItem() })
                    MediatorResult.Success(endOfPaginationReached = articles.isEmpty())
                }
            }
        } catch (e: Throwable) {
            MediatorResult.Error(e)
        }
    }

}

class ArticlesDataSource(val local: LocalDataHolder, val query: String? = null) :
    PagingSource<Int, ArticleItem>() {

    init {
        local.attachDataSource(this)
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleItem>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        val size = state.config.pageSize
        val nextKey = anchorPage.nextKey
        val prevKey = anchorPage.prevKey

        Log.v("PREV!", "$prevKey $nextKey $size");
        return prevKey?.plus(size) ?: nextKey?.minus(size)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleItem> {
        val pageKey = params.key ?: 0
        val pageSize = params.loadSize
        return try {
            Log.v("PREV!", query.toString());

            val articles = local.loadArticles(pageKey, pageSize, query)
            Log.v("RAR", articles.size.toString())
            val prevKey = if (pageKey > 0) pageKey.minus(pageSize) else null
            val nextKey = if (articles.isNotEmpty()) pageKey.plus(pageSize) else null

            Log.v("RAR!", "$prevKey $nextKey")
            LoadResult.Page(
                data = articles,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

}