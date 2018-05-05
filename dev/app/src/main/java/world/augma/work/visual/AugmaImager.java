package world.augma.work.visual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.signature.ObjectKey;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.Signature;

import world.augma.R;
import world.augma.asset.AugmaVisualType;
import world.augma.asset.effects.CircleCropTransformation;


@GlideModule
public class AugmaImager extends AppGlideModule {

    public static void set(AugmaVisualType type, Context context, ImageView imageView, S3UrlBuilder path) {
        process(type, GlideApp.with(context).load(path)/*.signature(new ObjectKey())*/, imageView);
    }

    public static void set(AugmaVisualType type, Context context, ImageView imageView, String path) {
        Log.e("IMAGER SET", "PATH: " + path.toString());
        process(type, GlideApp.with(context).load(path)/*.signature(new ObjectKey())*/, imageView);
    }

    public static void set(AugmaVisualType type, Context context, ImageView imageView, int resource) {
        process(type, GlideApp.with(context).load(resource)/*.signature(new ObjectKey())*/, imageView);
    }

    private static void process(AugmaVisualType type, GlideRequest requestBuilder, ImageView imageView) {

        GlideApp.getPhotoCacheDir(imageView.getContext()).delete();

        switch (type) {
            case PROFILE:
                requestBuilder
                        .priority(Priority.HIGH)
                        .transform(new CircleCropTransformation())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                break;

            case BACKGROUND:
                requestBuilder
                        .priority(Priority.HIGH)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
                break;

            case NOTE:
                break;
        }

        requestBuilder
                .fallback(R.drawable.image_does_not_exist)
                .error(R.drawable.error_loading_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

}

