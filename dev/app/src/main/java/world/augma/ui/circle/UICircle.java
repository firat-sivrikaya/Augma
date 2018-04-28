package world.augma.ui.circle;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.ui.widget.CircleCanvas;
import world.augma.work.AWS;

public class UICircle extends Fragment {

    private EditText circleSearchField;
    private List<Circle> circleList;
    private CircleCanvas canvas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_circle, container, false);
        canvas = (CircleCanvas) root.findViewById(R.id.circleFrame);
        circleSearchField = (EditText) root.findViewById(R.id.circleSearchField);
        circleList = new ArrayList<>();
        circleSearchField.addTextChangedListener(new CircleSearchTextChangeListener());

        return root;
    }

    private void updateCircleList(Editable text) {
        AWS aws = new AWS();

        try {
            if(aws.execute(AWS.Service.CIRCLE_SEARCH, text.toString().trim()).get()) {

                for(String match : Arrays.asList(aws.getMatchingCircleNames())) {
                    circleList.add(new Circle(match, null, null, null,
                            -1, -1, 200));
                }
                arrangeCircles();
                canvas.init(circleList);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementation of 2D Circle Packing Algorithm for non-overlapping placement of circles on the canvas
     */
    private void arrangeCircles() {
        circleList.get(0).setX(getResources().getDisplayMetrics().widthPixels / 2);
        circleList.get(0).setY(getResources().getDisplayMetrics().heightPixels / 2);
        circleList.get(0).setPlaced(true);

        if(circleList.size() > 1) {
            for(int i = 0; i < circleList.size(); i++) {
                circleList.get(i).computePositionOnScreen(circleList);
            }
        }
    }

    private class CircleSearchTextChangeListener implements TextWatcher {

        private boolean isTriggered = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(final Editable s) {
            if(isTriggered || s.toString().isEmpty()){
                return;
            } else {
                isTriggered = true;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(circleSearchField.hasFocus()) {
                        isTriggered = false;
                        circleList.clear();
                        circleSearchField.clearFocus();
                        canvas.removeAllViews();
                        updateCircleList(s);
                    }
                }
            }, 800);
        }
    }
}
