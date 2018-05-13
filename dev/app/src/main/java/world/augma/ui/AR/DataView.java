package world.augma.ui.AR;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import world.augma.R;
import world.augma.asset.Note;
import world.augma.ui.note.UINoteDisplay;
import world.augma.work.Camera;
import world.augma.work.PaintUtils;
import world.augma.work.RadarLines;
import world.augma.work.visual.S3;


/**
 * 
 * Currently the markers are plotted with reference to line parallel to the earth surface.
 * We are working to include the elevation and height factors.
 * 
 * */


public class DataView implements OnLocationChangedListener{

	RelativeLayout[] locationMarkerView;
	RelativeLayout.LayoutParams[] layoutParams;

	int[] nextXofText ;
	ArrayList<Integer> 	nextYofText = new ArrayList<Integer>();

	double[] bearings;
	float angleToShift;
	float yPosition;

	Location destinedLocation = new Location("provider");
	
	String[] places = new String[] {"SF Art Commission", "SF Dept. of Public Health", "SF Ethics Comm", "SF Conservatory of Music", "All Star Cafe", 
			"Magic Curry Cart", "SF SEO Marketing", " SF Honda", "SF Mun Transport Agency", "SF Parking Citation", "Mayors Office of Housing", "SF Redev Agency", "Catario Patrice", "Bank of America", 
			"SF Retirement System", "Bank of America Mortage", "Writers Corp.", "Van Nes Keno Mkt."};
	/** is the view Inited? */
	boolean isInit = false;
	boolean isDrawing = true;
	boolean isFirstEntry;
	Context _context;
	/** width and height of the view*/
	int width, height;
	android.hardware.Camera camera;

	float yawPrevious;
	float yaw = 0;
	float pitch = 0;
	float roll = 0;

	DisplayMetrics displayMetrics;
	RadarView radarPoints;

	RadarLines lrl = new RadarLines();
	RadarLines rrl = new RadarLines();
	float rx = 10, ry = 20;
	public float addX = 0, addY = 0;
	public float degreetopixelWidth;
	public float degreetopixelHeight;
	public float pixelstodp;
	public float bearing;

	public int[][] coordinateArray = new int[20][2];
	public int locationBlockWidth;
	public int locationBlockHeight;

	public float deltaX;
	public float deltaY;
	Bitmap bmp;
	private ARView arView;

	private List<Note> filteredNotes;

	Location currentLocation = new Location("provider");
	private double mMyLatitude = 0;
	private double mMyLongitude = 0;
	private MyCurrentLocation myCurrentLocation;

	public DataView(Context ctx, List<Note> filteredNotes) {
		this._context = ctx;
		this.filteredNotes = filteredNotes;
		//this.arView = arView;
	}

	public MyCurrentLocation getMyCurrentLocation()
	{
		return myCurrentLocation;
	}
	public boolean isInited() {
		return isInit;
	}

	public void init(int widthInit, int heightInit, android.hardware.Camera camera, DisplayMetrics displayMetrics, RelativeLayout background) {
		try {

			myCurrentLocation = new MyCurrentLocation(this);
			myCurrentLocation.buildGoogleApiClient(this._context);
			myCurrentLocation.start();

			locationMarkerView = new RelativeLayout[filteredNotes.size()];
			layoutParams = new RelativeLayout.LayoutParams[filteredNotes.size()];


			nextXofText = new int[filteredNotes.size()];
			
			for(int i=0;i<filteredNotes.size();i++){
				layoutParams[i] = new RelativeLayout.LayoutParams(200, 200);

				ImageView img = new ImageView(_context);
				locationMarkerView[i] = new RelativeLayout(_context);
				S3.fetchNotePreviewImage(this._context,  img, filteredNotes.get(i).getOwner().getUserID(), filteredNotes.get(i).getNoteID());
				locationMarkerView[i].setId(i);
				//layoutParams[i].setMargins(displayMetrics.widthPixels/2, displayMetrics.heightPixels/2, 0, 0);
				locationMarkerView[i].setLayoutParams(layoutParams[i]);
				//img.setLayoutParams(new  RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

				locationMarkerView[i].addView(img);
				background.addView(locationMarkerView[i]);
				locationMarkerView[i].setTag(filteredNotes.get(i));

				locationMarkerView[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						RelativeLayout image = (RelativeLayout) v;
						if(image.getTag() != null)
						{
							Note nt = (Note)image.getTag();
							Intent intent = new Intent(_context, UINoteDisplay.class);
							intent.putExtra("obj", nt);
							_context.startActivity(intent,
									ActivityOptionsCompat.makeCustomAnimation(_context, R.anim.fade_in, R.anim.fade_out).toBundle());
						}
					}
				});
			}

