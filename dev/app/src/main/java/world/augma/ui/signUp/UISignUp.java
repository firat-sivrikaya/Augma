package world.augma.ui.signUp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import world.augma.R;
import world.augma.ui.main.UIMain;
import world.augma.work.AWS;
import world.augma.work.AugmaSharedPreferences;
import world.augma.work.Utils;

/**
 * Created by Burak on 5-Mar-18
 */

public class UISignUp extends AppCompatActivity {

    private CircularProgressButton initiateButton;
    private EditText usernameField;
    private EditText passwordField;
    private EditText repeatPasswordField;
    private EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_sign_up);

        EditorActionListener listener = new EditorActionListener();

        /* Put fade in animation on widgets to smooth transition */
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeIn.setDuration(300);

        /* Initialize widgets */
        initiateButton = (CircularProgressButton) findViewById(R.id.initiateButton);
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        repeatPasswordField = (EditText) findViewById(R.id.repeatPasswordField);
        emailField = (EditText) findViewById(R.id.emailField);

        /* Start animations */
        repeatPasswordField.startAnimation(fadeIn);
        emailField.startAnimation(fadeIn);

        usernameField.setOnEditorActionListener(listener);
        passwordField.setOnEditorActionListener(listener);
        repeatPasswordField.setOnEditorActionListener(listener);
        emailField.setOnEditorActionListener(listener);
		
		findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.hideKeyboard(UISignUp.this);
                return false;
            }
        });
    }

    public void initiateRegister(View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initiateButton.startAnimation();
            }
        });
        Handler handler = new Handler();

        try {
            if(new AWS().execute(AWS.Service.REGISTER, usernameField.getText().toString().trim(),
                    passwordField.getText().toString().trim(), emailField.getText().toString().trim()).get()) {
                SharedPreferences.Editor sp = getSharedPreferences(AugmaSharedPreferences.SHARED_PREFS, Context.MODE_PRIVATE).edit();
                sp.putString(AugmaSharedPreferences.USERNAME, usernameField.getText().toString().trim());
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
                        Intent intent = new Intent(UISignUp.this, UIMain.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent,
                                ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
                        finish();
                    }
                }, 2000);
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.sendErrorNotification(UISignUp.this, "Username and/or email already taken.");
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

                if(usernameField.getText().toString().isEmpty()) {
                    Utils.sendWarningNotification(UISignUp.this, "You must enter a username.");
                } else if(!Utils.validateUsername(usernameField.getText().toString().trim())) {
                    Utils.hideKeyboard(UISignUp.this);
                    Utils.sendErrorNotification(UISignUp.this, "Invalid username!");
                }
            } else if(v == passwordField) {
                passwordField.clearFocus();
                repeatPasswordField.requestFocus();

                if(passwordField.getText().toString().isEmpty()) {
                    Utils.hideKeyboard(UISignUp.this);
                    Utils.sendWarningNotification(UISignUp.this, "You must enter a password.");
                }
            } else if(v == repeatPasswordField) {
                repeatPasswordField.clearFocus();
                emailField.requestFocus();

                if(passwordField.getText().toString().isEmpty()) {
                    Utils.hideKeyboard(UISignUp.this);
                    Utils.sendWarningNotification(UISignUp.this, "You must enter a password.");
                } else if(!passwordField.getText().toString().equals(repeatPasswordField.getText().toString())) {
                    Utils.hideKeyboard(UISignUp.this);
                    Utils.sendErrorNotification(UISignUp.this, "Both entered passwords must match!");
                }
            } else if(v == emailField) {
                Utils.hideKeyboard(UISignUp.this);
                usernameField.clearFocus();
                passwordField.clearFocus();
                repeatPasswordField.clearFocus();
                emailField.clearFocus();
                findViewById(android.R.id.content).requestFocus();

                if(emailField.getText().toString().trim().isEmpty()) {
                    Utils.sendWarningNotification(UISignUp.this, "You must enter a valid email adress.");
                } else if(!Utils.validateEmail(emailField.getText().toString().trim())) {
                    Utils.sendErrorNotification(UISignUp.this, "Invalid email adress!");
                }
            }
            return true;
        }
    }
}
