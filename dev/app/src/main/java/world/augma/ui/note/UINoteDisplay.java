package world.augma.ui.note;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;

import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Note;
import world.augma.asset.User;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;
import world.augma.work.visual.S3;

public class UINoteDisplay extends AppCompatActivity {

    private RelativeLayout root;
    private TextView userNameText;
    private LottieAnimationView upvoteButton;
    private LottieAnimationView lightTheBeaconButton;
    private TextView noteText;
    private ImageView profilePic;
    private ImageView noteImage;
    private SweetSheet sheet;
    private RelativeLayout topBar;
    private Note note;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_display);

        RelativeLayout noteLayout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.note_display_slider, null, false);
        CustomDelegate delegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        delegate.setCustomView(noteLayout);

        NoteDisplayClickListener listener = new NoteDisplayClickListener();


        note = (Note) getIntent().getExtras().getSerializable("obj");

        root                = (RelativeLayout) findViewById(R.id.noteDisplayRoot);
        topBar              = (RelativeLayout) findViewById(R.id.noteDisplayTopBar);
        noteImage           = (ImageView) findViewById(R.id.noteDisplayImage);
        noteText            = (TextView) noteLayout.findViewById(R.id.noteDisplayNoteText);
        upvoteButton        = noteLayout.findViewById(R.id.noteDisplayUpvoteButton);
        lightTheBeaconButton= noteLayout.findViewById(R.id.noteDisplayLightTheBeaconButton);
        userNameText        = (TextView) topBar.findViewById(R.id.noteDisplayUserNameText);
        profilePic          = (ImageView) ((RelativeLayout) topBar.findViewById(R.id.noteDisplayProfilePicLayout))
                .findViewById(R.id.noteDisplayProfilePic);

        sheet = new SweetSheet(root);
        sheet.setDelegate(delegate);

        upvoteButton.setAnimation(R.raw.thumbs_up);
        upvoteButton.setRepeatCount(0);
        upvoteButton.setScale(0.12f);
        upvoteButton.setOnClickListener(listener);
        upvoteButton.setMinAndMaxProgress(0, 0.8f);
        lightTheBeaconButton.setAnimation(R.raw.pulsing_beacon);
        lightTheBeaconButton.setOnClickListener(listener);
        lightTheBeaconButton.setRepeatCount(LottieDrawable.INFINITE);
        lightTheBeaconButton.setScale(0.3f);

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

        userNameText.setText(user.getName());
        noteText.setText(note.getNoteText());
        S3.fetchProfileImage(this, profilePic, user.getUserID());
        S3.fetchNoteImage(this,noteImage,user.getUserID(),note.getNoteID());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        sheet.toggle();
        return true;
    }

    private class NoteDisplayClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(v == upvoteButton && upvoteButton.getProgress() == 0) {
                upvoteButton.playAnimation();
            } else if(v == lightTheBeaconButton) {
                if(!lightTheBeaconButton.isAnimating()) {
                    lightTheBeaconButton.playAnimation();
                } else {
                    lightTheBeaconButton.cancelAnimation();
                }
            }
        }
    }

}



