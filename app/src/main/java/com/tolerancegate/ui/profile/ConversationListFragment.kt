package com.tolerancegate.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.uicomponents.extensions.getObjectIds
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import kotlinx.android.synthetic.main.fragment_conversation.*
import javax.inject.Inject

class ConversationListFragment : BaseCommonFragment() {

    @Inject
    lateinit var conversationListAdapter: ConversationListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_conversation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenTitle(R.string.messages)
        showBackIcon()

        callApiToGetData()

        refreshLayout.setOnRefreshListener {
            clearListAndCallData()
        }
    }

    private fun clearListAndCallData() {
        conversationListAdapter.setItemList(listOf())
        recyclerView.adapter = conversationListAdapter
        callApiToGetData()
    }

    private fun callApiToGetData() {
        refreshLayout.isRefreshing = true
        commonViewModel.findUserConversation(userViewModel.getCurrentUser()).observe(this, { response ->
            refreshLayout.isRefreshing = false
            if (response.isSuccessful && !response.data.isNullOrEmpty()) {
                conversationListAdapter.setItemList(response.data!!)
                recyclerView.adapter = conversationListAdapter
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConversationListFragment()
    }
}





