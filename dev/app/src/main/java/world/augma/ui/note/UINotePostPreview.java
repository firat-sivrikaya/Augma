package world.augma.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;

import world.augma.R;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.work.Utils;

public class UINotePostPreview extends AppCompatActivity {

    private RelativeLayout proceedButton;
    private SweetSheet sheet;
    private RelativeLayout root;
    //private byte[] bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post_preview);

        RelativeLayout editTextLayout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.note_post_preview_text_edit, null, false);

        root = findViewById(R.id.notePostPreviewRoot);
        proceedButton = editTextLayout.findViewById(R.id.notePostPreviewProceedButton);
        sheet = new SweetSheet(root);
        proceedButton.setOnClickListener(new PostPreviewButtonListener());
        CustomDelegate delegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        delegate.setCustomView(editTextLayout);
        sheet.setDelegate(delegate);
        setupBackground();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        sheet.toggle();
        Utils.hideKeyboard(this);
        return true;
    }

    public void setupBackground() {
       /* Bitmap bitmap = BitmapFactory.decodeByteArray(bg, 0, bg.length);
        Matrix mat = new Matrix();
        mat.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        */
        ImageView background = findViewById(R.id.notePostPreviewBackground);
        background.setImageBitmap(InterActivityShareModel.getInstance().getBitmap());
    }

    private class PostPreviewButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UINotePostPreview.this, UINotePostPreviewSelectCircle.class);
            intent.putExtra("noteText", ((EditText) findViewById(R.id.notePostPreviewEditText)).getText().toString());
            startActivity(intent,
                    ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());

        }
    }
}
