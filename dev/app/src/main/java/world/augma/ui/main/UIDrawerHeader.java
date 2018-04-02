package world.augma.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import co.gofynd.gravityview.GravityView;
import world.augma.R;

public class UIDrawerHeader extends AppCompatActivity {

    private GravityView gravityView;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_drawer_header);

        //TODO databaseden alınacak isim bilgisi!! tarih - zaman - hava durumu gibi widgetlar koyulabilir!
        backgroundImage = (ImageView) findViewById(R.id.drawer_background_image);
        gravityView = GravityView.getInstance(this);

        //TODO support etmeyen devicelar için normal background image koyulsun
        if(gravityView.deviceSupported()) {
            gravityView.setImage(backgroundImage, R.drawable.background_image).center();
        }
    }

}
