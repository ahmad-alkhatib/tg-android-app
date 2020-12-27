package app.core

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.android.synthetic.main.progress_dialog_fragment.view.*

open class ProgressDialogFragment : DaggerAppCompatDialogFragment() {

    internal var dialogMessage: CharSequence? = null
    internal var cancelable = false
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.progress_dialog_fragment, container, false)
        initView()
        applyCancelableFlag()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun applyCancelableFlag() {
        isCancelable = cancelable
        dialog!!.setCancelable(cancelable)
        dialog!!.setCanceledOnTouchOutside(cancelable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NO_TITLE,
            R.style.customDialogThemeSmall
        )
    }

    fun setMessage(message: String) {
        dialogMessage = message
    }

    private fun initView() {
        rootView.loader_message.text = dialogMessage
    }

    class Builder {

        private var dialogMessage: String? = null
        private var cancelable = false

        private var onCancelListener: DialogInterface.OnCancelListener? = null

        fun setDialogMessage(dialogMessage: String? = null): Builder {
            this.dialogMessage = dialogMessage
            return this
        }

        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener): Builder {
            this.onCancelListener = onCancelListener
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun build(): ProgressDialogFragment? {
            return newInstance(this, ProgressDialogFragment::class.java)
        }

        fun <T : ProgressDialogFragment> build(clazz: Class<T>): T? {
            return newInstance(this, clazz)
        }

        private fun <T : ProgressDialogFragment> newInstance(
            builder: Builder,
            clazz: Class<T>
        ): T? {
            var progressDialogFragment: T? = null
            try {
                progressDialogFragment = clazz.newInstance()
            } catch (e: Exception) {
            }

            if (progressDialogFragment == null) {
                return null
            }
            progressDialogFragment.cancelable = builder.cancelable
            progressDialogFragment.dialogMessage = builder.dialogMessage
            return progressDialogFragment
        }
    }
}
