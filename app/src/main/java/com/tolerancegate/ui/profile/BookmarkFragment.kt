package com.tolerancegate.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.uicomponents.extensions.getObjectIds
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import kotlinx.android.synthetic.main.fragment_bookmark.*
import javax.inject.Inject

class BookmarkFragment : BaseCommonFragment() {

    @Inject
    lateinit var lectureListAdapter: LectureListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.lectures)
        showBackIcon()
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                lectureListAdapter.filterList(s.toString())
            }
        })

        callApiToGetData()

        refreshLayout.setOnRefreshListener {
            clearListAndCallData()
        }

        lectureListAdapter.addCheckBookmarkStatus(this::checkBookmarkStatus)
        lectureListAdapter.addOrRemoveBookmark(this::addOrRemoveBookmark)
    }

    private fun clearListAndCallData() {
        lectureListAdapter.setItemList(listOf())
        recyclerView.adapter = lectureListAdapter
        callApiToGetData()
    }

    private fun callApiToGetData() {
        refreshLayout.isRefreshing = true
        var objIds: List<String> = userViewModel.getCurrentUser().getObjectIds("lectureBookmarkList")
        commonViewModel.findUserBookmarks(objIds).observe(this, { response ->
            refreshLayout.isRefreshing = false
            if (response.isSuccessful && !response.data.isNullOrEmpty()) {
                lectureListAdapter.setItemList(response.data!!)
                recyclerView.adapter = lectureListAdapter
            }
        })
    }

    private fun addOrRemoveBookmark(imageView: ImageView, item: ParseObject) {
        showProgress()
        if (userViewModel.isContainsInList(item.objectId, "lectureBookmarkList")) {
            userViewModel.removeFromList(item.objectId, "lectureBookmarkList").observe(this) {
                hideProgress()
                if (it) {
                    imageView.setImageResource(R.drawable.icon_bookmark)
                }
            }
        } else {
            userViewModel.addInList(item.objectId,"lectureBookmarkList").observe(this) {
                hideProgress()
                if (it) {
                    imageView.setImageResource(R.drawable.icon_bookmark_filled)
                }
            }
        }
    }

    private fun checkBookmarkStatus(imageView: ImageView, item: ParseObject) {
        if (userViewModel.isContainsInList(item.objectId, "lectureBookmarkList")) {
            imageView.setImageResource(R.drawable.icon_bookmark_filled)
        } else {
            imageView.setImageResource(R.drawable.icon_bookmark)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookmarkFragment()
    }
}





