package org.nudt.player.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.R
import org.nudt.player.adapter.VideoPagingAdapter
import org.nudt.player.databinding.ActivitySearchBinding
import org.nudt.player.ui.VideoViewModel

class SearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }

    private val videoViewModel: VideoViewModel by viewModel()

    private lateinit var adapter: VideoPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initSearchBar()
        initResult()
    }

    private fun initSearchBar() {
        binding.apply {
            ivBack.setOnClickListener { finish() }

            etKeyword.addTextChangedListener {
                val keyword = it.toString()
                if (keyword.isEmpty()) {
                    ivClear.visibility = View.GONE
                    tvSearch.text = getString(R.string.text_cancel)
                } else {
                    ivClear.visibility = View.VISIBLE
                    tvSearch.text = getString(R.string.text_search)
                }
            }
            ivClear.setOnClickListener {
                etKeyword.text.clear()
            }

            tvSearch.setOnClickListener {
                if (etKeyword.text.toString().isEmpty()) {
                    finish()
                } else {
                    startSearch()
                }
            }

            etKeyword.setOnEditorActionListener { view, actionId, event ->
                // 软键盘上是搜索键
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    startSearch()
                    return@setOnEditorActionListener true
                } else return@setOnEditorActionListener false
            }
            etKeyword.requestFocus()
        }

    }

    private fun initResult() {
        adapter = VideoPagingAdapter(this@SearchActivity, videoViewModel)
        binding.rvVideo.adapter = adapter
        binding.rvVideo.layoutManager = GridLayoutManager(this@SearchActivity, 2)
    }

    /**
     * 点击搜索btn或者软键盘上的搜索按钮
     */
    private fun startSearch() {
        binding.etKeyword.clearFocus()

        // 隐藏软键盘
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etKeyword.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        val keyword = binding.etKeyword.text.toString()
        lifecycleScope.launchWhenCreated {
            launch {
                // 更新页面数据
                videoViewModel.bindSearchPage(keyword).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }
}