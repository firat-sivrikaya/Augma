package world.augma.ui.note;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import world.augma.R;
import world.augma.asset.AugmaVisualType;
import world.augma.asset.effects.BlurEffect;
import world.augma.work.visual.AugmaImager;
import world.augma.work.visual.GlideApp;

public class UINoteDisplay extends AppCompatActivity {

    private ImageView profilePic;
    private ImageView notePic;
    private TextView note;
    private TextView userName;
    private ConstraintLayout noteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_display);

        profilePic = (ImageView) findViewById(R.id.noteProfilePic);
        notePic = (ImageView) findViewById(R.id.noteBackgroundBlurRender);
        noteLayout = (ConstraintLayout) findViewById(R.id.noteLayout);
        userName = (TextView) findViewById(R.id.noteUserNameText);
        note = (TextView) findViewById(R.id.noteText);

        //TODO Sonra sil
        note.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam dignissim, " +
                "nulla commodo venenatis malesuada, metus metus ult" +
                "ricies velit, eu sodales justo urna at massa. Donec" +
                " ac lorem dolor. Mauris leo augue, faucibus ac neque a, egestas " +
                "fermentum turpis. Phasellus lacinia varius lacus, a tempus felis sagittis eu.");
        userName.setText("Burcu Şahin");
        AugmaImager.set(AugmaVisualType.NOTE, this, profilePic, "android.resource://world.augma/drawable/" + R.drawable.profile_pic);

        GlideApp.with(this)
                .load("android.resource://world.augma/drawable/" + R.drawable.sample5)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        applyBlur(notePic, noteLayout);

                        /* TODO bu çalışıyor gibi sonra bak
                        Glide.with(activity)
    .load(photoUri)
    .asBitmap()
    .centerCrop()
    .into(new BitmapImageViewTarget(imageView) {
        @Override
        protected void setResource(Bitmap resource) {
            RoundedBitmapDrawable circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
            circularBitmapDrawable.setCircular(true);
            imageView.setImageDrawable(circularBitmapDrawable);
        }
    });
                         */







                        return false;
                    }
                })
                .into(notePic);

    }

    private void applyBlur(final ImageView img, final ConstraintLayout layout) {

        img.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                img.getViewTreeObserver().removeOnPreDrawListener(this);
                img.buildDrawingCache();
                BlurEffect.blurOverlay(UINoteDisplay.this, img.getDrawingCache(), layout);
                return true;
            }
        });
    }

    /*
    public void animView(final int p) {

        ValueAnimator topAnim = ObjectAnimator.ofInt(bottomSheetLayout, "top", bottomSheetLayout.getTop(), bottomSheetLayout.getTop() + p);
        topAnim.setDuration(2000);

        topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (background != null)
                    BlurEffect.blurOverlay(getApplicationContext(), background.getDrawingCache(), bottomSheetLayout);
            }
        });

        ValueAnimator bottomAnim = ObjectAnimator.ofInt(bottomSheetLayout, "bottom", bottomSheetLayout.getBottom(), bottomSheetLayout.getBottom() + p);
        bottomAnim.setDuration(2000);

        topAnim.start();
        bottomAnim.start();

        animP = -animP;
        topAnim.start();
        bottomAnim.start();
    }

    */

}



