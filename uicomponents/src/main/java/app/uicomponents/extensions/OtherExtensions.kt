package app.uicomponents.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Patterns
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import app.apirequest.parse.Property
import app.apirequest.parse.Property.FULL_NAME
import app.apirequest.parse.Property.IMAGE
import app.utilities.Constant
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword() = this.length in 6..10

fun ParseObject.getNameLocale(): String {
    return getStrLocale("name")
}

fun ParseObject.getNameInSmall(): String {
    return this.getNameLocale().toLowerCase(Locale.getDefault())
}

fun ParseObject.getTitleInSmall(): String {
    return this.getStr("title").toLowerCase(Locale.getDefault())
}

fun ParseObject.getStr(key: String): String {
    return if (this.has(key)) this[key] as? String ?: "" else ""
}

fun ParseObject.getNameWithDegree(): String {
    var degree = this.getObject("degree")?.getStrLocale("shortForm") ?: ""
    var userName = this.getStr(FULL_NAME)
    return if (degree.isEmpty()) userName else "$degree $userName"
}

fun ParseObject.getStr(key: String, default:String): String {
    return if (this.has(key)) this[key] as? String ?: default else default
}

fun ParseObject.getAddress(): String {
    var country = this.getObject("country")?.getNameLocale() ?: ""
    var city = this.getObject("city")?.getNameLocale() ?: ""
    return if (city.isEmpty()) country else "$city, $country"
}

fun ParseObject.getLanguage() = this.getObject("language")?.getNameLocale() ?: ""

fun ParseObject.getStrLocale(key: String): String {
    return if (this.has(key + Constant.appLanguageKey)) this[key + Constant.appLanguageKey] as? String ?: "" else ""
}

fun ParseObject.getImageUrl(key: String = IMAGE): String {
    return if (this.has(key)) (this[key] as? ParseFile)?.url ?: "" else ""
}

fun ParseUser.getStr(key: String): String {
    return if (this.has(key)) this[key] as? String ?: "" else ""
}

fun ParseUser.getBool(key: String): Boolean {
    return if (this.has(key)) this[key] as? Boolean == true else false
}

fun ParseObject.getBool(key: String): Boolean {
    return if (this.has(key)) this[key] as? Boolean == true else false
}

fun ParseUser.getStrLocale(key: String): String {
    return if (this.has(key + Constant.appLanguageKey)) this[key + Constant.appLanguageKey] as? String ?: "" else ""
}

fun AppCompatImageView.setCircleBitmap(bmp: Bitmap?) {
    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, bmp)
    circularBitmapDrawable.isCircular = true
    this.setImageDrawable(circularBitmapDrawable)
}

fun Bitmap.getByte(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 50, stream)
    return stream.toByteArray()
}

fun File.toBitmap(): Bitmap {
    return BitmapFactory.decodeFile(this.path)
}

fun MutableList<ParseObject>.toStr(): String {
    return this.joinToString(separator = ", ") { it.getNameLocale() }
}

fun ParseObject.getObjects(key: String): MutableList<ParseObject> {
    return this.get(key) as? MutableList<ParseObject> ?: mutableListOf()
}

fun ParseObject.getObjectAsList(key: String): MutableList<ParseObject> {
    this.getObject(key)?.let {
        return listOf(this.getObject(key)) as MutableList<ParseObject>
    }
    return mutableListOf()
}

fun ParseObject.getObject(key: String): ParseObject? {
    return this.get(key) as? ParseObject
}

fun ParseObject.getObjectIds(key: String): List<String> {
    return if (this.has(key)) this.get(key) as? List<String> ?: listOf() else listOf()
}

fun ParseUser.getObjectIds(key: String): List<String> {
    return if (this.has(key)) this.get(key) as? List<String> ?: listOf() else listOf()
}

fun ParseObject.getPTFirstSubject(key: String): String {
    var subjectList = this.getObjects(key)
    if(!subjectList.isNullOrEmpty()){
        return subjectList[0].getNameLocale()
    }
    return ""
}

fun Date.toStr(): String {
    val format = SimpleDateFormat("dd MMMM yyyy")
    return format.format(this)
}

fun Date.toStrFormat(): String {
    val format = SimpleDateFormat("dd-MMM-yyyy")
    return format.format(this)
}
