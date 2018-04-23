package world.augma.ui.circle;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.igalata.bubblepicker.BubblePickerListener;
import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import world.augma.R;
import world.augma.asset.Circle;

public class UICircle extends Fragment {

    private  BubblePicker bubblePicker;
    private  List<Circle> circleList;

    public UICircle() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_circle, container, false);

        fetchUserCircleList();

        bubblePicker = (BubblePicker) root.findViewById(R.id.circles);
        bubblePicker.setCenterImmediately(true);
        bubblePicker.setAdapter(new CircleAdapter());
        bubblePicker.setListener(new CircleSelectionListener());
        bubblePicker.setMaxSelectedCount(1);
        bubblePicker.setBubbleSize(1);
        bubblePicker.setZOrderOnTop(false);


        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        bubblePicker.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        bubblePicker.onResume();
    }

    private void fetchUserCircleList() {

        /* TODO Buradan veri cekilecek */

        circleList = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            circleList.add(new Circle( generateRandomString(20), generateRandomString(100), null, null));
        }
    }

    //TODO sonra bunu sil
    private String generateRandomString(int bound) {
        String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(rnd.nextInt(bound));
        for(int i = 0; i < sb.length(); i++) {
            sb.append(str.charAt(rnd.nextInt(str.length())));
        }
        return sb.toString();
    }

    private class CircleAdapter implements BubblePickerAdapter {

        @Override
        public int getTotalCount() {
            return circleList.size();
        }

        @NotNull
        @Override
        public PickerItem getItem(int i) {
            PickerItem item = new PickerItem();
            Circle currentCircle = circleList.get(i);
            int color = generateRandomColor();
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.5f;
            int darker = Color.HSVToColor(hsv);

            item.setGradient(new BubbleGradient(color,
                    darker, BubbleGradient.VERTICAL));
            item.setTextColor(Color.WHITE);
            return item;
        }

        private int generateRandomColor() {
            Random random = new Random();

            return Color.argb(1.0f, random.nextFloat(), random.nextFloat(), random.nextFloat());
        }
    }

    private class CircleSelectionListener implements BubblePickerListener {

        @Override
        public void onBubbleDeselected(PickerItem pickerItem) {

        }

        @Override
        public void onBubbleSelected(PickerItem pickerItem) {

        }
    }
}
