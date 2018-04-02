package world.augma.ui.circle;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import world.augma.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UICircle extends Fragment {

    private  BubblePicker bubblePicker;

    public UICircle() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_circle, container, false);


        final String[] strs = {"Domates", "Biber", "Patlıcan"};

       bubblePicker = (BubblePicker) root.findViewById(R.id.circles);

        bubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return strs.length;
            }

            @NotNull
            @Override
            public PickerItem getItem(int i) {
                PickerItem item = new PickerItem();
                item.setTitle(strs[i]);
                switch(i) {
                    case 0:
                        item.setColor(Color.RED);
                    break;
                    case 1:
                        item.setColor(Color.GREEN);
                        break;
                    case 2:
                        item.setColor(Color.MAGENTA);
                        break;
                    default:
                        item.setColor(Color.YELLOW);
                }

                item.setTextColor(Color.WHITE);
                item.setBackgroundImage(getResources().getDrawable(R.drawable.profile_pic, null));
                return item;
            }
        });


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
}
