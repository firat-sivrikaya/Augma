package world.augma.ui.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devlomi.hidely.hidelyviews.HidelyImageButton;
import com.devlomi.hidely.hidelyviews.HidelyImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.Circle;
import world.augma.asset.User;
import world.augma.ui.main.UIMain;
import world.augma.ui.map.UIMap;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;
import world.augma.work.visual.S3;

public class UINotePostPreviewSelectCircle extends AppCompatActivity {

    private List<Circle> circleList;
    private List<Circle> selectedCirclelist;
    private HidelyImageButton proceedButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post_preview_select_circle);

        recyclerView = findViewById(R.id.notePostPreviewSelectCircleList);
        proceedButton = findViewById(R.id.notePostPreviewSelectCircleConfirmButton);
        circleList = InterActivityShareModel.getInstance().getUiMain().fetchUser().getMemberships();

        selectedCirclelist = new ArrayList<>();
        proceedButton.setOnClickListener(new NotePostPreviewSelecCircleProceedButtonListener());

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

                if(!selectedCirclelist.isEmpty()) {
                    proceedButton.show();
                } else {
                    proceedButton.hide();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private class NotePostPreviewSelecCircleProceedButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            JSONArray jsnCircleLst = new JSONArray();
            Log.e("selected circle list:",selectedCirclelist.toString());
            Log.e("Json selected circle list:",jsnCircleLst.toString());

            for (int i = 0; i< selectedCirclelist.size();i++){
                JSONObject iObj = new JSONObject();
                try {
                    iObj.put("circleID",selectedCirclelist.get(i).getCircleID());
                    iObj.put("circleName",selectedCirclelist.get(i).getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsnCircleLst.put(iObj);
            }
            Log.e("Json selected circle list 2:",jsnCircleLst.toString());

            String noteText = getIntent().getExtras().getString("noteText");
            ServiceUIMain serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
            User user =  serviceUIMain.fetchUser();
            JSONObject jsonUser = new JSONObject();

            try {
                jsonUser.put("userID",user.getUserID());
                jsonUser.put("username",user.getUsername());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Location location = UIMap.mLastKnownLocation;
            String noteID = "";

            ByteArrayOutputStream bos = new ByteArrayOutputStream();



            AWS aws1 = new AWS();
            try {
                if(aws1.execute(AWS.Service.POST_NOTE,noteText, ""+location.getLatitude(), ""+location.getLongitude(),
                        jsonUser.toString(), jsnCircleLst.toString()).get()){
                    //slow it down
                    InterActivityShareModel.getInstance().getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] image = bos.toByteArray();
                    bos.flush();
                    bos.close();

                    noteID = aws1.getNewNoteID();
                    //TODO we need to wait MOST IMPORTANT SHIT RIGHT HERE
                    S3.uploadNoteImage(image,user.getUserID(),noteID);
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.e("AWS Error", "ERROR: Cannot post note");
            } catch (IOException e) {
                e.printStackTrace();
            }
            proceedBackToMainPage(v);
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

    public void proceedBackToMainPage(View v) {
        startActivity(new Intent(this, UIMain.class),
                ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out).toBundle());
        finish();
    }

    interface OnItemClick {

        void onClick(View view, Circle c);
    }
}
