package world.augma.ui.profile;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import world.augma.R;
import world.augma.ui.widget.Wave;
import world.augma.work.ProfileImageTransformer;

/**
 * Created by Burak.
 */
public class UIProfile extends AppCompatActivity {

    private ImageView profileImage;
    private ImageView backgroundImage;
    private TextView userFullName;
    private TextView bioText;
    private TextView userLocation;
    private TextView bioHorizontalSeparator;
    private TextView postsHorizontalSeparator;
    private LinearLayout statDisplayLayout;
    private ConstraintSet extendedLayout, shrinkLayout;
    private ConstraintLayout mainLayout;
    private Wave bottomWave;

    private float y;

    public UIProfile() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_profile);

        profileImage = (ImageView) findViewById(R.id.profilePic);
        backgroundImage = (ImageView) findViewById(R.id.background_image);
        bioText = (TextView) findViewById(R.id.bio_text);
        userLocation = (TextView) findViewById(R.id.profile_user_location);
        userFullName = (TextView) findViewById(R.id.profile_user_full_name);
        bioHorizontalSeparator = (TextView) ((LinearLayout) findViewById(R.id.bioSeparator)).findViewById(R.id.horizontalSeparatorText);
        postsHorizontalSeparator = (TextView) ((LinearLayout) findViewById(R.id.postsSeparator)).findViewById(R.id.horizontalSeparatorText);
        mainLayout = (ConstraintLayout) findViewById(R.id.ui_profile_layout);
        statDisplayLayout = (LinearLayout) findViewById(R.id.stat_display);
        bottomWave = (Wave) findViewById(R.id.bottomWave);

        profileImage.bringToFront();
        bottomWave.setTopWaveColor(Color.parseColor("#ce0081"));
        extendedLayout = new ConstraintSet();
        shrinkLayout = new ConstraintSet();
        shrinkLayout.clone(this, R.layout.ui_profile_folded);
        extendedLayout.clone(mainLayout);
        y = -1;

        //Load background image
        Glide.with(this)
                .load("android.resource://world.augma/drawable/" + R.drawable.background_image)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(backgroundImage);

        //Load profile image in circular form -> with adjusted size multiplier
        Glide.with(this)
                .load(Uri.parse("android.resource://world.augma/drawable/" + R.drawable.profile_pic))
                .crossFade()
                .thumbnail(0.9f)
                .bitmapTransform(new ProfileImageTransformer(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImage);

        //TODO 115 Char sınırla, essay yazmasın...
        bioText.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum varius cursus bibendum. Proin vitae feugiat eros...");
        userFullName.setText(
                getString(R.string.demo_user_name)
                        .concat(" ")
                        .concat(getString(R.string.demo_user_surname)).trim());

        userLocation.setText("Bilkent");
        bioHorizontalSeparator.setText("Bio");
        postsHorizontalSeparator.setText("Posts");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                TransitionManager.beginDelayedTransition(mainLayout);
                if(event.getY() - y < 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomWave.setVisibility(View.INVISIBLE);
                            statDisplayLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                    shrinkLayout.applyTo(mainLayout);

                } else {
                    extendedLayout.applyTo(mainLayout);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomWave.setVisibility(View.VISIBLE);
                            statDisplayLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
                return true;
        }
        return false;
    }
}
