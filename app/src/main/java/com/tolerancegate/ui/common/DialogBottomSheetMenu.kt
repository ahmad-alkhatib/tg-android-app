package com.tolerancegate.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tolerancegate.R
import kotlinx.android.synthetic.main.dialog_bottom_sheet.*

class DialogBottomSheetMenu : BottomSheetDialogFragment() {

    private var onButtonCameraClick: () -> Unit = {}
    private var onButtonLibraryClick: () -> Unit = {}
    private var onButtonDeleteClick: () -> Unit = {}
    private var onButtonFileClick: () -> Unit = {}
    private var showButtonDelete = false
    private var showButtonFile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraBtn.setOnClickListener {
            onButtonCameraClick()
            dismiss()
        }
        libraryBtn.setOnClickListener {
            onButtonLibraryClick()
            dismiss()
        }
        if (showButtonDelete) {
            deleteBtn.visibility = VISIBLE
            deleteBtn.setOnClickListener {
                onButtonDeleteClick()
                dismiss()
            }
        }
        if (showButtonFile) {
            fileBtn.visibility = VISIBLE
            fileBtn.setOnClickListener {
                onButtonFileClick()
                dismiss()
            }
        }
    }

    fun addButtonCameraAction(onButtonClick: () -> Unit) {
        this.onButtonCameraClick = onButtonClick;
    }

    fun addButtonLibraryAction(onButtonClick: () -> Unit) {
        this.onButtonLibraryClick = onButtonClick;
    }

    fun addButtonDeleteAction(onButtonClick: () -> Unit) {
        showButtonDelete = true
        this.onButtonDeleteClick = onButtonClick;
    }

    fun addButtonFileAction(onButtonClick: () -> Unit) {
        showButtonFile = true
        this.onButtonFileClick = onButtonClick;
    }

    companion object {
        @JvmStatic
        fun instance() = DialogBottomSheetMenu()
        const val NAME = "BOTTOM_SHEET_MENU"
    }
}