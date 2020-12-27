package app.utilities

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object ImageLoaderUtils {

//    fun loadImageCrop(url: String, imgView: ImageView) {
//        loadImageFromServerCrop(url, imgView)
//    }
//
//    fun loadImageFit(url: String, imgView: ImageView) {
//        loadImageFromServerFit(url, imgView)
//    }
//
//    fun loadImage(url: String, imgView: ImageView) {
//        loadImageFromServer(url, imgView)
//    }
//
//    fun loadImageCropTop(url: String, imgView: ImageView) {
//        loadImageFromServerCropTop(url, imgView)
//    }
//
//    private fun loadImageFromServer(url: String, imgView: ImageView) {
//        Glide.with(imgView).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgView)
//    }
//
//    private fun loadImageFromServerCropTop(url: String, imgView: ImageView) {
//        Glide.with(imgView).load(url)
//            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgView)
//    }

    fun loadImageFromServerCrop(url: String, imgView: AppCompatImageView, placeholder: Int) {
        Glide.with(imgView).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder)
            .error(placeholder).into(imgView)
    }
//
//    private fun loadImageFromServerFit(url: String, imgView: ImageView) {
//        Glide.with(imgView).load(url).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(imgView)
//    }

    fun loadCircleImage(url: String, imgView: AppCompatImageView, placeholder: Int) {
        Glide.with(imgView).load(url).circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder)
            .error(placeholder).into(imgView)
    }
}