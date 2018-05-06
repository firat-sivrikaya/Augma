package world.augma.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.AugmaSharedPreferences;
import world.augma.asset.AugmaVisualType;
import world.augma.asset.Note;
import world.augma.asset.User;
import world.augma.ui.main.UIMain;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;
import world.augma.work.visual.AugmaImager;
import world.augma.work.visual.S3;

public class UINoteDisplay extends AppCompatActivity {

    private SlidingUpPanelLayout root;
    private TextView userNameText;
    private TextView noteText;
    private ImageView profilePic;
    private ImageView noteImage;
    private RelativeLayout topBar;
    private RelativeLayout bottomPanel;
    private Note note;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_display);

        note = (Note) getIntent().getExtras().getSerializable("obj");

        root                = (SlidingUpPanelLayout) findViewById(R.id.noteDisplayRoot);
        topBar              = (RelativeLayout) findViewById(R.id.noteDisplayTopBar);
        bottomPanel         = (RelativeLayout) findViewById(R.id.noteDisplayBottomSlider);
        noteImage           = (ImageView) findViewById(R.id.noteDisplayImage);
        userNameText        = (TextView) topBar.findViewById(R.id.noteDisplayUserNameText);
        noteText            = (TextView) bottomPanel.findViewById(R.id.noteDisplayNoteText);
        profilePic          = (ImageView) ((RelativeLayout) topBar.findViewById(R.id.noteDisplayProfilePicLayout))
                .findViewById(R.id.noteDisplayProfilePic);

        //TODO Sonra sil
        AWS aws = new AWS();

        if(null == note.getOwner() ){
            ServiceUIMain uiMain =(ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
            user = uiMain.fetchUser();

        }
        else{
            try {
                if(aws.execute(AWS.Service.GET_USER, note.getOwner().getUserID()).get()) {
                    user = aws.fetchUser();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }



        noteText.setText(note.getNoteText());
        Log.e("@@@@@@@@@@@@@@@@@@@@@@@@", note.getNoteText());
        userNameText.setText(user.getName());
        S3.fetchProfileImage(this, profilePic, user.getUserID());
        S3.fetchNoteImage(this,noteImage,user.getUserID(),note.getNoteID());
    }


}



