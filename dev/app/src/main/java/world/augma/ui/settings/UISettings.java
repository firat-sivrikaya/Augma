package world.augma.ui.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irozon.sneaker.Sneaker;

import java.util.Random;

import world.augma.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UISettings extends Fragment {

    public UISettings() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ui_settings, container, false);


        //Custom notification atımı

        /*
        Sneaker.with(this)
                .setTitle("Title", R.color.white) // Title and title color
                .setMessage("This is the message.", R.color.white) // Message and message color
                .setDuration(4000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT) // Height of the Sneaker layout
                .setIcon(R.drawable.ic_no_connection, R.color.white, false) // Icon, icon tint color and circular icon view
                .setTypeface(Typeface.createFromAsset(this.getAssets(), "font/" + fontName)); // Custom font for title and message
       .setOnSneakerClickListener(this) // Click listener for Sneaker
                .setOnSneakerDismissListener(this) // Dismiss listener for Sneaker. - Version 1.0.2
                .setCornerRadius(radius, margin) // Radius and margin for round corner Sneaker. - Version 1.0.2
                .sneak(R.color.colorAccent); // Sneak with background color

                */

        //Notificationlar

        switch (new Random().nextInt(3)) {
            case 0:
                Sneaker.with(getActivity())
                        .setTitle("Error!")
                        .setMessage("Error Mesajı")
                        .sneakError();
                break;
            case 1:
                Sneaker.with(getActivity())
                        .setTitle("Success!")
                        .setMessage("Success Mesajı")
                        .sneakSuccess();
                break;
            case 2:
                Sneaker.with(getActivity())
                        .setTitle("Warning!")
                        .setMessage("Warning Mesajı")
                        .sneakWarning();
                break;
        }
        return root;
    }
}
