package world.augma.ui.AR;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import java.util.List;

import world.augma.asset.Note;
import world.augma.work.PaintUtils;


public class RadarView implements OnLocationChangedListener{

	Context _context;
	public DataView view;
	float range;
	public static float RADIUS = 200;
	static float originX = 0 , originY = 0;

	static int radarColor = Color.parseColor("#A0E3D8C4");
	Location destinedLocation = new Location("provider");

	private List<Note> filteredNotes;
	Location currentLocation = new Location("provider");
	private double mMyLatitude = 0;
	private double mMyLongitude = 0;
	private MyCurrentLocation myCurrentLocation;


	public float[][] coordinateArray;

	public float circleOriginX;
	public float circleOriginY;
	private float mscale;
	
	
	public float x = 0;
	public float y = 0;
	public float z = 0;

	float  yaw = 0;
	double[] bearings;
	ARView arView = new ARView();
	
	public RadarView(DataView dataView, double[] bearings, List<Note> filteredNotes, Context _context){
		//this.view = dataView;
		this._context = _context;
		this.filteredNotes = filteredNotes;
		this.bearings = bearings;
		coordinateArray = new float[filteredNotes.size()][2];

		myCurrentLocation = new MyCurrentLocation(this);
		myCurrentLocation.buildGoogleApiClient(this._context);
		myCurrentLocation.start();

		calculateMetrics();
	}
	
	public void calculateMetrics(){
		circleOriginX = originX + RADIUS;
		circleOriginY = originY + RADIUS;
		
		range = (float)arView.convertToPix(10) * 50;
		mscale = range / arView.convertToPix((int)RADIUS);
	}
	
	public void paint(PaintUtils dw, float yaw) {
		
//		circleOriginX = originX + RADIUS;
//		circleOriginY = originY + RADIUS;
		this.yaw = yaw;
//		range = arView.convertToPix(10) * 1000;		/** Draw the radar */
		dw.setFill(true);
		dw.setColor(radarColor);
		dw.paintCircle(originX + RADIUS, originY + RADIUS, RADIUS);

		/** put the markers in it */
//		float scale = range / arView.convertToPix((int)RADIUS);
		/**
		 *  Your current location coordinate here.
		 * */

		
		for(int i = 0; i <filteredNotes.size();i++){
			destinedLocation.setLatitude(filteredNotes.get(i).getLatitude());
			destinedLocation.setLongitude(filteredNotes.get(i).getLongitude());
			convLocToVec(currentLocation, destinedLocation);
			float x = this.x / mscale * 5;
			float y = this.z / mscale * 5;


			if (x * x + y * y < RADIUS * RADIUS) {
				dw.setFill(true);
				dw.setColor(Color.rgb(255, 255, 255));
				dw.paintRect(x + RADIUS, y + RADIUS, 5, 5);
			}
		}
	}


	
	/** Width on screen */
	public float getWidth() {
		return RADIUS * 2;
	}

	/** Height on screen */
	public float getHeight() {
		return RADIUS * 2;
	}
	
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void convLocToVec(Location source, Location destination) {
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(source.getLatitude(), source.getLongitude(), destination
				.getLatitude(), source.getLongitude(), z);
		float[] x = new float[1];
		Location.distanceBetween(source.getLatitude(), source.getLongitude(), source
				.getLatitude(), destination.getLongitude(), x);
		if (source.getLatitude() < destination.getLatitude())
			z[0] *= -1;
		if (source.getLongitude() > destination.getLongitude())
			x[0] *= -1;

		set(x[0], (float) 0, z[0]);
	}

	@Override
	public void onLocationChanged(Location location) {
		mMyLatitude = location.getLatitude();
		mMyLongitude = location.getLongitude();
		currentLocation.setLatitude(mMyLatitude);
		currentLocation.setLongitude(mMyLongitude);
	}
}