package com.tolerancegate.ui.authentication

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import app.apirequest.parse.Property.ACCOUNT_TYPE
import app.apirequest.parse.Property.CITY
import app.apirequest.parse.Property.CONTACT_NUMBER
import app.apirequest.parse.Property.COUNTRY
import app.apirequest.parse.Property.DEGREE
import app.apirequest.parse.Property.FULL_NAME
import app.apirequest.parse.Property.GENDER
import app.apirequest.parse.Property.HAS_CITY
import app.apirequest.parse.Property.HAS_PRIVATE_TUTORING
import app.apirequest.parse.Property.IMAGE
import app.apirequest.parse.Property.INSTITUTION_DESCRIPTION
import app.apirequest.parse.Property.INSTITUTION_IMAGE1
import app.apirequest.parse.Property.INSTITUTION_TYPE
import app.apirequest.parse.Property.IS_ACTIVE
import app.apirequest.parse.Property.IS_SIGN_UP_COMPLETED
import app.apirequest.parse.Property.PROGRAM_OF_STUDY
import app.apirequest.parse.Property.PT_CURRENCY
import app.apirequest.parse.Property.PT_FEES_PER_HOUR
import app.apirequest.parse.Property.PT_SEEKS_TO_LEARN
import app.apirequest.parse.Property.PT_TEACHING_MODE
import app.apirequest.parse.Property.PT_TUITION_TYPE
import app.apirequest.parse.Property.PT_TUTORING_EXPERIENCE
import app.apirequest.parse.Property.PT_YEARS_OF_EXPERIENCE
import app.apirequest.parse.Property.SPECIALIZATION
import app.apirequest.parse.Property.SUBJECTS_TAUGHT
import app.apirequest.parse.PropertyValue
import app.apirequest.parse.PropertyValue.FILE_JPEG
import app.apirequest.parse.PropertyValue.INSTITUTE
import app.apirequest.parse.PropertyValue.INSTRUCTOR
import app.apirequest.parse.PropertyValue.STUDENT
import app.core.custom.CustomEditText
import app.uicomponents.extensions.*
import app.utilities.Constant
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import com.tolerancegate.R
import com.tolerancegate.ui.common.DialogBottomSheetMenu
import com.tolerancegate.ui.common.DialogItemPicker
import com.tolerancegate.ui.common.FileSelectionActivity
import com.tolerancegate.ui.common.FileSelectionActivity.Companion.CAMERA_TO_OPEN
import com.tolerancegate.ui.common.FileSelectionActivity.Companion.GALLERY_TO_OPEN
import com.tolerancegate.ui.home.HomeActivity
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFragment() {

    var selectedImageView: AppCompatImageView? = null
    var userBitmap: Bitmap? = null
    var bannerBitmap: Bitmap? = null
    var parseUser: ParseUser = ParseUser()
    var forSocialSignup = false
    var forProfileUpdate = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerView.updateStatusBarHeight(globalViewModel.getStatusBarHeight())

        if (isPTEnabled()) {
            ptSwitchButton.isChecked = true
            institutionRadioBtn.visibility = GONE
            instructorRadioBtn.setBackgroundResource(R.drawable.segmented_btn_right)
        }

        if (getRegistrationType().value == INSTITUTE) {
            institutionRadioBtn.isChecked = true
        } else if (getRegistrationType().value == INSTRUCTOR) {
            instructorRadioBtn.isChecked = true
        } else {
            studentRadioBtn.isChecked = true
        }

        setListeners()
        setSelectionFieldActions()

        globalViewModel.fileSelectionLiveData.observe(this, {
            if (it != null && selectedImageView != null) {

                when (selectedImageView) {
                    userProfileImage, instituteLogoImage -> {
                        userBitmap = it.toBitmap()
                        userProfileImage!!.setCircleBitmap(userBitmap)
                        instituteLogoImage!!.setImageBitmap(userBitmap)
                    }
                    instituteBannerImage -> {
                        bannerBitmap = it.toBitmap()
                        selectedImageView!!.setImageBitmap(bannerBitmap)
                    }
                }

                selectedImageView = null
                globalViewModel.fileSelectionLiveData.postValue(null)
            }
        })

        userProfileImage.setOnClickListener {
            showImagePicker(userProfileImage, userBitmap != null)
        }
        instituteLogoImage.setOnClickListener {
            showImagePicker(instituteLogoImage, userBitmap != null)
        }
        instituteBannerImage.setOnClickListener {
            showImagePicker(instituteBannerImage, bannerBitmap != null)
        }

        checkIsUserForUpdate()
    }

    private fun showConfirmation() {
        activity?.let {
            dialogProvider.showAndroidDialogMessage(
                it, getString(R.string.registration),
                getString(R.string.are_your_sure_that_you_want_to_cancel_registration)
            ) {
                userViewModel.logout()
                popFragment()
            }
        }
    }

    private fun checkIsUserForUpdate() {
        if (userViewModel.hasCurrentUser()) {
            forSocialSignup = userViewModel.isSocialSignUpNotCompleted()
            forProfileUpdate = forSocialSignup.not()
            setCurrentUserDetails()
        }
    }

    private fun setCurrentUserDetails() {
        registerNowBtn.setText(resources.getString(R.string.update))
        setScreenTitle(resources.getString(R.string.profile))
        editFullName.setText(userViewModel.getFullName())
        editPassword.remove()
        editEmail.setOptional(true)
        editPassword.setOptional(true)
        setProfileImage()

        if (forSocialSignup) {
            editEmail.remove()
        } else if (forProfileUpdate) {
            segmentGroup.visibility = GONE
            editEmail.setDisableView()
            editEmail.setText(userViewModel.getEmail())
            doCallForGetCurrentUserDetails()
        }
    }

    fun setProfileImage() {
        var profileImage = userViewModel.getImageUrl();
        loadCircleImage(profileImage, userProfileImage)
        loadImageFromServerCrop(profileImage, instituteLogoImage)
    }

    fun doCallForGetCurrentUserDetails() {
        showProgress()
        authViewModel.getCurrentUserObj(userViewModel.getUserId()).observe(this, {
            hideProgress()
            if (it.isSuccessful) {
                it.data?.let(this::setUserDetails)
            }
        })
    }

    private fun setUserDetails(user: ParseObject) {
        editCity.remove()
        editCountry.setItems(user.getObjectAsList("country"))
        user.getObject("city")?.let {
            editCity.show()
            editCity.setItems(mutableListOf(it))
        }
        editGender.setItems(user.getObjectAsList("gender"))
        editDegree.setItems(user.getObjectAsList("degree"))
        editProgramStudy.setItems(user.getObjectAsList("programOfStudy"))
        editSubject.setItems(user.getObjects("subjectsTaught"))
        editInstitutionType.setItems(user.getObjectAsList("institutionType"))
        editAboutInstitution.setText(user.getStr("institutionDescription"))
        editNumber.setText(user.getStr("contactNumber"))
        editSpecialization.setText(user.getStr("specialization"))
        ptSwitchButton.isChecked = user.getBool("hasPrivateTutoring")
        loadImageFromServerCrop(user.getImageUrl("institutionImage1"), instituteBannerImage, R.drawable.image_placeholder)
        editTeachingMode.setItems(user.getObjects("ptTeachingMode"))
        editSeeksToLear.setItems(user.getObjects("ptSeeksToLearn"))
        editTuitionType.setItems(user.getObjects("ptTuitionType"))
        editFeesPerHour.setText(user.getStr("ptFeesPerHour"))
        editFeesPerHour.setCurrency(user.getStr("ptCurrency"))
        editYearExperience.setText(user.getStr("ptYearsOfExperience"))
        editExperienceDescription.setText(user.getStr("ptTutoringExperience"))
    }

    private fun showImagePicker(view: AppCompatImageView, showDelete: Boolean) {
        selectedImageView = view
        val addPhotoBottomDialogFragment = DialogBottomSheetMenu.instance()
        addPhotoBottomDialogFragment.addButtonCameraAction { openImageSelectionActivity(CAMERA_TO_OPEN) }
        addPhotoBottomDialogFragment.addButtonLibraryAction { openImageSelectionActivity(GALLERY_TO_OPEN) }
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
            when (it) {
                userProfileImage, instituteLogoImage -> {
                    userBitmap = null
                    userProfileImage.setImageResource(R.drawable.image_profile_placeholder)
                    instituteLogoImage.setImageResource(R.drawable.image_placeholder)
                }
                instituteBannerImage -> {
                    bannerBitmap = null
                    it.setImageResource(R.drawable.image_placeholder)
                }
            }
        }
    }

    private fun openImageSelectionActivity(type: String) {
        activity?.let {
            FileSelectionActivity.start(it, type)
        }
    }

    private fun hideAllViews() {
        userProfileImage.visibility = GONE
        editGender.visibility = GONE
        editDegree.visibility = GONE
        editProgramStudy.visibility = GONE
        ptSwitchButton.visibility = GONE
        editSubject.visibility = GONE
        editSpecialization.visibility = GONE
        ptHeadingLayout.visibility = GONE
        editYearExperience.visibility = GONE
        editFeesPerHour.visibility = GONE
        editExperienceDescription.visibility = GONE
        editTeachingMode.visibility = GONE
        instituteLogoImageCard.visibility = GONE
        editInstitutionType.visibility = GONE
        instituteBannerImageCard.visibility = GONE
        editSeeksToLear.visibility = GONE
        editTuitionType.visibility = GONE
        editAboutInstitution.visibility = GONE
    }

    private fun visibleViewsForInstitution() {
        editFullName.setHintText(resources.getString(R.string.institution_name))
        instituteLogoImageCard.visibility = VISIBLE
        editInstitutionType.visibility = VISIBLE
        instituteBannerImageCard.visibility = VISIBLE
        editAboutInstitution.visibility = VISIBLE
    }

    private fun visibleViewsForStudent() {
        editFullName.setHintText(resources.getString(R.string.full_name))
        userProfileImage.visibility = VISIBLE
        editGender.visibility = VISIBLE
        editDegree.visibility = VISIBLE
        editProgramStudy.visibility = VISIBLE
        ptSwitchButton.visibility = VISIBLE
        if (ptSwitchButton.isChecked) {
            editSeeksToLear.visibility = VISIBLE
            editTuitionType.visibility = VISIBLE
        }
        editGender.setHintText(resources.getString(R.string.gender_optional))
        editDegree.setHintText(resources.getString(R.string.degree_optional))
    }

    private fun visibleViewsForInstructor() {
        editFullName.setHintText(resources.getString(R.string.full_name))
        userProfileImage.visibility = VISIBLE
        editGender.visibility = VISIBLE
        editDegree.visibility = VISIBLE
        editSpecialization.visibility = VISIBLE
        editSubject.visibility = VISIBLE
        ptSwitchButton.visibility = VISIBLE
        if (ptSwitchButton.isChecked) {
            editYearExperience.visibility = VISIBLE
            editFeesPerHour.visibility = VISIBLE
            editExperienceDescription.visibility = VISIBLE
            editTeachingMode.visibility = VISIBLE
        }
        editGender.setHintText(resources.getString(R.string.gender))
        editDegree.setHintText(resources.getString(R.string.degree))
    }

    private fun setListeners() {
        ptSwitchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            getRegistrationType().postValue(getRegistrationType().value)
        }

        getRegistrationType().observe(this, {
            hideAllViews()
            when (it) {
                INSTITUTE -> visibleViewsForInstitution()
                STUDENT -> visibleViewsForStudent()
                INSTRUCTOR -> visibleViewsForInstructor()
            }
        })

        segmentGroup.setOnCheckedChangeListener { radioGroup, radioButtonID ->
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(radioButtonID)
            when (selectedRadioButton) {
                studentRadioBtn -> getRegistrationType().postValue(STUDENT)
                instructorRadioBtn -> getRegistrationType().postValue(INSTRUCTOR)
                institutionRadioBtn -> getRegistrationType().postValue(INSTITUTE)
            }
        }

        registerNowBtn.setOnClickListener {
            if (checkFormValidation()) {
                if (isForUpdate()) {
                    callToUpdateUser()
                } else {
                    callToSaveNewUser()
                }
            }
        }

        headerView.addActionForLeftIcon {
            if (forSocialSignup) {
                showConfirmation()
            } else {
                popFragment()
            }
        }
    }

    private fun isForUpdate() = forProfileUpdate || forSocialSignup

    private fun callToUpdateUser() {
        showProgress()
        authViewModel.updateUser(parseUser).observe(activity!!, {
            hideProgress()
            if (it.isSuccessful) {
                if (forSocialSignup) {
                    navigateToHome()
                } else {
                    navigationViewModel.userUpdated.postValue(true)
                    activity?.finish()
                }
            } else {
                showApiError(R.string.login, it.error)
            }
        })
    }

    private fun callToSaveNewUser() {
        showProgress()
        authViewModel.registerNewUser(parseUser).observe(activity!!, {
            hideProgress()
            if (it.isSuccessful) {
                navigateToHome()
            } else {
                showApiError(R.string.login, it.error)
            }
        })
    }

    private fun navigateToHome() {
        activity?.let { activity -> HomeActivity.start(activity, true) }
    }

    private fun checkFormValidation(): Boolean {
        if (isForUpdate()) {
            parseUser = userViewModel.getCurrentUser()
        } else {
            parseUser = ParseUser()
        }

        // for student validation
        if (studentRadioBtn.isChecked) {
            return checkStudentFieldsValidation()
        }
        // for instructor validation
        else if (instructorRadioBtn.isChecked) {
            return checkInstructorFieldsValidation()
        }
        // for institution validation
        else if (institutionRadioBtn.isChecked) {
            return checkInstitutionFieldsValidation()
        }

        return false
    }

    private fun checkStudentFieldsValidation(): Boolean {
        if (checkCommonFieldsValidation(false) && checkPrivateTutoringFieldsValidation(true)) {
            parseUser.put(ACCOUNT_TYPE, PropertyValue.STUDENT)
            parseUser.put(HAS_PRIVATE_TUTORING, ptSwitchButton.isChecked)
            if (editGender.getSelectedObjects().isNotEmpty()) {
                parseUser.put(GENDER, editGender.getSingleObject())
            }
            if (editDegree.getSelectedObjects().isNotEmpty()) {
                parseUser.put(DEGREE, editDegree.getSingleObject())
            }
            if (editProgramStudy.getSelectedObjects().isNotEmpty()) {
                parseUser.put(PROGRAM_OF_STUDY, editProgramStudy.getSingleObject())
            }

            userBitmap?.let {
                var parseFile = ParseFile(FILE_JPEG, it.getByte());
                parseFile.save()
                parseUser.put(IMAGE, parseFile)
            }
            return true
        }
        return false
    }

    private fun checkInstructorFieldsValidation(): Boolean {
        if (checkCommonFieldsValidation(true) && isValid(editDegree) && isValid(editSpecialization)
            && isValid(editSubject) && checkPrivateTutoringFieldsValidation(false)
        ) {
            parseUser.put(ACCOUNT_TYPE, PropertyValue.INSTRUCTOR)
            parseUser.put(GENDER, editGender.getSingleObject())
            parseUser.put(DEGREE, editDegree.getSingleObject())
            parseUser.put(SPECIALIZATION, editSpecialization.getText())
            parseUser.put(SUBJECTS_TAUGHT, editSubject.getSelectedObjects())
            parseUser.put(HAS_PRIVATE_TUTORING, ptSwitchButton.isChecked)

            userBitmap?.let {
                var parseFile = ParseFile(FILE_JPEG, it.getByte());
                parseFile.save()
                parseUser.put(IMAGE, parseFile)
            }
            return true
        }
        return false
    }

    private fun checkPrivateTutoringFieldsValidation(student: Boolean): Boolean {
        if (ptSwitchButton.isChecked) {
            if (student && isValid(editSeeksToLear) && isValid(editTuitionType)) {
                parseUser.put(PT_SEEKS_TO_LEARN, editSeeksToLear.getSelectedObjects())
                parseUser.put(PT_TUITION_TYPE, editTuitionType.getSelectedObjects())
                return true
            }
            if (student.not() && isValid(editYearExperience) && isValid(editFeesPerHour) && isValid(editTeachingMode)) {
                parseUser.put(PT_CURRENCY, editFeesPerHour.getCurrency())
                parseUser.put(PT_FEES_PER_HOUR, editFeesPerHour.getText())
                parseUser.put(PT_TEACHING_MODE, editTeachingMode.getSelectedObjects())
                parseUser.put(PT_YEARS_OF_EXPERIENCE, editYearExperience.getText())
                parseUser.put(PT_TUTORING_EXPERIENCE, editExperienceDescription.getText())
                return true
            }
            return false
        } else {
            return true
        }
    }

    private fun checkCommonFieldsValidation(checkGender: Boolean): Boolean {
        if (isValid(editFullName) && isValid(editEmail, true) && isValid(editPassword)
            && isValid(editPassword, password = true) && (!checkGender || isValid(editGender))
            && isValid(editCountry) && (editCity.visibility != VISIBLE || isValid(
                editCity
            ))
        ) {
            setCommonValues()
            return true
        }
        return false
    }

    private fun checkInstitutionFieldsValidation(): Boolean {
        if (checkCommonFieldsValidation(false) && isValid(editInstitutionType)) {
            parseUser.put(INSTITUTION_DESCRIPTION, editAboutInstitution.getText())
            parseUser.put(INSTITUTION_TYPE, editInstitutionType.getSingleObject())
            parseUser.put(ACCOUNT_TYPE, INSTITUTE)

            userBitmap?.let {
                var parseFile = ParseFile(FILE_JPEG, it.getByte());
                parseFile.save()
                parseUser.put(IMAGE, parseFile)
            }

            bannerBitmap?.let {
                var parseFile = ParseFile(FILE_JPEG, it.getByte());
                parseFile.save()
                parseUser.put(INSTITUTION_IMAGE1, parseFile)
            }
            return true
        }
        return false
    }

    private fun setCommonValues() {
        if (!isForUpdate()) {
            parseUser.username = editEmail.getText()
            parseUser.email = editEmail.getText()
            parseUser.setPassword(editPassword.getText())
            parseUser.put("emailMasterPermission", true)
            parseUser.put("videoCallMasterPermission", true)
            parseUser.put("audioCallMasterPermission", true)
            parseUser.put("chatMasterPermission", true)
        }
        parseUser.put(FULL_NAME, editFullName.getText())
        parseUser.put(CONTACT_NUMBER, editNumber.getText())
        parseUser.put(COUNTRY, editCountry.getSingleObject())
        if (editCity.getSelectedObjects().isNotEmpty()) {
            parseUser.put(CITY, editCity.getSingleObject())
        }
        parseUser.put(IS_SIGN_UP_COMPLETED, true)
        parseUser.put(IS_ACTIVE, true)
        parseUser.put("deviceLanguage", Constant.appLanguageKey)
    }

    private fun setSelectionFieldActions() {
        editGender.addClickAction { showPickerDialog(it) }
        editCountry.addClickAction { showCountryPickerDialog(it) }
        editCity.addClickAction {
            if (editCountry.getText().isEmpty()) {
                showMessageDialog(R.string.registration, R.string.please_select_a_country_before_selecting_the_city)
            } else {
                showCityPickerDialog(it)
            }
        }
        editDegree.addClickAction { showPickerDialog(it) }
        editProgramStudy.addClickAction { showPickerDialog(it) }
        editSubject.addClickAction { showPickerDialog(it) }
        editTeachingMode.addClickAction { showPickerDialog(it) }
        editSeeksToLear.addClickAction { showPickerDialog(it) }
        editTuitionType.addClickAction { showPickerDialog(it) }
        editInstitutionType.addClickAction { showPickerDialog(it) }
    }

    private fun isValid(customEditText: CustomEditText, email: Boolean = false, password: Boolean = false): Boolean {
        return if (password) {
            isValidPassword(R.string.profile, customEditText)
        } else {
            isFieldValid(R.string.profile, customEditText, email)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }

    private fun showPickerDialog(customEditText: CustomEditText) {
        activity?.let {
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.show(it.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    private fun showCountryPickerDialog(customEditText: CustomEditText) {
        activity?.let { c ->
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.setCountryFlag(true)
            pickerDialogFragment.addActionForItemSelection { ocCountrySelected(it) }
            pickerDialogFragment.show(c.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    private fun ocCountrySelected(parseObject: ParseObject) {
        editCity.removeSelection()

        if (parseObject.getBool(HAS_CITY)) {
            editCity.visibility = VISIBLE
        } else {
            editCity.visibility = GONE
        }

        editFeesPerHour.setCurrency(parseObject["currency"] as? String ?: "$")
    }

    private fun showCityPickerDialog(customEditText: CustomEditText) {
        activity?.let {
            var pickerDialogFragment = DialogItemPicker.instance();
            pickerDialogFragment.setCustomEditText(customEditText)
            pickerDialogFragment.setCityFlag(true)
            pickerDialogFragment.setDependentEditText(editCountry)
            pickerDialogFragment.show(it.supportFragmentManager, "PICKER_DIALOG")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.setRegistrationForPT(false)
    }
}