package world.augma.ui.circle;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.asset.User;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.ui.widget.CircleCanvas;
import world.augma.work.AWS;
import world.augma.work.Utils;

public class UICircle extends Fragment {

    private EditText circleSearchField;
    private List<Circle> circleList;
    private CircleCanvas canvas;
    private LottieAnimationView addCircleButton;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_circle, container, false);
        canvas = (CircleCanvas) root.findViewById(R.id.circleFrame);
        circleSearchField = (EditText) root.findViewById(R.id.circleSearchField);
        addCircleButton = root.findViewById(R.id.circleAddCircleButton);
        circleList = new ArrayList<>();
        ServiceUIMain serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
        user =  serviceUIMain.fetchUser();

        circleSearchField.setOnEditorActionListener(new CircleSearchListener());
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.hideKeyboard(UICircle.this.getActivity());
                return false;
            }
        });

        addCircleButton.setAnimation(R.raw.add_circle);
        addCircleButton.setScale(0.3f);
        addCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO add circle here
                View layout = LayoutInflater.from(UICircle.this.getContext()).inflate(R.layout.circle_creation_info, null, false);
                final EditText circleName = layout.findViewById(R.id.circleCreationCircleNameField);
                final EditText circleDesc = layout.findViewById(R.id.circleCreationCircleDescField);

                new AlertDialog.Builder(UICircle.this.getContext())
                        .setView(layout)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO AWS burada eklesin
                                JSONObject jsonUser = new JSONObject();

                                try {
                                    jsonUser.put("userID",user.getUserID());
                                    jsonUser.put("username",user.getUsername());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                AWS aws = new AWS();

                                try {
                                    if(aws.execute(AWS.Service.CREATE_CIRCLE,jsonUser.toString(),circleName.getText().toString(),circleName.getText().toString().toLowerCase(),circleDesc.getText().toString()).get())
                                        Utils.sendSuccessNotification(UICircle.this.getActivity(), "CIRCLE: " + circleName.getText().toString());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                addCircleButton.playAnimation();
                            }
                        })
                        .setNeutralButton("Cancel", null).show();
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
