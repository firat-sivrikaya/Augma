package world.augma.ui.AR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.List;

import world.augma.asset.Note;
import world.augma.work.Compatibility;
import world.augma.work.PaintUtils;

public class ARView extends Activity implements SensorEventListener{

	private static Context _context;

	RadarMarkerView radarMarkerView;
	public RelativeLayout upperLayerLayout;
	static PaintUtils paintScreen;
	static DataView dataView;
	boolean isInitiated = false;
	public static float azimuth;
	public static float pitch;
	public static float roll;

	DisplayMetrics displayMetrics;


	private float RTmp[] = new float[9];
	private float Rot[] = new float[9];
	private float I[] = new float[9];
	private float grav[] = new float[3];
	private float mag[] = new float[3];
	private float results[] = new float[3];
	private SensorManager sensorMgr;
	private List<Sensor> sensors;
	private Sensor sensorGrav, sensorMag;

	static final float ALPHA = 0.25f;
	protected float[] gravSensorVals;
	protected float[] magSensorVals;

	private List<Note> filteredNotes;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		filteredNotes = (List<Note>) getIntent().getExtras().getSerializable("filteredNotes");


		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);



		upperLayerLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams upperLayerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		upperLayerLayout.setLayoutParams(upperLayerLayoutParams);
		upperLayerLayout.setBackgroundColor(Color.TRANSPARENT);

		_context = this;

		radarMarkerView = new RadarMarkerView(this, displayMetrics, upperLayerLayout);

		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		if(!isInitiated){
			dataView = new DataView(ARView.this, filteredNotes);
			paintScreen = new PaintUtils();
			isInitiated = true;
		}

	}

	public static Context getContext() {
		return _context;
	}

	public int convertToPix(int val){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, _context.getResources().getDisplayMetrics());
		return (int)px;

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();


		sensorMgr.unregisterListener(this, sensorGrav);
		sensorMgr.unregisterListener(this, sensorMag);
		sensorMgr = null;
	}

	@Override
	protected void onResume() {

		super.onResume();



		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sensorGrav = sensors.get(0);
		}

		sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (sensors.size() > 0) {
			sensorMag = sensors.get(0);
		}

		sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
		sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(dataView.getMyCurrentLocation() != null)
			dataView.getMyCurrentLocation().stop();
	}

	@Override
	public void onSensorChanged(SensorEvent evt) {


		if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			gravSensorVals = lowPass(evt.values.clone(), gravSensorVals);
			grav[0] = evt.values[0];
			grav[1] = evt.values[1];
			grav[2] = evt.values[2];

		} else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magSensorVals = lowPass(evt.values.clone(), magSensorVals);
			mag[0] = evt.values[0];
			mag[1] = evt.values[1];
			mag[2] = evt.values[2];

		}

		if (gravSensorVals != null && magSensorVals != null) {
			SensorManager.getRotationMatrix(RTmp, I, gravSensorVals, magSensorVals);

			int rotation = Compatibility.getRotation(this);

			if (rotation == 1) {
				SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);
			} else {
				SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, Rot);
			}

			SensorManager.getOrientation(Rot, results);

			ARView.azimuth = (float)(((results[0]*180)/Math.PI)+180);
			ARView.pitch = (float)(((results[1]*180/Math.PI))+90);
			ARView.roll = (float)(((results[2]*180/Math.PI)));

			radarMarkerView.postInvalidate();
		}
	}

	protected float[] lowPass( float[] input, float[] output ) {
		if ( output == null ) return input;

		for ( int i=0; i<input.length; i++ ) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}


}

class RadarMarkerView extends View{

	CameraViewActivity arView;
	DisplayMetrics displayMetrics;
	RelativeLayout upperLayoutView = null;
	public RadarMarkerView(Context context, DisplayMetrics displayMetrics, RelativeLayout rel) {
		super(context);

		arView = (CameraViewActivity) context;
		this.displayMetrics = displayMetrics;
		upperLayoutView = rel;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CameraViewActivity.paintScreen.setWidth(canvas.getWidth());
		CameraViewActivity.paintScreen.setHeight(canvas.getHeight());
		CameraViewActivity.paintScreen.setCanvas(canvas);

		if (!CameraViewActivity.dataView.isInited()) {

			CameraViewActivity.dataView.init(CameraViewActivity.paintScreen.getWidth(), CameraViewActivity.paintScreen.getHeight(),CameraViewActivity.mCamera, displayMetrics,upperLayoutView);
		}

		CameraViewActivity.dataView.draw(CameraViewActivity.paintScreen, CameraViewActivity.azimuth, CameraViewActivity.pitch, CameraViewActivity.roll);
	}
}