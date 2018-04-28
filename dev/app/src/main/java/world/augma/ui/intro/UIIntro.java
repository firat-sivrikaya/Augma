package world.augma.ui.intro;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import me.itangqi.waveloadingview.WaveLoadingView;
import world.augma.R;
import world.augma.ui.login.UILogin;
import world.augma.ui.main.UIMain;
import world.augma.work.AugmaSharedPreferences;

/**
 * Created by Burak on 2-Mar-18
 */

public class UIIntro extends AppCompatActivity implements Animation.AnimationListener {

    /* Main views */
    private TextView title;
    private WaveLoadingView loadingView;

    /* Animations */
    private Animation riseUp;
    private Animation fadeInTitle;
    private Animation fadeInLoadingView;
    private Animation loadingGrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_intro);

        /* Initialize views */
        title = (TextView) findViewById(R.id.introTitle);
        loadingView = (WaveLoadingView) findViewById(R.id.introWaveLoading);

        /* Initialize animations */
        riseUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_rise_up);
        fadeInTitle = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeInLoadingView = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        loadingGrow = AnimationUtils.loadAnimation(UIIntro.this, R.anim.intro_circle_grow);

        /* Listen to the fade in animations */
        fadeInTitle.setAnimationListener(this);
        fadeInLoadingView.setAnimationListener(this);
        riseUp.setAnimationListener(this);
        loadingGrow.setAnimationListener(this);

        /* Specify durations for fading effect */
        fadeInTitle.setDuration(1000);
        fadeInLoadingView.setDuration(300);

        /* Initiate the animation chain */
        title.startAnimation(fadeInTitle);
        title.startAnimation(riseUp);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if(animation == fadeInLoadingView) {

             /* Loading animation */
            final Handler handler = new Handler();
            new Thread(new Runnable() {

                private int  progress = 0;

                @Override
                public void run() {
                    progress = loadingView.getProgressValue();
                    while (progress < 100) {
                        progress += 1;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadingView.setProgressValue(progress);
                            }
                        });

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.startAnimation(loadingGrow);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation == riseUp) {
            loadingView.startAnimation(fadeInLoadingView);
        } else if(animation == loadingGrow) {

            /* Check If user has already logged in */
            SharedPreferences sp = getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS, Context.MODE_PRIVATE);
            Intent transition = new Intent(UIIntro.this, UILogin.class);

            if(sp.getString(AugmaSharedPreferences.USERNAME, null) != null) {
                transition = new Intent(UIIntro.this, UIMain.class);
            }
            startActivity(transition,
                    ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
            finish();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}
}