package world.augma.ui.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import world.augma.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UISettings extends Fragment {

    public UISettings() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_settings, container, false);
    }
}
