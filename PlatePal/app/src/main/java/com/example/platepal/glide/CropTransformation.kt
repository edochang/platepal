package com.example.platepal.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.max

/**
 * Copyright (C) 2020 Wasabeef
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class CropTransformation @JvmOverloads constructor(
    private var width: Int,
    private var height: Int,
    cropType: CropType = CropType.CENTER
) :
    BitmapTransformation() {
    enum class CropType {
        TOP,
        CENTER,
        BOTTOM
    }

    private var cropType = CropType.CENTER

    init {
        this.cropType = cropType
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        width = if (width == 0) toTransform.getWidth() else width
        height = if (height == 0) toTransform.getHeight() else height
        val config =
            if (toTransform.getConfig() != null) toTransform.getConfig() else Bitmap.Config.ARGB_8888
        val bitmap = pool[width, height, config]
        bitmap.setHasAlpha(true)
        val scaleX = width.toFloat() / toTransform.getWidth()
        val scaleY = height.toFloat() / toTransform.getHeight()
        val scale = max(scaleX, scaleY)
        val scaledWidth = scale * toTransform.getWidth()
        val scaledHeight = scale * toTransform.getHeight()
        val left = (width - scaledWidth) / 2
        val top = getTop(scaledHeight)
        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        bitmap.density = toTransform.density
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(toTransform, null, targetRect, null)
        return bitmap
    }

    private fun getTop(scaledHeight: Float): Float {
        return when (cropType) {
            CropType.TOP -> 0F
            CropType.CENTER -> (height - scaledHeight) / 2
            CropType.BOTTOM -> height - scaledHeight
            else -> 0F
        }
    }

    override fun toString(): String {
        return "CropTransformation(width=$width, height=$height, cropType=$cropType)"
    }

    override fun equals(o: Any?): Boolean {
        return o is CropTransformation && o.width == width && o.height == height && o.cropType == cropType
    }

    override fun hashCode(): Int {
        return ID.hashCode() + width * 100000 + height * 1000 + cropType.ordinal * 10
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + width + height + cropType).toByteArray(CHARSET))
    }

    companion object {
        private const val VERSION = 1
        private const val ID = "jp.wasabeef.glide.transformations.CropTransformation." + VERSION
    }
}