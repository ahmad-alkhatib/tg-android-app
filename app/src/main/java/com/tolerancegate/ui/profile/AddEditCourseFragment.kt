package com.tolerancegate.ui.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import app.apirequest.parse.ClassName
import app.apirequest.parse.ClassName.NONE
import app.apirequest.parse.Property
import app.apirequest.parse.Property.IMAGE
import app.apirequest.parse.PropertyValue
import app.core.custom.CustomEditText
import app.uicomponents.extensions.*
import com.parse.ParseFile
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import com.tolerancegate.ui.common.DialogBottomSheetMenu
import com.tolerancegate.ui.common.DialogItemPicker
import com.tolerancegate.ui.common.FileSelectionActivity
import kotlinx.android.synthetic.main.fragment_add_edit_course.*

class AddEditCourseFragment : BaseCommonFragment() {

    var selectedImageView: AppCompatImageView? = null
    var bannerBitmap: Bitmap? = null
    var imageSelected = false
    var titleKey: Int = R.string.add_course
    var courseObject: ParseObject = ParseObject(NONE)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_edit_course, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBackIcon()
        setScreenTitle(titleKey)
        setSelectionFieldActions()

        globalViewModel.fileSelectionLiveData.observe(this, {
            if (it != null && selectedImageView != null && selectedImageView == courseBannerImage) {
                bannerBitmap = it.toBitmap()
                selectedImageView!!.setImageBitmap(bannerBitmap)
                selectedImageView = null
                globalViewModel.fileSelectionLiveData.postValue(null)
                imageSelected = true
            }
        })

        courseBannerImage.setOnClickListener {
            showImagePicker(courseBannerImage, bannerBitmap != null)
        }

        checkIsForUpdate()

        addCourseBtn.setOnClickListener {
            if (checkFormValidation()) {
                callToSaveOrUpdateObject()
            }
        }
    }

    private fun callToSaveOrUpdateObject() {
        showProgress()
        commonViewModel.saveOrUpdateObject(courseObject).observe(activity!!, {
            hideProgress()
            if (it.isSuccessful) {
                globalViewModel.courseListUpdated.postValue(true)
                onBackPressed()
            } else {
                showApiError(titleKey, it.error)
            }
        })
    }

    private fun checkIsForUpdate() {
        if (isForUpdate()) {
            doCallForGetDetails()
        }
    }

    private fun doCallForGetDetails() {
        showProgress()
        commonViewModel.getCourseDetailsForUpdate(getCourseID()).observe(this, {
            hideProgress()
            if (it.isSuccessful) {
                it.data?.let(this::setDetails)
            }
        })
    }

    private fun setDetails(parseObject: ParseObject) {
        courseObject = parseObject
        editTitle.setText(courseObject.getStr("title"))
        editLanguage.setItems(courseObject.getObjectAsList("language"))
        editSubject.setItems(courseObject.getObjectAsList("subject"))
        editDescription.setText(courseObject.getStr("description"))
        switchActive.isChecked = courseObject.getBool("isActive")
        loadImageFromServerCrop(courseObject.getImageUrl("institutionImage1"), courseBannerImage, R.drawable.image_placeholder)
        imageSelected = true
    }

    private fun showImagePicker(view: AppCompatImageView, showDelete: Boolean) {
        selectedImageView = view
        val addPhotoBottomDialogFragment = DialogBottomSheetMenu.instance()
        addPhotoBottomDialogFragment.addButtonCameraAction { openImageSelectionActivity(FileSelectionActivity.CAMERA_TO_OPEN) }
        addPhotoBottomDialogFragment.addButtonLibraryAction { openImageSelectionActivity(FileSelectionActivity.GALLERY_TO_OPEN) }
        if (showDelete) {
            addPhotoBottomDialogFragment.addButtonDeleteAction { deleteImage() }
        }
        activity?.let {
            addPhotoBottomDialogFragment.show(it.supportFragmentManager, "BOTTOM_SHEET_DIALOG")
        }
    }

    private fun deleteImage() {
        selectedImageView?.let {
            it.setImageBitmap(null)
            it.setImageResource(R.drawable.image_placeholder)
            bannerBitmap = null
            imageSelected = false
        }
    }

    private fun openImageSelectionActivity(type: String) {
        activity?.let {
            FileSelectionActivity.start(it, type)
        }
    }

    private fun setSelectionFieldActions() {
        editLanguage.addClickAction { showPickerDialog(it) }
        editSubject.addClickAction { showPickerDialog(it) }
    }

    private fun showPickerDialog(customEditText: CustomEditText) {
        activity?.let {
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.show(it.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    private fun isForUpdate(): Boolean {
        return getCourseID().isNotEmpty()
    }

    private fun getCourseID(): String {
        arguments?.let {
            return it.getString(COURSE_ID, "")
        }
        return ""
    }

    private fun checkFormValidation(): Boolean {
        if (!isForUpdate()) {
            courseObject = ParseObject(ClassName.COURSE)
        }

        return checkFieldsValidation()
    }

    private fun checkIsImageSelected(): Boolean {
        if (!imageSelected) {
            showMessageDialog(titleKey, R.string.please_select_an_image)
            return false
        }
        return true
    }

    private fun checkFieldsValidation(): Boolean {
        if (isValid(editTitle) && checkIsImageSelected() && isValid(editLanguage)
            && isValid(editSubject) && isValid(editDescription)
        ) {
            courseObject.put("title", editTitle.getText())
            courseObject.put("description", editDescription.getText())
            courseObject.put("language", editLanguage.getSingleObject())
            courseObject.put("subject", editSubject.getSingleObject())
            courseObject.put("instructor", userViewModel.getCurrentUser())
            courseObject.put("isActive", switchActive.isChecked)
            bannerBitmap?.let {
                var parseFile = ParseFile(PropertyValue.FILE_JPEG, it.getByte());
                parseFile.save()
                courseObject.put(IMAGE, parseFile)
            }

            return true
        }
        return false
    }

    private fun isValid(customEditText: CustomEditText) = isFieldValid(titleKey, customEditText)

    companion object {
        @JvmStatic
        fun newInstance(courseId: String = "") = AddEditCourseFragment().apply {
            arguments = Bundle().apply {
                putString(COURSE_ID, courseId)
            }
        }

        const val COURSE_ID = "course_id"
    }
}





