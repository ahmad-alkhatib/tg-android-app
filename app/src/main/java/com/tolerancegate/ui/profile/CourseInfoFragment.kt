package com.tolerancegate.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.apirequest.parse.ClassName
import app.apirequest.parse.Property
import app.uicomponents.extensions.*
import app.utilities.ImageLoaderUtils
import com.parse.ParseObject
import com.tolerancegate.R
import com.tolerancegate.ui.common.BaseCommonFragment
import kotlinx.android.synthetic.main.fragment_course_info.*

class CourseInfoFragment : BaseCommonFragment() {

    lateinit var course: ParseObject
    private lateinit var courseID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_course_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBackIcon()
        setScreenTitle(R.string.course_information)
        globalViewModel.selectedCourseId.observe(this) {
            if (!it.isNullOrEmpty()) {
                courseID = it
                getCourseDetails()
            }
        }
    }

    fun getCourseDetails() {
        showProgress()
        commonViewModel.getCourseDetails(courseID).observe(this, {
            hideProgress()
            if (it.isSuccessful) {
                it.data?.let {
                    course = it
                    showCourseDetails()
                }
            }
        })
    }

    private fun showCourseDetails() {
        var instructor = course.getObject("instructor") ?: ParseObject(ClassName.NONE)
        titleTv.text = course.getStr("title")
        userNameTv.text = instructor.getNameWithDegree()
        userDescTv.text = instructor.getStr("specialization")
        courseDetailListTv.text = course.getStr("description")
        languageTv.text = "${getString(R.string.language)}:"
        languageValueTv.text = course.getLanguage()
        dateTv.text = course.updatedAt.toStrFormat()

        ImageLoaderUtils.loadImageFromServerCrop(
            course.getImageUrl(),
            courseImage,
            R.drawable.image_placeholder_gray
        )

        ImageLoaderUtils.loadCircleImage(
            instructor.getImageUrl(),
            userImage,
            R.drawable.image_placeholder_user_gray
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = CourseInfoFragment()
    }
}





