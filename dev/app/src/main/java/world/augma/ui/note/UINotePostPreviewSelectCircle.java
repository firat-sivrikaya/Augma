package world.augma.ui.note;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yalantis.multiselection.lib.MultiSelect;
import com.yalantis.multiselection.lib.MultiSelectBuilder;
import com.yalantis.multiselection.lib.adapter.BaseLeftAdapter;
import com.yalantis.multiselection.lib.adapter.BaseRightAdapter;

import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.ExecutionException;

import world.augma.R;
import world.augma.asset.AugmaCallback;
import world.augma.asset.Circle;
import world.augma.asset.User;
import world.augma.ui.map.UIMap;
import world.augma.ui.services.InterActivityShareModel;
import world.augma.ui.services.ServiceUIMain;
import world.augma.work.AWS;
import world.augma.work.visual.S3;

public class UINotePostPreviewSelectCircle extends AppCompatActivity {

    private MultiSelect<Circle> multiSelect;
    private List<Circle> selectedCircleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_note_post_preview_select_circle);

        List<Circle> circleList = InterActivityShareModel.getInstance().getUiMain().fetchUser().getMemberships();

        MultiSelectBuilder<Circle> builder = new MultiSelectBuilder<>(Circle.class)
                .withContext(this)
                .mountOn((ViewGroup) findViewById(R.id.notePostPreviewSelectCircleRoot))
                .withSidebarWidth(60);

        CircleSelectionLeftAdapter leftAdapter = new CircleSelectionLeftAdapter(new AugmaCallback() {
            @Override
            public void onTrigger(int p) {
                multiSelect.select(p);
            }
        });

        CircleSelectionRightAdapter rightAdapter = new CircleSelectionRightAdapter(new AugmaCallback() {
            @Override
            public void onTrigger(int p) {
                multiSelect.select(p);
            }
        });

        leftAdapter.addAll(circleList);
        builder.withLeftAdapter(leftAdapter).withRightAdapter(rightAdapter);
        findViewById(R.id.notePostPreviewSelecCircleProceedButton).setOnClickListener(new NotePostPreviewSelecCircleProceedButtonListener());
        multiSelect.showSelectedPage();
        multiSelect.showNotSelectedPage();
    }

    private class NotePostPreviewSelecCircleProceedButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            List<Circle>  circleLst = multiSelect.getSelectedItems();
            JSONArray jsnCircleLst = new JSONArray(circleLst);




            byte[] image = getIntent().getExtras().getParcelable("previewPic");
            String noteText = getIntent().getExtras().getString("noteText");
            ServiceUIMain serviceUIMain = (ServiceUIMain) InterActivityShareModel.getInstance().getUiMain();
            User user =  serviceUIMain.fetchUser();
            Location location = UIMap.mLastKnownLocation;
            String noteID = "";


            AWS aws1 = new AWS();
            try {
                if(aws1.execute(AWS.Service.POST_NOTE,noteText, ""+location.getLatitude(), ""+location.getLongitude(),user.getUserID(),jsnCircleLst.toString()).get()){
                    noteID = aws1.getNewNoteID();
                    S3.uploadNoteImage(image,user.getUserID(),noteID);
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.e("AWS Error", "ERROR: Cannot post note");
            }



        }
    }

}

    class CircleSelectionItem extends RecyclerView.ViewHolder {

        private TextView circleName;
        private TextView circleSize;

        public CircleSelectionItem(View itemView) {
            super(itemView);
            circleName = itemView.findViewById(R.id.notePostPreviewSelectCircleItemName);
            circleSize = itemView.findViewById(R.id.notePostPreviewSelectCircleSize);
        }

        public void bind(Circle item) {
            circleName.setText(item.getName());
            circleSize.setText(String.valueOf(item.getMemberList().size()));
        }
    }

    class CircleSelectionLeftAdapter extends BaseLeftAdapter<Circle, RecyclerView.ViewHolder> {

        private final AugmaCallback callback;

        public CircleSelectionLeftAdapter(AugmaCallback callback) {
            super(Circle.class);
            this.callback = callback;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CircleSelectionItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_selection_item,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            CircleSelectionItem item = (CircleSelectionItem) holder;

            item.bind(getItemAt(position));
            item.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    v.setPressed(true);
                    v.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            v.setPressed(false);
                            callback.onTrigger(holder.getAdapterPosition());
                        }
                    }, 200);
                }
            });

        }
    }

    class CircleSelectionRightAdapter extends BaseRightAdapter<Circle, RecyclerView.ViewHolder> {

        private final AugmaCallback callback;

        public CircleSelectionRightAdapter(AugmaCallback callback) {
            this.callback = callback;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CircleSelectionItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_selection_item,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            CircleSelectionItem item = (CircleSelectionItem) holder;

            item.bind(getItemAt(position));
            item.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    v.setPressed(true);
                    v.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            v.setPressed(false);
                            callback.onTrigger(holder.getAdapterPosition());
                        }
                    }, 200);
                }
            });
        }
    }

