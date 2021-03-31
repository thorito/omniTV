package com.sivsa.omnitv.tools

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ToolsImage(private val context: Context) {

    suspend fun getDrawableIcon(@DrawableRes iconDefault: Int,
                                urlImage: String? = null): Drawable? = suspendCancellableCoroutine { c ->

        val ctx = context.applicationContext
        if (urlImage.isNullOrBlank()) {
            c.resume(ContextCompat.getDrawable(ctx, iconDefault))
        } else {
            Glide.with(ctx)
                .asDrawable()
                .load(urlImage)
                .apply(RequestOptions().override(250, 250))
                .circleCrop()
                .placeholder(iconDefault)
                .into(object: CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        c.resume(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        c.resume(placeholder)
                    }
                })
        }
    }
}