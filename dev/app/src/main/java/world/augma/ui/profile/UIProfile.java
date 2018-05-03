package world.augma.ui.profile;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import world.augma.R;
import world.augma.asset.User;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.ui.widget.Wave;
import world.augma.work.AWS;
import world.augma.work.S3;

/**
 * Created by Burak.
 */
public class UIProfile extends AppCompatActivity {

    private static final int GALLERY_COLUMN_COUNT = 3;

    private ImageView profileImage;
    private ImageView backgroundImage;
    private List<ImageView> notes;  //TODO Bu liste sonra Note listesi olacak
    private TextView userFullName;
    private TextView bioText;
    private TextView userLocation;
    private TextView bioHorizontalSeparator;
    private TextView postsHorizontalSeparator;
    private TextView statDisplayPosts;
    private TextView statDisplayLikes;
    private TextView statDisplayCircles;
    private RecyclerView recyclerView;
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
        recyclerView = (RecyclerView) ((CardView) findViewById(R.id.galleryParent)).findViewById(R.id.gallery);
        statDisplayCircles = (TextView) ((LinearLayout) statDisplayLayout.findViewById(R.id.numOfCircles)).findViewById(R.id.numOfCirclesNumText);
        statDisplayLikes = (TextView) ((LinearLayout) statDisplayLayout.findViewById(R.id.numOfLikes)).findViewById(R.id.numOfLikesNumText);
        statDisplayPosts = (TextView) ((LinearLayout) statDisplayLayout.findViewById(R.id.numOfPosts)).findViewById(R.id.numOfPostsNum);

        profileImage.bringToFront();
        bottomWave.setTopWaveColor(Color.parseColor("#005DAF"));
        extendedLayout = new ConstraintSet();
        shrinkLayout = new ConstraintSet();
        shrinkLayout.clone(this, R.layout.ui_profile_folded);
        extendedLayout.clone(mainLayout);
        y = -1;

        serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
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

        //TODO Galeriyi başlatmak için aşağıdaki commentleri kaldır
        recyclerView.setAdapter(new GridLayoutAdapter());

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(GALLERY_COLUMN_COUNT, StaggeredGridLayoutManager.VERTICAL));

        statDisplayLikes.setText(""+user.getRating());
        statDisplayCircles.setText(""+user.getMemberships().size());
        statDisplayPosts.setText(""+user.getOwnedNotes().size());

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

            profileImage.setImageURI(selectedImage);
            BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            byte[] bb = bos.toByteArray();
            if(S3.uploadProfileImage(this.getApplicationContext(),bb,user.getUserID())){
                S3.fetchProfileImage(this,profileImage,user.getUserID());
                serviceUIMain.updateHeader();
            }
        }
    }

    private class ProfileClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if(view == profileImage) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        }
    }

    private class GalleryItem extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public GalleryItem(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_item_image);
        }
    }

    private class GridLayoutAdapter extends RecyclerView.Adapter<GalleryItem> {

        @NonNull
        @Override
        public GalleryItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GalleryItem(LayoutInflater.from(UIProfile.this).inflate(R.layout.gallery_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryItem holder, int position) {
            holder.imageView.requestLayout();
            S3.fetchProfileImage(UIProfile.this,holder.imageView,user.getUserID()); //TODO buradan noteların imageları girilecek
        }

        @Override
        public int getItemCount() {
            return user.getOwnedNotes().size();
        }
    }
}
