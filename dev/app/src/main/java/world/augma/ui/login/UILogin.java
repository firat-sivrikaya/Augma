package world.augma.ui.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import world.augma.R;
import world.augma.ui.main.UIMain;
import world.augma.ui.signUp.UISignUp;
import world.augma.utils.Utils;

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

        AsyncTask<Void, Void, Void> loadingDemo = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                initiateButton.doneLoadingAnimation(getResources().getColor(R.color.colorCLBSuccess, null),
                        Utils.convertDrawableToBitmap(getDrawable(R.drawable.ic_check_48dp)));
            }
        };

        initiateButton.startAnimation();
        loadingDemo.execute();

        //Remove comments to proceed to main page!!!
        /*Intent transition = new Intent(UILogin.this, UIMain.class);
        finish();
        startActivity(transition);*/
    }
}
