package com.tolerancegate.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.ClassName.UPLOADED_USER_DOC
import app.apirequest.parse.Property.FILE
import app.apirequest.parse.Property.FILE_NAME
import app.apirequest.parse.Property.USER
import app.apirequest.parse.PropertyValue.FILE_JPEG
import app.core.base.BaseDialogFragment
import app.utilities.Constant.FILE_NAME_REGEX
import com.parse.ParseFile
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.ui.common.FileSelectionActivity.Companion.CAMERA_TO_OPEN
import com.tolerancegate.ui.common.FileSelectionActivity.Companion.GALLERY_TO_OPEN
import com.tolerancegate.ui.common.FileSelectionActivity.Companion.PDF_FILE_TO_OPEN
import kotlinx.android.synthetic.main.dialog_add_document.*
import kotlinx.android.synthetic.main.dialog_item_picker.headerView
import java.io.File
import javax.inject.Inject


class DialogAddDocument : BaseDialogFragment() {

    private var documentList: MutableList<Documents> = mutableListOf()
    private var selectedPosition: Int = -1

    @Inject
    lateinit var adapter: DocumentItemAdapter

    override fun getTheme(): Int {
        return R.style.StateListDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        return inflater.inflate(R.layout.dialog_add_document, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.addActionForLeftIcon {
            dismiss()
        }

        getDocumentListForUser()

        globalViewModel.fileSelectionLiveData.observe(this, {
            if (it != null && selectedPosition >= 0) {
                val regex = Regex(FILE_NAME_REGEX)
                documentList[selectedPosition].apply {
                    isSelected = true
                    filename = regex.replace(it.name, "")
                    file = it
                }
                adapter.notifyDataSetChanged()
                selectedPosition = -1
                globalViewModel.fileSelectionLiveData.postValue(null)
            }
        })

        uploadDocumentBtn.setOnClickListener {
            if (documentList.any { it.isSelected.not() }) {
                showMessageDialog(R.string.documents, R.string.please_select_documents)
            } else {
                uploadAllTheDocuments()
            }
        }
    }

    private fun setItemsForList() {
        adapter.setItemList(documentList)
        adapter.addActionForItemClick { onItemClick(it) }
        recyclerView.adapter = adapter
    }

    private fun onItemClick(position: Int) {
        if (documentList[position].isSelected) {
            documentList[position].apply {
                isSelected = false
                filename = ""
                file = null
            }
            adapter.notifyDataSetChanged()
        } else {
            showImagePicker(position)
        }
    }

    private fun showImagePicker(position: Int) {
        selectedPosition = position
        val addPhotoBottomDialogFragment = DialogBottomSheetMenu.instance()
        addPhotoBottomDialogFragment.addButtonCameraAction { openImageSelectionActivity(CAMERA_TO_OPEN) }
        addPhotoBottomDialogFragment.addButtonLibraryAction { openImageSelectionActivity(GALLERY_TO_OPEN) }
        addPhotoBottomDialogFragment.addButtonFileAction { openImageSelectionActivity(PDF_FILE_TO_OPEN) }
        activity?.let {
            addPhotoBottomDialogFragment.show(it.supportFragmentManager, "BOTTOM_SHEET_DIALOG")
        }
    }

    private fun openImageSelectionActivity(type: String) {
        activity?.let {
            FileSelectionActivity.start(it, type)
        }
    }

    private fun getDocumentListForUser() {
        globalViewModel.findDocumentListForUser(userViewModel.getAccountType()).observe(activity!!, {
            if (it.isSuccessful) {
                for (item in it.data!!) {
                    documentList.add(Documents(FILE_JPEG, null, item, false))
                }
                setItemsForList()
            } else {
                showApiError(R.string.documents, it.error)
            }
        })
    }

    private fun uploadAllTheDocuments() {
        showProgress()
        val parseObjects = mutableListOf<ParseObject>()
        val currentUser = userViewModel.getCurrentUser()
        for (item in documentList) {
            val parseObject = ParseObject(UPLOADED_USER_DOC)
            val parseFile = ParseFile(item.filename, item.file?.readBytes())
            parseObject.put(FILE, parseFile)
            parseObject.put(FILE_NAME, item.filename)
            parseObject.put(USER, currentUser)
            parseObjects.add(parseObject)
        }

        globalViewModel.saveParseObjects(parseObjects).observe(activity!!, {
            if (it.isSuccessful) {
                showMessageDialog(
                    R.string.documents_has_been_uploaded,
                    R.string.we_will_notify_you_once_review_process_is_completed_thank_you
                ) {
                    dismiss()
                }
                userViewModel.setStatusToUploaded()
            } else {
                showApiError(R.string.upload_error, it.error)
            }
            hideProgress()
        })
    }

    companion object {
        @JvmStatic
        fun instance() = DialogAddDocument()
    }
}

data class Documents(
    var filename: String,
    var file: File?,
    var fileObj: ParseObject,
    var isSelected: Boolean
)