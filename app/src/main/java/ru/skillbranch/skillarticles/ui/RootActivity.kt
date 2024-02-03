package ru.skillbranch.skillarticles.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.ui.custom.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.BottombarData
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.SubmenuData
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory
import ru.skillbranch.skillarticles.viewmodels.toBottombarData
import ru.skillbranch.skillarticles.viewmodels.toSubmenuData

class RootActivity : AppCompatActivity(), IArticleView {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var viewModelFactory: ViewModelProvider.Factory = ViewModelFactory(this, "0")
    private val viewModel: ArticleViewModel by viewModels { viewModelFactory }
    val vb: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)

    val vbBottombar
        get() = vb.bottombar
    val vbSubmenu
        get() = vb.submenu

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupBottombar()
        setupSubmenu()
        setupCopyListener()

        viewModel.observeState(this, ::renderUi)
        viewModel.observeSubState(this, ArticleState::toBottombarData, ::renderBotombar)
        viewModel.observeSubState(this, ArticleState::toSubmenuData, ::renderSubmenu)

        viewModel.observeNotifications(this) {
            renderNotification(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu.findItem(R.id.action_search)
        searchView = (menuItem.actionView as SearchView)
        searchView.queryHint = getString(R.string.article_search_placeholder)

        //restore SearchView
        if (viewModel.currentState.isSearch) {
            menuItem.expandActionView()
            searchView.setQuery(viewModel.currentState.searchQuery, false)
            searchView.requestFocus()
        } else {
            searchView.clearFocus()
        }

        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveSate()
        super.onSaveInstanceState(outState)
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(vb.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(vb.bottombar)

        when (notify) {
            is Notify.ActionMessage -> {
                val (_, label, handler) = notify

                with(snackbar) {
                    setActionTextColor(getColor(R.color.color_accent_dark))
                    setAction(label) { handler.invoke() }
                }
            }

            is Notify.ErrorMessage -> {
                val (_, label, handler) = notify

                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    handler ?: return@with
                    setAction(label) { handler.invoke() }
                }
            }

            else -> { /* nothing */
            }
        }

        snackbar.show()
    }

    override fun setupSubmenu() {
        with(vbSubmenu) {
            btnTextUp.setOnClickListener { viewModel.handleUpText() }
            btnTextDown.setOnClickListener { viewModel.handleDownText() }
            switchMode.setOnClickListener { viewModel.handleNightMode() }
        }

    }

    override fun setupBottombar() {
        with(vbBottombar) {
            val vbr = vb

            btnLike.setOnClickListener { viewModel.handleLike() }
            btnBookmark.setOnClickListener { viewModel.handleBookmark() }
            btnShare.setOnClickListener { viewModel.handleShare() }
            btnSettings.setOnClickListener { viewModel.handleToggleMenu() }

            btnResultUp.setOnClickListener {
                searchView.clearFocus()
                viewModel.handleUpResult()
            }

            btnResultDown.setOnClickListener {
                searchView.clearFocus()
                viewModel.handleDownResult()
            }

            btnSearchClose.setOnClickListener {
                viewModel.handleSearchMode(false)
                invalidateOptionsMenu()
            }
        }
    }

    override fun renderBotombar(data: BottombarData) {
        with(vbBottombar) {
            btnSettings.isChecked = data.isShowMenu
            btnLike.isChecked = data.isLike
            btnBookmark.isChecked = data.isBookmark
        }

        if (data.isSearch) showSearchBar(data.resultsCount, data.searchPosition)
        else hideSearchBar()

    }

    override fun renderSubmenu(data: SubmenuData) {
        with(vbSubmenu) {
            switchMode.isChecked = data.isDarkMode
            btnTextDown.isChecked = !data.isBigText
            btnTextUp.isChecked = data.isBigText
        }

        if (data.isShowMenu) vb.submenu.open() else vb.submenu.close()
    }

    override fun renderUi(data: ArticleState) {
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        with(vb.tvTextContent) {
            textSize = if (data.isBigText) 18f else 14f
            isLoading = data.content.isEmpty()
            setContent(data.content)
        }

        //bind toolbar
        with(vb.toolbar) {
            title = data.title ?: "loading"
            subtitle = data.category ?: "loading"
            if (data.categoryIcon != null) logo = getDrawable(data.categoryIcon as Int)
        }

        if (data.isLoadingContent) return

        if (data.isSearch) {
            renderSearchResult(data.searchResults)
            renderSearchPosition(data.searchPosition, data.searchResults)
        } else clearSearchResult()
    }

    override fun setupToolbar() {
        setSupportActionBar(vb.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = vb.toolbar.children.find { it is AppCompatImageView } as? ImageView
        logo ?: return
        logo.scaleType = ImageView.ScaleType.CENTER_CROP
        //check toolbar imports
        (logo.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = dpToIntPx(40)
            it.height = dpToIntPx(40)
            it.marginEnd = dpToIntPx(16)
            logo.layoutParams = it
        }

    }

    override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        vb.tvTextContent.renderSearchResult(searchResult)
    }

    override fun renderSearchPosition(searchPosition: Int, searchResult: List<Pair<Int, Int>>) {
        vb.tvTextContent.renderSearchPosition(searchResult.getOrNull(searchPosition))
    }

    override fun clearSearchResult() {
        vb.tvTextContent.clearSearchResult()
    }

    override fun showSearchBar(resultsCount: Int, searchPosition: Int) {
        with(vb.bottombar) {
            setSearchState(true)
            setSearchInfo(resultsCount, searchPosition)
        }
        vb.scroll.setMarginOptionally(bottom = dpToIntPx(56))
    }

    override fun hideSearchBar() {
        with(vb.bottombar) {
            setSearchState(false)
        }
        vb.scroll.setMarginOptionally(bottom = dpToIntPx(0))
    }

    override fun setupCopyListener() {
        vb.tvTextContent.setCopyListener { copy ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied code", copy)
            clipboard.setPrimaryClip(clip)
            viewModel.handleCopyCode()
        }
    }
}