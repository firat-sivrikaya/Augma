package world.augma.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.ui.circle.UICirclePage;

public class CircleCanvas extends GridLayout {

    private Animation circleCreation;
    private Animation circleGrow;
    private Circle clickedCircle;

    public CircleCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<Circle> circleList) {

        circleCreation = AnimationUtils.loadAnimation(getContext(), R.anim.circle_creation);
        circleGrow = AnimationUtils.loadAnimation(getContext(), R.anim.circle_page_grow);

        circleGrow.setAnimationListener(new CircleAnimationListener());
        circleGrow.setFillAfter(false);

        if(circleList != null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            CircleSelectionListener listener = new CircleSelectionListener();

            for(Circle circle : circleList) {
                clickedCircle = circle;

                RelativeLayout obj = (RelativeLayout) inflater.inflate(R.layout.circle_representation, null, false);
                ((TextView) obj.findViewById(R.id.circleCenter)).setText(circle.getName());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) circle.getRadius(), (int) circle.getRadius());

                params.setMargins(10,10,10,10);
                obj.setLayoutParams(params);
                obj.setOnClickListener(listener);
                addView(obj);
                obj.startAnimation(circleCreation);
            }
        }
    }

    private class CircleSelectionListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            RelativeLayout obj = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.circle_representation, null, false);
            obj.setLayoutParams(v.getLayoutParams());
            addView(obj);
            obj.startAnimation(circleGrow);
        }
    }

    private class CircleAnimationListener implements Animation.AnimationListener {

        /**
         * <p>Notifies the start of the animation.</p>
         *
         * @param animation The started animation.
         */
        @Override
        public void onAnimationStart(Animation animation) {

            Intent intent = new Intent(getContext(), UICirclePage.class);
            intent.putExtra("name", clickedCircle.getName());
            intent.putExtra("circleID", clickedCircle.getCircleID());
            intent.putExtra("desc", clickedCircle.getDescription());

            getContext().startActivity(intent,
                    ActivityOptionsCompat.makeCustomAnimation(getContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
        }

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animation The animation which reached its end.
         */
        @Override
        public void onAnimationEnd(Animation animation) {
            CircleCanvas.this.removeAllViews();
        }

        /**
         * <p>Notifies the repetition of the animation.</p>
         *
         * @param animation The animation which was repeated.
         */
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
