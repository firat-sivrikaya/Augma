package world.augma.ui.note;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import world.augma.R;
import world.augma.asset.AugmaVisualType;
import world.augma.work.visual.AugmaImager;

public class UINoteDisplay extends AppCompatActivity {

    private SlidingUpPanelLayout root;
    private TextView userNameText;
    private TextView noteText;
    private ImageView profilePic;
    private ImageView noteImage;
    private RelativeLayout topBar;
    private RelativeLayout bottomPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_display);

        root                = (SlidingUpPanelLayout) findViewById(R.id.noteDisplayRoot);
        topBar              = (RelativeLayout) findViewById(R.id.noteDisplayTopBar);
        bottomPanel         = (RelativeLayout) findViewById(R.id.noteDisplayBottomSlider);
        noteImage           = (ImageView) findViewById(R.id.noteDisplayImage);
        userNameText        = (TextView) topBar.findViewById(R.id.noteDisplayUserNameText);
        noteText            = (TextView) bottomPanel.findViewById(R.id.noteDisplayNoteText);
        profilePic          = (ImageView) ((RelativeLayout) topBar.findViewById(R.id.noteDisplayProfilePicLayout))
                .findViewById(R.id.noteDisplayProfilePic);

        //TODO Sonra sil
        noteText.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam dignissim, " +
                "nulla commodo venenatis malesuada, metus metus ult" +
                "ricies velit, eu sodales justo urna at massa. Donec" +
                " ac lorem dolor. Mauris leo augue, faucibus ac neque a, egestas " +
                "fermentum turpis. Phasellus lacinia varius lacus, a tempus felis sagittis eu.");
        userNameText.setText("Burcu Şahin");
        AugmaImager.set(AugmaVisualType.NOTE, this, profilePic, "android.resource://world.augma/drawable/" + R.drawable.profile_pic);
        AugmaImager.set(AugmaVisualType.NOTE, this, noteImage, "android.resource://world.augma/drawable/" + R.drawable.sample);
    }

}



