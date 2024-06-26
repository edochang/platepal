package com.example.platepal.glide

import android.content.Context
import android.content.res.Resources
import android.text.Html
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.example.platepal.R
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.InputStream


@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // You can change this to make Glide more verbose
        builder.setLogLevel(Log.ERROR)
    }
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}

// Calling glideapp.with with the most specific Activity/Fragment
// context allows it to track lifecycles for your fetch
// https://stackoverflow.com/questions/31964737/glide-image-loading-with-application-context
object Glide {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private var glideOptions = RequestOptions ()
        // Options like CenterCrop are possible, but I like this one best
        // Evidently you need fitCenter or dontTransform.  If you use centerCrop, your
        // list disappears.  I think that was an old bug.
        .fitCenter()
        // Rounded corners are so lovely.
        .transform(RoundedCorners (20))

    private var glideDiscoverOptions = RequestOptions ()
        // Options like CenterCrop are possible, but I like this one best
        // Evidently you need fitCenter or dontTransform.  If you use centerCrop, your
        // list disappears.  I think that was an old bug.
        .fitCenter()
        // Rounded corners are so lovely.
        .transform(RoundedCorners (20))

    private fun fromHtml(source: String): String {
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    fun glideFetch(urlString: String, thumbnailURL: String, imageView: ImageView) {
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(fromHtml(urlString))
            .apply(glideOptions)
            .error(R.color.dark_orange)
            .override(width, height)
            .error(
                GlideApp.with(imageView.context)
                    .asBitmap()
                    .load(fromHtml(thumbnailURL))
                    .apply(glideOptions)
                    .error(R.color.dark_orange)
                    .override(500, 500)
            )
            .into(imageView)
    }

    fun fetchFromStorage(storageReference: StorageReference, imageView: ImageView) {
        // Layout engine does not know size of imageView
        // Hardcoding this here is a bad idea.  What would be better?
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(storageReference)
            .apply(glideOptions)
            .error(android.R.color.holo_red_dark)
            .override(width, height)
            .into(imageView)
        Log.d("Glide", "the image width is $width, image height is $height")
    }

    fun fetchFromStorageForDiscover(storageReference: StorageReference, imageView: ImageView) {
        // Layout engine does not know size of imageView
        // Hardcoding this here is a bad idea.  What would be better?
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(storageReference)
            .apply(glideDiscoverOptions)
            .apply(bitmapTransform(CropTransformation(3300, height, CropTransformation.CropType.CENTER)))
            .error(android.R.color.holo_red_dark)
            .override(width, height)
            .into(imageView)
        Log.d("Glide", "the image width is $width, image height is $height")
    }

    fun fetchFromLocal(photoFile: File, imageView: ImageView) {
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(photoFile.path)
            .apply(glideOptions)
            .error(android.R.color.holo_red_dark)
            .override(width, height)
            .into(imageView)
    }

    fun fetchFromStorageForProfile(storageReference: StorageReference, imageView: ImageView) {
        // Layout engine does not know size of imageView
        // Hardcoding this here is a bad idea.  What would be better?
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(storageReference)
            .apply(glideOptions)
            .apply(RequestOptions.circleCropTransform())
            .error(android.R.color.holo_red_dark)
            .override(width, height)
            .into(imageView)
        Log.d("Glide", "the image width is $width, image height is $height")
    }

    fun fetchFromLocalForProfile(photoFile: File, imageView: ImageView) {
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(photoFile.path)
            .apply(glideOptions)
            .apply(RequestOptions.circleCropTransform())
            .error(android.R.color.holo_red_dark)
            .override(width, height)
            .into(imageView)
    }
}
