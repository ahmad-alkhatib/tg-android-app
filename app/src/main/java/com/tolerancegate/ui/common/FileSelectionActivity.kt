package com.tolerancegate.ui.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import app.core.base.BaseActivity
import app.utilities.PathUtil
import com.tolerancegate.BuildConfig
import com.tolerancegate.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileSelectionActivity : BaseActivity() {

    private var selectedOption: String = CAMERA_TO_OPEN
    private val GALLERY_REQUEST = 120
    private val CAMERA_REQUEST = 100
    private val PDF_REQUEST = 130

    private val IMAGE_SIZE = 400

    private val PERMISSION_CODE_CAMERA = 45
    private val PERMISSION_CODE_GALARRY = 46

    private var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        selectedOption = intent?.extras?.getString(TO_OPEN_KEY) ?: CAMERA_TO_OPEN
        // check permission storage before open camera or Gallery
        if ((selectedOption == CAMERA_TO_OPEN)
            && Build.VERSION.SDK_INT > LOLLIPOP
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_CODE_CAMERA)
        } else if ((selectedOption != CAMERA_TO_OPEN)
            && Build.VERSION.SDK_INT > LOLLIPOP
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE_GALARRY
            )
        } else {
            checkAndCapture()
        }
    }

    // either open camera or Gallery
    private fun checkAndCapture() {
        if (selectedOption == CAMERA_TO_OPEN) {
            captureImage()
        } else if (selectedOption == GALLERY_TO_OPEN) {
            selectImage()
        } else if (selectedOption == PDF_FILE_TO_OPEN) {
            selectPDFFile()
        }
    }

    // Open PDF picker
    private fun selectPDFFile() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "application/pdf"
//        }
//
//        startActivityForResult(intent, PDF_REQUEST)

        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/pdf"
        startActivityForResult(intent, PDF_REQUEST)
//        startActivity(intent)
    }

    // Open gallery
    private fun selectImage() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, GALLERY_REQUEST)
    }

    // open camera to capture image
    private fun captureImage() {
        // You should add provide in manifest and its path in XML
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg")
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(mContext!!, BuildConfig.APPLICATION_ID + ".provider", file)
        )
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    // get image URI (image complete url from storage)
    private fun getRealPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(mContext!!, contentUri!!, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    // check image rotation and set as straight
    @Throws(IOException::class)
    fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String?): Bitmap {
        try {
            val ei = ExifInterface(image_absolute_path)
            return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, false)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, true)
                else -> bitmap
            }
        } catch (ignored: Exception) {
        }
        return bitmap
    }

    // if rotation is not correct then rotate image as original
    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return bmp
    }

    // some time need to flip image for front camera
    fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1.0f else 1.0f), if (vertical) -1.0f else 1.0f)
        val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return bmp
    }

    // resize image for perticular size need to upload on server because large device images are the size of MBs
    fun Resize(originalBMP: Bitmap, reqSize: Int): Bitmap {
        val oriW = originalBMP.width.toFloat()
        val oriH = originalBMP.height.toFloat()
        val height: Float
        val width: Float
        if (oriH > oriW) {
            val ratio = oriH / oriW
            height = reqSize * ratio
            width = height / ratio
        } else if (oriW > oriH) {
            val ratio = oriW / oriH
            width = reqSize * ratio
            height = width / ratio
        } else {
            height = reqSize.toFloat()
            width = reqSize.toFloat()
        }
        return Bitmap.createScaledBitmap(originalBMP, width.toInt(), height.toInt(), true)
    }

    // when ask for permission request here will come if user give or declined the permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE_GALARRY || requestCode == PERMISSION_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAndCapture()
            } else {
                showMessageDialog(
                    R.string.allow_permissions,
                    if (selectedOption == CAMERA_TO_OPEN) R.string.app_does_not_have_access_to_your_camera else R.string.app_does_not_have_access_to_your_photos
                ) { finish() }
            }
        }
    }

    // when user select or capture imahe the original image data comes here
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                val imagePath = getRealPathFromURI(data!!.data)
                finalizeSelectedImageAndFinish(
                    MediaStore.Images.Media.getBitmap(this.contentResolver, data.data),
                    imagePath
                )
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpg")
                val options = BitmapFactory.Options()
                options.inSampleSize = 4

                finalizeSelectedImageAndFinish(
                    BitmapFactory.decodeFile(Uri.fromFile(file).path, options),
                    file.absolutePath
                )
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        } else if (requestCode == PDF_REQUEST && resultCode == RESULT_OK) {
            try {
                val completePaths: String? = PathUtil.getRealPath(this, data?.data)
                var files = File(completePaths)
                postSelectedFile(files)
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        } else {
            finish()
        }
    }

    @Throws(IOException::class)
    private fun finalizeSelectedImageAndFinish(bitmap: Bitmap, image_absolute_path: String) {
        val bmp = modifyOrientation(bitmap, image_absolute_path)
        postSelectedFile(bitmapToFile(Resize(bmp, IMAGE_SIZE)))
    }

    private fun postSelectedFile(file: File) {
        globalViewModel.fileSelectionLiveData.postValue(file)
        finish()
    }

    private fun bitmapToFile(bmp: Bitmap): File {
        val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "image.jpeg")
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bmp.compress(CompressFormat.JPEG, 50, bos)
        val bitmapdata: ByteArray = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        return file
    }

    companion object {
        @JvmStatic
        fun start(activity: Activity, selectionType: String) {
            activity.startActivity(
                Intent(activity, FileSelectionActivity::class.java)
                    .putExtra(TO_OPEN_KEY, selectionType)
            )
        }

        val TO_OPEN_KEY = "to_open"
        val CAMERA_TO_OPEN = "camera"
        val GALLERY_TO_OPEN = "gallery"
        val PDF_FILE_TO_OPEN = "pdf_file"
    }
}