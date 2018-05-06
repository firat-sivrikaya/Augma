package world.augma.ui.note;

import android.location.Location;
import android.graphics.Rect;
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
    private byte[] background;

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
        multiSelect = builder.withLeftAdapter(leftAdapter).withRightAdapter(rightAdapter).build();
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

        public CircleSelectionItem(View itemView) {
            super(itemView);
            circleName = itemView.findViewById(R.id.notePostPreviewSelectCircleItemName);
        }

        public void bind(Circle item) {
            circleName.setText(item.getName());
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

    class CircleSelectionItemDecorator extends RecyclerView.ItemDecoration {

        private int size;

        public CircleSelectionItemDecorator(int size) {
            this.size = size;
        }

        /**
         * Retrieve any offsets for the given item. Each field of <code>outRect</code> specifies
         * the number of pixels that the item view should be inset by, similar to padding or margin.
         * The default implementation sets the bounds of outRect to 0 and returns.
         * <p>
         * <p>
         * If this ItemDecoration does not affect the positioning of item views, it should set
         * all four fields of <code>outRect</code> (left, top, right, bottom) to zero
         * before returning.
         * <p>
         * <p>
         * If you need to access Adapter for additional data, you can call
         * {@link RecyclerView#getChildAdapterPosition(View)} to get the adapter position of the
         * View.
         *
         * @param outRect Rect to receive the output.
         * @param view    The child view to decorate
         * @param parent  RecyclerView this ItemDecoration is decorating
         * @param state   The current state of RecyclerView.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = size;
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
        }
    }

