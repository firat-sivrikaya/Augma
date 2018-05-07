package world.augma.ui.circle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.ui.widget.CircleCanvas;
import world.augma.work.AWS;
import world.augma.work.Utils;

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

        circleSearchField.setOnEditorActionListener(new CircleSearchListener());
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.hideKeyboard(UICircle.this.getActivity());
                return false;
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        circleSearchField.setText("");
    }

    private void updateCircleList(Editable text) {
        AWS aws = new AWS();

        try {
            if(aws.execute(AWS.Service.CIRCLE_SEARCH, text.toString().toLowerCase().trim()).get()) {

                for(Circle circle: aws.getMatchedCircles()) {
                    circleList.add(circle);
                }
                canvas.init(circleList);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class CircleSearchListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(v == circleSearchField && actionId == EditorInfo.IME_ACTION_SEARCH) {
                circleSearchField.clearFocus();
                circleList.clear();
                canvas.removeAllViews();
                Utils.hideKeyboard(UICircle.this.getActivity());
                updateCircleList(circleSearchField.getText());
                return true;
            }
            return false;
        }
    }
}
