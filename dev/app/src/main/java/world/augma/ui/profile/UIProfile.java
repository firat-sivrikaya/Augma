package world.augma.ui.profile;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import world.augma.R;
import world.augma.ui.widget.Wave;
import world.augma.utils.ProfileImageTransformer;

/**
 * Created by Burak.
 */
public class UIProfile extends AppCompatActivity {

    private final int DEFAULT_WAVE_LEVEL = 55;

    private ImageView profileImage;
    private ImageView backgroundImage;
    private Wave waveOverlay;

    public UIProfile() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_profile);

        profileImage = (ImageView) findViewById(R.id.drawer_profilePic);
        backgroundImage = (ImageView) findViewById(R.id.drawer_background_image);

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

        waveOverlay = new Wave(UIProfile.this, null);
        ((LinearLayout.LayoutParams) waveOverlay.getLayoutParams()).topMargin = 780;
        addContentView(waveOverlay, waveOverlay.getLayoutParams());
    }
}
