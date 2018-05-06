package world.augma.ui.note;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devlomi.hidely.hidelyviews.HidelyImageView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.asset.User;
import world.augma.ui.map.UIMap;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;
import world.augma.work.visual.S3;

public class UINotePostPreviewSelectCircle extends AppCompatActivity {

    private List<Circle> circleList;
    private List<Circle> selectedCirclelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post_preview_select_circle);

        circleList = InterActivityShareModel.getInstance().getUiMain().fetchUser().getMemberships();
        selectedCirclelist = new ArrayList<>(circleList.size());

        CircleSelectionAdapter adapter = new CircleSelectionAdapter(new OnItemClick() {
            @Override
            public void onClick(View view, Circle c) {
                HidelyImageView hidelyImageView = (HidelyImageView) view;

                if(!hidelyImageView.isShowing()) {
                    hidelyImageView.show();
                    selectedCirclelist.add(c);
                } else {
                    hidelyImageView.hide();
                    selectedCirclelist.remove(c);
                }
            }
        });
    }

    private class NotePostPreviewSelecCircleProceedButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            JSONArray jsnCircleLst = new JSONArray(circleList);

            String noteText = getIntent().getExtras().getString("noteText");
            ServiceUIMain serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
            User user =  serviceUIMain.fetchUser();
            Location location = UIMap.mLastKnownLocation;
            String noteID = "";

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InterActivityShareModel.getInstance().getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] image = bos.toByteArray();

            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AWS aws1 = new AWS();
            try {
                if(aws1.execute(AWS.Service.POST_NOTE,noteText, ""+location.getLatitude(), ""+location.getLongitude(),
                        user.getUserID(), jsnCircleLst.toString()).get()){
                    noteID = aws1.getNewNoteID();
                    S3.uploadNoteImage(image,user.getUserID(),noteID);
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.e("AWS Error", "ERROR: Cannot post note");
            }
        }
    }

    private class CircleSelectionAdapter extends RecyclerView.Adapter<UINotePostPreviewSelectCircle.CircleSelectionItem> {

        OnItemClick onItemClick;

        public CircleSelectionAdapter(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        @NonNull
        @Override
        public CircleSelectionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CircleSelectionItem(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.circle_selection_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final CircleSelectionItem holder, int position) {
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onClick(holder.checkMarkView, holder.c);
                }
            });
            holder.c = circleList.get(position);
            holder.noteName.setText(circleList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return circleList.size();
        }
    }

    private class CircleSelectionItem extends RecyclerView.ViewHolder {

        private LinearLayout rootLayout;
        private HidelyImageView checkMarkView;
        private TextView noteName;
        private Circle c;

        public CircleSelectionItem(View itemView) {
            super(itemView);

            this.c = c;
            rootLayout = itemView.findViewById(R.id.notePostPreviewSelectCircleItemRoot);
            checkMarkView = itemView.findViewById(R.id.notePostPreviewSelectCircleItemCheckMark);
            noteName = itemView.findViewById(R.id.notePostPreviewSelectCircleItemName);
        }

        public void setCircle(Circle c) {
            this.c = c;
        }
    }

    interface OnItemClick {

        void onClick(View view, Circle c);
    }
}
