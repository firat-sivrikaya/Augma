package world.augma.ui.signUp;

import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import world.augma.R;
import world.augma.utils.Utils;

/**
 * Created by Burak on 5-Mar-18
 */

public class UISignUp extends AppCompatActivity {

    private CircularProgressButton initiateButton;
    private EditText repeatPasswordEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_sign_up);

        /* Put fade in animation on widgets to smooth transition */
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeIn.setDuration(300);

        /* Initialize widgets */
        initiateButton = (CircularProgressButton) findViewById(R.id.initiateButton);
        repeatPasswordEditText = (EditText) findViewById(R.id.repeatPasswordField);
        emailEditText = (EditText) findViewById(R.id.emailField);

        /* Start animations */
        repeatPasswordEditText.startAnimation(fadeIn);
        emailEditText.startAnimation(fadeIn);
    }

    public void initiateRegister(View view) {
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

                initiateButton.doneLoadingAnimation(getResources().getColor(R.color.colorCLBFailure, null),
                        Utils.convertDrawableToBitmap(getDrawable(R.drawable.ic_fail_light_48dp)));
            }
        };

        initiateButton.startAnimation();
        loadingDemo.execute();
    }
}
