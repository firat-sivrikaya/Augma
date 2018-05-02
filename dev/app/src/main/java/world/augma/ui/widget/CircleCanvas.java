package world.augma.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import world.augma.R;
import world.augma.asset.Circle;

public class CircleCanvas extends GridLayout {

    private Animation circleCreation;
    private Animation circleGrow;

    public CircleCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(List<Circle> circleList) {
        circleCreation = AnimationUtils.loadAnimation(getContext(), R.anim.circle_creation);
        circleGrow = AnimationUtils.loadAnimation(getContext(), R.anim.circle_page_grow);

        if(circleList != null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            CircleSelectionListener listener = new CircleSelectionListener();

            for(Circle circle : circleList) {
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
            ((ViewGroup) getParent()).addView(obj);
            obj.startAnimation(circleGrow);
        }
    }
}
