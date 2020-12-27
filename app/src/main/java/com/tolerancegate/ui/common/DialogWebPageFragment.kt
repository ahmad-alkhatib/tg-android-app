package com.tolerancegate.ui.common

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import app.core.base.BaseDialogFragment
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_web_page.*

class DialogWebPageFragment : BaseDialogFragment() {

    private var urlPath = "https://docs.google.com/gview?embedded=true&url="
    private var url = ""
    private var title = ""

    override fun getTheme(): Int {
        return R.style.StateListDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        return inflater.inflate(R.layout.dialog_web_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.addActionForLeftIcon {
            dismiss()
        }

        headerView.setTitle(title)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (refreshLayout != null) {
                    refreshLayout.isRefreshing = true
                    refreshLayout.isEnabled = true
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (refreshLayout != null) {
                    refreshLayout.isRefreshing = false
                    refreshLayout.isEnabled = false
                }
            }
        }

        webView.loadUrl(urlPath + url.replace("%20", " "))
    }

    fun setPageUrl(url: String) {
        this.url = url
    }

    fun setTitle(title: String) {
        this.title = title
    }

    companion object {
        @JvmStatic
        fun instance() = DialogWebPageFragment()
    }
}