			this.displayMetrics = displayMetrics;
			this.degreetopixelWidth = this.displayMetrics.widthPixels / camera.getParameters().getHorizontalViewAngle();
			this.degreetopixelHeight = this.displayMetrics.heightPixels / camera.getParameters().getVerticalViewAngle();
			Log.e("camera.getParameters().getHorizontalViewAngle()==" ,""+camera.getParameters().getHorizontalViewAngle());

			bearings = new double[filteredNotes.size()];


			for(int i = 0; i <filteredNotes.size();i++){
				destinedLocation.setLatitude(filteredNotes.get(i).getLatitude());
				destinedLocation.setLongitude(filteredNotes.get(i).getLongitude());
				bearing = currentLocation.bearingTo(destinedLocation);

				if(bearing < 0){
					bearing  = 360 + bearing;
				}
				bearings[i] = bearing;

			}
			radarPoints = new RadarView(this, bearings, filteredNotes, _context);
			this.camera = camera;
			width = widthInit;
			height = heightInit;
			
			lrl.set(0, -RadarView.RADIUS);
			lrl.rotate(Camera.DEFAULT_VIEW_ANGLE / 2);
			lrl.add(rx + RadarView.RADIUS, ry + RadarView.RADIUS);
			rrl.set(0, -RadarView.RADIUS);
			rrl.rotate(-Camera.DEFAULT_VIEW_ANGLE / 2);
			rrl.add(rx + RadarView.RADIUS, ry + RadarView.RADIUS);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		/*
		 * initialization is done, so dont call init() again.
		 * */
		isInit = true;
	}

	public void draw(PaintUtils dw, float yaw, float pitch, float roll) {


		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;

		// Draw Radar
		String	dirTxt = "";
		int bearing = (int) this.yaw; 
		int range = (int) (this.yaw / (360f / 16f));
		if (range == 15 || range == 0) dirTxt = "N"; 
		else if (range == 1 || range == 2) dirTxt = "NE"; 
		else if (range == 3 || range == 4) dirTxt = "E"; 
		else if (range == 5 || range == 6) dirTxt = "SE";
		else if (range == 7 || range == 8) dirTxt= "S"; 
		else if (range == 9 || range == 10) dirTxt = "SW"; 
		else if (range == 11 || range == 12) dirTxt = "W"; 
		else if (range == 13 || range == 14) dirTxt = "NW";


		radarPoints.view = this;

		dw.paintObj(radarPoints, rx+PaintUtils.XPADDING, ry+PaintUtils.YPADDING, -this.yaw, 1, this.yaw);
		dw.setFill(false);
		dw.setColor(Color.argb(100,220,0,0));
		dw.paintLine( lrl.x, lrl.y, rx+RadarView.RADIUS, ry+RadarView.RADIUS);
		dw.paintLine( rrl.x, rrl.y, rx+RadarView.RADIUS, ry+RadarView.RADIUS);
		dw.setColor(Color.rgb(255,255,255));
		dw.setFontSize(12);
		//radarText(dw, "" + bearing + ((char) 176) + " " + dirTxt, rx + RadarView.RADIUS, ry - 5, true, false, -1);

		drawTextBlock(dw);
	}

	void drawPOI(PaintUtils dw, float yaw){
		if(isDrawing){
			dw.paintObj(radarPoints, rx+PaintUtils.XPADDING, ry+PaintUtils.YPADDING, -this.yaw, 1, this.yaw);
			isDrawing = false;
		}
	}

	void radarText(PaintUtils dw, String txt, float x, float y, boolean bg, boolean isLocationBlock, int count) {

		float padw = 4, padh = 2;
		float w = dw.getTextWidth(txt) + padw * 2;
		float h;
		if(isLocationBlock){
			h = dw.getTextAsc() + dw.getTextDesc() + padh * 2+10;
		}else{
			h = dw.getTextAsc() + dw.getTextDesc() + padh * 2;
		}
		if (bg) {

			if(isLocationBlock){
				layoutParams[count].setMargins((int)(x - w / 2 - 10), (int)(y - h / 2 - 10), 0, 0);
				locationMarkerView[count].setLayoutParams(layoutParams[count]);

			}else{
				dw.setColor(Color.rgb(0, 0, 0));
				dw.setFill(true);
				dw.paintRect((x - w / 2) + PaintUtils.XPADDING , (y - h / 2) + PaintUtils.YPADDING, w, h);
				pixelstodp = (padw + x - w / 2)/((displayMetrics.density)/160);
				dw.setColor(Color.rgb(255, 255, 255));
				dw.setFill(false);
				dw.paintText((padw + x -w/2)+PaintUtils.XPADDING, ((padh + dw.getTextAsc() + y - h / 2)) + PaintUtils.YPADDING,txt);
			}
		}

	}

	String checkTextToDisplay(String str){

		if(str.length()>15){
			str = str.substring(0, 15)+"...";
		}
		return str;

	}

	void drawTextBlock(PaintUtils dw){

		for(int i = 0; i<bearings.length;i++){
			if(bearings[i]<0){

				if(this.pitch != 90){
					yPosition = (this.pitch - 90) * this.degreetopixelHeight+200;
				}else{
					yPosition = (float)this.height/2;
				}

				bearings[i] = 360 - bearings[i];
				angleToShift = (float)bearings[i] - this.yaw;
				nextXofText[i] = (int)(angleToShift*degreetopixelWidth);
				yawPrevious = this.yaw;
				isDrawing = true;
				radarText(dw, places[i], nextXofText[i], yPosition, true, true, i);
				coordinateArray[i][0] =  nextXofText[i];
				coordinateArray[i][1] =   (int)yPosition;

			}else{
				angleToShift = (float)bearings[i] - this.yaw;

				if(this.pitch != 90){
					yPosition = (this.pitch - 90) * this.degreetopixelHeight+200;
				}else{
					yPosition = (float)this.height/2;
				}


				nextXofText[i] = (int)((displayMetrics.widthPixels/2)+(angleToShift*degreetopixelWidth));
				if(Math.abs(coordinateArray[i][0] - nextXofText[i]) > 50){
					radarText(dw, places[i], (nextXofText[i]), yPosition, true, true, i);
					coordinateArray[i][0] =  (int)((displayMetrics.widthPixels/2)+(angleToShift*degreetopixelWidth));
					coordinateArray[i][1] =  (int)yPosition;

					isDrawing = true;
				}else{
					radarText(dw, places[i],coordinateArray[i][0],yPosition, true, true, i);
					isDrawing = false;
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		mMyLatitude = location.getLatitude();
		mMyLongitude = location.getLongitude();
		currentLocation.setLatitude(mMyLatitude);
		currentLocation.setLongitude(mMyLongitude);
//			for(int i = 0; i < filteredNotes.size(); i++) {
//				degreesOfNotes[i] = calculateDegreeOfTheNote(filteredNotes.get(i));
//			}



	}

	public class NearbyPlacesList extends BaseAdapter{

		ArrayList<Integer> matchIDs = new ArrayList<Integer>();
		public NearbyPlacesList(ArrayList<Integer> matchID){
			matchIDs = matchID;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return matchIDs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}