package world.augma.ui.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;

import world.augma.R;

public class UINotePostPreview extends AppCompatActivity {

    private RelativeLayout proceedButton;
    private SweetSheet sheet;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post_preview);

        root = findViewById(R.id.notePostPreviewRoot);
        proceedButton = findViewById(R.id.notePostPreviewProceedButton);
        sheet = new SweetSheet(root);

        root.setBackground(new BitmapDrawable(getResources(), (Bitmap) getIntent().getExtras().get("previewPic")));
        proceedButton.setOnClickListener(new PostPreviewButtonListener());
        CustomDelegate delegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        delegate.setCustomView(LayoutInflater.from(this)
                .inflate(R.layout.note_post_preview_text_edit, null, false));
        sheet.setDelegate(delegate);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        sheet.toggle();
        return true;
    }

    private class PostPreviewButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(UINotePostPreview.this, UINotePostPreviewSelectCircle.class);
            intent.putExtra("previewPic", (Parcelable) getIntent().getExtras().get("previewPic"));
            intent.putExtra("noteText", ((EditText) findViewById(R.id.notePostPreviewEditText)).getText().toString());
            startActivity(intent,
                    ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());

        }
    }
}
