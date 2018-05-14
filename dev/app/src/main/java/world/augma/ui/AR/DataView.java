package world.augma.ui.AR;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import java.util.List;

import world.augma.asset.Note;
import world.augma.work.Camera;
import world.augma.work.PaintUtils;
import world.augma.work.RadarLines;


/**
 * 
 * Currently the markers are plotted with reference to line parallel to the earth surface.
 * We are working to include the elevation and height factors.
 * 
 * */


public class DataView implements OnLocationChangedListener{


	double[] bearings;

	Location destinedLocation = new Location("provider");
	

	boolean isInit = false;

	Context _context;
	/** width and height of the view*/


	float yaw = 0;
	float pitch = 0;
	float roll = 0;

	DisplayMetrics displayMetrics;
	RadarView radarPoints;

	RadarLines lrl = new RadarLines();
	RadarLines rrl = new RadarLines();
	float rx = 10, ry = 20;
	public float bearing;


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



			this.displayMetrics = displayMetrics;

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

		dw.paintObj(radarPoints, rx+PaintUtils.XPADDING, ry+PaintUtils.YPADDING, this.yaw, 1, -this.yaw);
		dw.setFill(false);
		dw.setColor(Color.argb(100,220,0,0));
		dw.paintLine( lrl.x, lrl.y, rx+RadarView.RADIUS, ry+RadarView.RADIUS);
		dw.paintLine( rrl.x, rrl.y, rx+RadarView.RADIUS, ry+RadarView.RADIUS);
		dw.setColor(Color.rgb(255,255,255));
		dw.setFontSize(12);

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
}