package world.augma.ui.circle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import world.augma.R;
import world.augma.work.Utils;
import world.augma.work.visual.OnSwipeTouchListener;

public class UICirclePage extends AppCompatActivity {

    //userID, username, circleID, circleNAME
    private String circleID;
    private String circleName;
    private String circleDescription;
    private TextView circlePageCircleName;
    private TextView circleDescriptionField;
    private LottieAnimationView background;
    private LottieAnimationView swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_circle_page);

        circleID = getIntent().getExtras().getString("circleID");
        circleName = getIntent().getExtras().getString("name");
        circleDescription = getIntent().getExtras().getString("desc");

        swipeView = findViewById(R.id.circlePageSwipeView);
        background = findViewById(R.id.circlePageBackground);
        circlePageCircleName = (TextView) findViewById(R.id.circlePageCircleName);
        circleDescriptionField = (TextView) findViewById(R.id.circleDescription);

        circleDescriptionField.setText(circleDescription);
        circlePageCircleName.setText(circleName);

        background.setAnimation(R.raw.gradient_animated_background);
        background.setRepeatCount(LottieDrawable.INFINITE);
        background.playAnimation();

        swipeView.setAnimation(R.raw.swipe_left_to_right);
        swipeView.setRepeatCount(LottieDrawable.INFINITE);
        swipeView.setScale(0.7f);
        swipeView.playAnimation();

        swipeView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Utils.sendSuccessNotification(UICirclePage.this, "YAAAY!");
            }
        });

        //TODO aws

    }
}
