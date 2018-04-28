package world.augma.ui.login;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import world.augma.R;
import world.augma.asset.User;
import world.augma.ui.main.UIMain;
import world.augma.ui.signUp.UISignUp;
import world.augma.work.AWS;
import world.augma.work.AugmaSharedPreferences;
import world.augma.work.Utils;

/**
 * Created by Burak on 5-Mar-18
 */

public class UILogin extends AppCompatActivity {

    private TextView title;
    private EditText usernameField;
    private EditText passwordField;
    private CircularProgressButton initiateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_login);

        EditorActionListener keyListener = new EditorActionListener();

        /* Put fade in animation on widgets to smooth transition */
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeIn.setDuration(300);

        /* Initialize the main page for background_image animation */
        LinearLayout loginPage = (LinearLayout) findViewById(R.id.loginPage);
        AnimationDrawable bgAnimation = (AnimationDrawable) loginPage.getBackground();

        bgAnimation.setEnterFadeDuration(5000);
        bgAnimation.setExitFadeDuration(3000);
        bgAnimation.start();

        /* Initialize widgets */
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        title = (TextView) findViewById(R.id.loginTitle);
        initiateButton = (CircularProgressButton) findViewById(R.id.initiateButton);

        /* Start animations */
        title.startAnimation(fadeIn);
        usernameField.startAnimation(fadeIn);
        passwordField.startAnimation(fadeIn);

        usernameField.setOnEditorActionListener(keyListener);
        passwordField.setOnEditorActionListener(keyListener);
    }

    public void redirectToSignUp(View v) {
        Intent transition = new Intent(UILogin.this, UISignUp.class);
        Pair[] p = new Pair[4];

        p[0] = new Pair<View, String>(title, getString(R.string.trans_logo));
        p[1] = new Pair<View, String>(usernameField, getString(R.string.trans_username_edit_text));
        p[2] = new Pair<View, String>(passwordField, getString(R.string.trans_password_edit_text));
        p[3] = new Pair<View, String>(initiateButton, getString(R.string.trans_initiate_button));

        ActivityOptions transAnimation = ActivityOptions.makeSceneTransitionAnimation(UILogin.this, p);
        startActivity(transition, transAnimation.toBundle());
    }

    public void redirectToMainPage(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initiateButton.startAnimation();
            }
        });
        Handler handler = new Handler();
        AWS aws = new AWS();
        try {
            if(aws.execute(AWS.Service.LOGIN, usernameField.getText().toString().trim(),
                    passwordField.getText().toString().trim()).get()) {
                SharedPreferences.Editor sp = getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS, Context.MODE_PRIVATE).edit();
                sp.putString(AugmaSharedPreferences.USER_ID, aws.getUserID());
                sp.apply();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initiateButton.doneLoadingAnimation(getResources().getColor(R.color.colorCLBSuccess, null),
                                Utils.convertDrawableToBitmap(getDrawable(R.drawable.ic_check_48dp)));
                    }
                }, 1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(UILogin.this, UIMain.class),
                                ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
                        finish();
                    }
                }, 2000);
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.sendErrorNotification(UILogin.this, "Incorrect credentials!");
                        initiateButton.doneLoadingAnimation(getResources().getColor(R.color.colorCLBFailure, null),
                                Utils.convertDrawableToBitmap(getDrawable(R.drawable.ic_fail_light_48dp)));
                    }
                },1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initiateButton.revertAnimation();
                    }
                }, 2500);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class EditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(v == usernameField) {
                usernameField.clearFocus();
                passwordField.requestFocus();
                if(!usernameField.getText().toString().isEmpty()
                        && !Utils.validateUsername(usernameField.getText().toString().trim())){
                    Utils.hideKeyboard(UILogin.this);
                    Utils.sendErrorNotification(UILogin.this, "Invalid username!");
                }
            } else if(v == passwordField) {
                Utils.hideKeyboard(UILogin.this);
                usernameField.clearFocus();
                passwordField.clearFocus();
                if(passwordField.getText().toString().isEmpty()) {
                    Utils.sendWarningNotification(UILogin.this, "You must enter your password.");
                }
                findViewById(android.R.id.content).requestFocus();
            }
            return true;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
}
