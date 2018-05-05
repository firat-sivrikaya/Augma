package world.augma.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.irozon.alertview.AlertActionStyle;
import com.irozon.alertview.AlertStyle;
import com.irozon.alertview.AlertView;
import com.irozon.alertview.interfaces.AlertActionListener;
import com.irozon.alertview.objects.AlertAction;
import com.ms_square.etsyblur.BlurSupport;

import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.AugmaSharedPreferences;
import world.augma.asset.AugmaVisualType;
import world.augma.asset.User;
import world.augma.ui.circle.UICircle;
import world.augma.ui.login.UILogin;
import world.augma.ui.map.UIMap;
import world.augma.ui.profile.UIProfile;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.ui.settings.UISettings;
import world.augma.work.AWS;
import world.augma.work.FirebaseInstance;
import world.augma.work.visual.AugmaImager;

/** Created by Burak Şahin */

public class UIMain extends AppCompatActivity implements ServiceUIMain {

    /* Tags for fragments */
    private static final String HOME_TAG        = "home";
    private static final String CIRCLES_TAG     = "circles";
    private static final String SETTINGS_TAG    = "settings";
    private static String INDEX_TAG             = HOME_TAG;

    /* Indexes for each menu item */
    private static final int HOME       = 0;
    private static final int CIRCLES    = 1;
    private static final int SETTINGS   = 2;

    /* Index of current menu item */
    private int navIndex = 0;

    /* Handler to post pending operations to another thread to avoid screen locking */
    private Handler handler;
    private User user;

    /* Components */
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView userName;
    private KenBurnsView bgImage;
    private ImageView profileImage;
    public FirebaseInstance firebase;

    public UIMain () {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        //Initialize the references
        Log.e("YOUR FIREBASE TOKEN", FirebaseInstanceId.getInstance().getToken());
        handler = new Handler();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        BlurSupport.addTo(drawer);
        View navHeader = navigationView.getHeaderView(0);
        userName = (TextView) navHeader.findViewById(R.id.drawer_usernameDisplay);
        bgImage = (KenBurnsView) navHeader.findViewById(R.id.drawer_background_image);
        profileImage = (ImageView) navHeader.findViewById(R.id.drawer_profilePic);

        AWS aws = new AWS();

        try {
            if(aws.execute(AWS.Service.GET_USER, AugmaSharedPreferences.getUserId(UIMain.this)).get()) {
                user = aws.fetchUser();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        loadHeader();
        setUpNavigationView();

        if(savedInstanceState == null)  {
            navIndex = 0;
            INDEX_TAG = HOME_TAG;
            loadSelectedNavigationView();
        }

        InterActivityShareModel.getInstance().setUiMain(this);
    }

    /**
     * Set up the header of Navigation Drawer
     */
    private void loadHeader() {
        //Create User's name and surname in displayable format
        //userName.setText(user.getUsername()); TODO DEGISTIR silmiyo

        userName.setText("Burcu Şahin");

        //Load background image
        //S3.fetchBackgroundImage(this, bgImage,"android.resource://world.augma/drawable/" + R.drawable.background_image); TODO DEGISTIR
        AugmaImager.set(AugmaVisualType.NOTE, this, bgImage, "android.resource://world.augma/drawable/" + R.drawable.background_image);

        //Load profile image in circular form -> with adjusted size multiplier
        //S3.fetchProfileImage(this, profileImage, user.getUserID());
        AugmaImager.set(AugmaVisualType.NOTE, this, profileImage, "android.resource://world.augma/drawable/" + R.drawable.profile_pic);

        /* TODO Test notification indicator -> LATER USE: when user has notifications put this indicator */
        //navigationView.getMenu().getItem(3).setActionView(R.layout.notification_indicator);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.side_menu_home:
                        navIndex = HOME;
                        INDEX_TAG = HOME_TAG;
                        break;
                    case R.id.side_menu_circles:
                        navIndex = CIRCLES;
                        INDEX_TAG = CIRCLES_TAG;
                        break;
                    case R.id.side_menu_settings:
                        navIndex = SETTINGS;
                        INDEX_TAG = SETTINGS_TAG;
                        break;
                    case R.id.side_menu_logout:

                        AlertView alert = new AlertView("Logout", "Are you sure you want to log out?", AlertStyle.IOS);
                        alert.addAction(new AlertAction("Logout", AlertActionStyle.NEGATIVE, new AlertActionListener() {

                            @Override
                            public void onActionClick(AlertAction alertAction) {
                                AugmaSharedPreferences.logout(UIMain.this);
                                startActivity(new Intent(UIMain.this, UILogin.class),
                                        ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
                                finish();
                            }
                        }));
                        alert.show(UIMain.this);
                        break;

                    default:
                        navIndex = 0;
                        break;
                }

                if(item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true); //Renotify listeners that this item has been checked again

                loadSelectedNavigationView();
                return true;
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileTransition = new Intent(UIMain.this, UIProfile.class);
                Pair[] p = new Pair[1];

                p[0] = new Pair<View, String>(profileImage, getString(R.string.trans_profile_pic));

                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(UIMain.this, p);
                startActivity(profileTransition, activityOptions.toBundle());
            }
        });
    }

    private void loadSelectedNavigationView() {
        selectMenuItem();

        //if user selects the same menu item again, don't load the fragment again just close
        if(getSupportFragmentManager().findFragmentByTag(INDEX_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable pending = new Runnable() {
            @Override
            public void run() {
                Fragment frg = getIndexFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.mapFrame, frg, INDEX_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if(pending != null) {
            handler.post(pending);
        }
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getIndexFragment() {

        switch (navIndex) {
            case HOME:
                return new UIMap();

            case CIRCLES:
               return new UICircle();

            case SETTINGS:
                return new UISettings();

            default:
                return new UIMap();
        }
    }

    /**
     * Make the menu item selected.
     */
    private void selectMenuItem() {
        navigationView.getMenu().getItem(navIndex).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        if(navIndex != HOME) {
            navIndex = 0;
            INDEX_TAG = HOME_TAG;
            loadSelectedNavigationView();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public User fetchUser() {
        return user;
    }

    @Override
    public ImageView getProfileView() {
        return profileImage;
    }

    @Override
    public ImageView getBackgroundView() {
        return bgImage;
    }

    @Override
    public void updateHeader() {
        loadHeader();
    }


}
