package com.tolerancegate.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_bottom_sheet_course.*

class DialogBottomSheetCourse : BottomSheetDialogFragment() {

    private var onButtonEditClick: () -> Unit = {}
    private var onButtonViewClick: () -> Unit = {}
    private var onButtonDeleteClick: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet_course, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewCourseBtn.setOnClickListener {
            onButtonViewClick()
            dismiss()
        }
        editCourseBtn.setOnClickListener {
            onButtonEditClick()
            dismiss()
        }
        deleteCourseBtn.setOnClickListener {
            onButtonDeleteClick()
            dismiss()
        }
    }

    fun addButtonEditAction(onButtonClick: () -> Unit) {
        this.onButtonEditClick = onButtonClick;
    }

    fun addButtonViewAction(onButtonClick: () -> Unit) {
        this.onButtonViewClick = onButtonClick;
    }

    fun addButtonDeleteAction(onButtonClick: () -> Unit) {
        this.onButtonDeleteClick = onButtonClick;
    }

    companion object {
        @JvmStatic
        fun instance() = DialogBottomSheetCourse()
    }
}