package world.augma.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import world.augma.R;
import world.augma.asset.Circle;

public class CircleCanvas extends RelativeLayout {

    private Animation circleCreation;
    private Animation circleDisappear;

    public CircleCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<Circle> circleList) {
        circleCreation = AnimationUtils.loadAnimation(getContext(), R.anim.circle_creation);

        if(circleList != null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            CircleSelectionListener listener = new CircleSelectionListener();
            Handler handler = new Handler();
            int delay = 0;

            for(Circle circle : circleList) {
                RelativeLayout obj = (RelativeLayout) inflater.inflate(R.layout.circle_representation, null, false);

                ((TextView) obj.findViewById(R.id.circleCenter)).setText(circle.getName());
                obj.setLayoutParams(new RelativeLayout.LayoutParams(circle.getRadius(), circle.getRadius()));
                obj.setX(circle.getX());
                obj.setY(circle.getY());
                obj.setOnClickListener(listener);

                final RelativeLayout finalLayout = obj;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addView(finalLayout);
                        finalLayout.startAnimation(circleCreation);
                    }
                }, delay);
                delay += 50;
            }
        }
    }

    private class CircleSelectionListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO Open up circle page
        }
    }
}
