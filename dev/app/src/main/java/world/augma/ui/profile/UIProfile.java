package world.augma.ui.profile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.asset.User;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.ui.widget.Wave;
import world.augma.work.AWS;
import world.augma.work.ProfileImageTransformer;
import world.augma.work.S3;

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
    private ServiceUIMain serviceUIMain;
    private static final int RESULT_LOAD_IMAGE = 1;
    private float y;
    private User user ;

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

        serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getActivity();
        user =  serviceUIMain.fetchUser();

        S3.fetchBackgroundImage(this,backgroundImage, "android.resource://world.augma/drawable/" + R.drawable.background_image);
        S3.fetchProfileImage(this, profileImage, user.getUserID());

        //Load background image
        /*

        Glide.with(this)
                .load("android.resource://world.augma/drawable/" + R.drawable.background_image)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(backgroundImage);

        */

        //Load profile image in circular form -> with adjusted size multiplier
        /*
        Glide.with(this)

                .load(Uri.parse("android.resource://world.augma/drawable/" + R.drawable.profile_pic))
                .crossFade()
                .thumbnail(0.9f)
                .bitmapTransform(new ProfileImageTransformer(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImage);

                */

        //TODO 115 Char sınırla, essay yazmasın...
        bioText.setText(user.getBio());
        userFullName.setText(user.getName());

        userLocation.setText("Bilkent");

        //*******
        bioHorizontalSeparator.setText("Bio");
        postsHorizontalSeparator.setText("Posts");
        profileImage.setOnClickListener(new ProfileClickListener());
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

    private void updateUserProfile(String userID)
    {
        AWS aws = new AWS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            //TODO image boyutu kontrol et ve image'i daire seklinde göster
            profileImage.setImageURI(selectedImage); //TODO image i augma klasorune koy sonra S3 fetch profileimage la set et
            BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            byte[] bb = bos.toByteArray();
            //TODO Needs Fix
            if(S3.uploadProfileImage(this.getApplicationContext(),bb,user.getUserID())){
                S3.fetchProfileImage(this,profileImage,user.getUserID());
            }

        }
    }

    private class ProfileClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(view == profileImage) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        }
    }
}
