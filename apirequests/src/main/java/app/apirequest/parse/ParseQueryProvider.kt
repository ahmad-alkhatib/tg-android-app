package app.apirequest.parse

import app.apirequest.parse.ClassName.COURSE
import app.apirequest.parse.ClassName.LECTURE
import app.apirequest.parse.ClassName.SIGN_UP_REQUIREMENT
import app.apirequest.parse.ClassName.USER
import app.apirequest.parse.Property.CITY
import app.apirequest.parse.Property.COUNTRY
import app.apirequest.parse.Property.PT_SEEKS_TO_LEARN
import app.apirequest.parse.Property.PT_TEACHING_MODE
import app.apirequest.parse.Property.PT_TUITION_TYPE
import app.apirequest.parse.Property.SUBJECTS_TAUGHT
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import javax.inject.Inject

class ParseQueryProvider @Inject internal constructor() {
    fun queryForSignUpRequirements(accountType: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(SIGN_UP_REQUIREMENT)
        query.whereEqualTo("isActive", true)
        query.whereEqualTo("accountType", accountType)
        query.orderByAscending("index")
        return query
    }

    fun queryForPTObjectDetails(objectIds: List<String>): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(USER)
        query.whereContainedIn("objectId", objectIds)
        query.include(PT_SEEKS_TO_LEARN)
        query.include(PT_TUITION_TYPE)
        query.include(PT_TEACHING_MODE)
        query.include(SUBJECTS_TAUGHT)
        return query
    }

    fun queryForGetUserDetails(userId: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(USER)
        query.include(SUBJECTS_TAUGHT)
        query.include(COUNTRY)
        query.include(CITY)
        query.include("city.country")
        query.whereEqualTo("objectId", userId)
        return query
    }

    fun queryForGetUserPTDetails(userId: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(USER)
        query.include("subjectsTaught")
        query.include("university")
        query.include("degree")
        query.include("programOfStudy")
        query.include("city")
        query.include("city.country")
        query.include("country")
        query.include("ptSeeksToLearn")
        query.include("ptTuitionType")
        query.include("ptTeachingMode")
        query.include("subjectsTaught")
        query.whereEqualTo("objectId", userId)
        return query
    }

    fun queryForGetCourseDetails(courseId: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(COURSE)
        query.include("language")
        query.include("instructor")
        query.include("instructor.speciality")
        query.include("instructor.university")
        query.include("instructor.degree")
        query.include("instructor.city")
        query.include("instructor.city.country")
        query.include("instructor.country")
        query.whereEqualTo("objectId", courseId)
        return query
    }

    fun queryForGetCourseDetailsForUpdate(courseId: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(COURSE)
        query.include("language")
        query.include("program")
        query.include("subject")
        query.include("language")
        query.include("instructor")
        query.whereEqualTo("objectId", courseId)
        return query
    }

    fun queryForGetCurrentUser(userId: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(USER)
        query.include("gender")
        query.include("country")
        query.include("city")
        query.include("city.country")
        query.include("degree")
        query.include("programOfStudy")
        query.include("subjectsTaught")
        query.include("institutionType")
        query.include("ptSeeksToLearn")
        query.include("ptTuitionType")
        query.include("ptTeachingMode")
        query.whereEqualTo("objectId", userId)
        return query
    }

    fun queryForGetUserBookmarks(objectIds: List<String>, hasLimit: Boolean = false): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(LECTURE)
        query.whereContainedIn("objectId", objectIds)
        query.whereEqualTo("isActive", true)
        query.whereNotEqualTo("isDeleted", true)
        query.include("course")
        query.include("course.instructor")
        query.include("course.instructor.degree")
        query.include("course.language")
        query.include("course.subject")
        query.include("course.speciality")
        query.include("course.speciality.instructorDegree")
        if (hasLimit) {
            query.limit = 5;
        }
        return query
    }

    fun queryForGetUserConversation(user: ParseUser): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>("Conversation")
        query.whereExists("lastMessageDate")
        query.whereEqualTo("userList", user)
        query.include("userList")
        query.include("lastMessage")
        query.include("user1")
        query.include("user2")
        query.orderByDescending("lastMessageDate")
        query.limit = 200
        return query
    }

    fun queryForGetUserFollowing(objectIds: List<String>): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(USER)
        query.whereContainedIn("objectId", objectIds)
        query.include("degree")
        query.include("speciality")
        query.include("city")
        query.include("country")
        query.limit = 5;
        return query
    }

    fun queryForGetUserFollowers(objectId: String): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>(USER)
        query.whereEqualTo("followings", objectId)
        query.include("degree")
        query.include("speciality")
        query.include("city")
        query.include("city.country")
        query.include("country")
        query.include("programOfStudy")
        query.include("country")
        query.limit = 5;
        return query
    }

    fun queryForGetCourses(query: ParseQuery<ParseObject>): ParseQuery<ParseObject> {
        query.whereNotEqualTo("isDeleted", true)
        query.include("instructor")
        query.include("instructor.degree")
        query.include("language")
        query.include("subject")
        query.include("speciality")
        query.include("speciality.instructorDegree")
        query.include("city.country")
        query.include("city")
        query.include("country")
        return query
    }

    fun queryForAllInstitutions(query: ParseQuery<ParseObject>): ParseQuery<ParseObject> {
        query.whereEqualTo("accountType", "institute")
        query.whereEqualTo("isApproved", true)
        query.whereEqualTo("isActive", true)
        query.whereEqualTo("isMembershipActive", true)
        query.include("university")
        query.include("country")
        query.include("city")
        return query
    }

    fun queryForAllInstructors(query: ParseQuery<ParseObject>): ParseQuery<ParseObject> {
        query.include("instructorDegree")
        query.include("subjectsTaught")
        query.include("degree")
        query.include("programOfStudy")
        query.include("speciality")
        query.include("city")
        query.include("city.country")
        query.include("country")
        query.include("ptSeeksToLearn")
        query.include("ptTuitionType")
        query.include("ptTeachingMode")
        return query
    }

    fun queryForInstitutionType(): ParseQuery<ParseObject> {
        val query = ParseQuery.getQuery<ParseObject>("InstitutionType")
        query.whereEqualTo("showInInstitutionList", true)
        query.orderByAscending("index")
        return query
    }
}