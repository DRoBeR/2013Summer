package me.xiangchen.app.duettech;

import java.util.Calendar;

import me.xiangchen.technique.bumpsense.xacBumpSenseFeatureMaker;
import me.xiangchen.technique.doubleflip.xacAuthenticSenseFeatureMaker;
import me.xiangchen.technique.flipsense.xacFlipSenseFeatureMaker;
import me.xiangchen.technique.handsense.xacHandSenseFeatureMaker;
import me.xiangchen.technique.tiltsense.xacTiltSenseFeatureMaker;
import me.xiangchen.technique.touchsense.xacTouchSenseFeatureMaker;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEventListener;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorException;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorManager;

public class DuetTechExtension extends ControlExtension {

	public final static String LOGTAG = "DuetTech";
	public final static int WATCHACCELFPS = 10;

	int width;
	int height;

	RelativeLayout layout;
	Canvas canvas;
	Bitmap bitmap;
	TextView textView;

	AccessorySensor sensor = null;
	AccessorySensorEventListener listener;

	public DuetTechExtension(Context context, String hostAppPackageName) {
		super(context, hostAppPackageName);
		DuetTechManager.setWatch(this);
		
		width = getSupportedControlWidth(context);
		height = getSupportedControlHeight(context);

		layout = new RelativeLayout(context);
		textView = new TextView(context);
		textView.setText("Duet tech");
		textView.setTextSize(9);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.WHITE);
		textView.layout(0, 0, width, height);
		layout.addView(textView);

		AccessorySensorManager manager = new AccessorySensorManager(context,
				hostAppPackageName);
		sensor = manager.getSensor(Sensor.SENSOR_TYPE_ACCELEROMETER);

		listener = new AccessorySensorEventListener() {

			public void onSensorEvent(AccessorySensorEvent sensorEvent) {
				float[] values = sensorEvent.getSensorValues();

				xacHandSenseFeatureMaker.updateWatchAccel(values);
				xacHandSenseFeatureMaker.addWatchFeatureEntry();

				xacFlipSenseFeatureMaker.updateWatchAccel(values);
				xacFlipSenseFeatureMaker.addWatchFeatureEntry();

				xacTouchSenseFeatureMaker.updateWatchAccel(values);
				xacTouchSenseFeatureMaker.addWatchFeatureEntry();

				xacAuthenticSenseFeatureMaker.updateWatchAccel(values);
				xacAuthenticSenseFeatureMaker.addWatchFeatureEntry();

				xacAuthenticSenseFeatureMaker.updateWatchAccel(values);
				xacAuthenticSenseFeatureMaker.addWatchFeatureEntry();

				xacTiltSenseFeatureMaker.updateWatchAccel(values);
				xacTiltSenseFeatureMaker.addWatchFeatureEntry();

				xacBumpSenseFeatureMaker.updateWatchAccel(values);
				xacBumpSenseFeatureMaker.addWatchFeatureEntry();
			}
		};
	}
	
	@Override
	public void onTouch(final ControlTouchEvent event) {
		long curTime = Calendar.getInstance().getTimeInMillis();
		if(event.getAction() == Control.Intents.TOUCH_ACTION_PRESS) {
			DuetTechManager.updateWatchGesture(DuetTechManager.TAP, curTime);
		}
	}
	
	@Override
	public void onSwipe(int direction) {
		long curTime = Calendar.getInstance().getTimeInMillis();
		switch(direction) {
		case Control.Intents.SWIPE_DIRECTION_RIGHT:
			DuetTechManager
					.updateWatchGesture(DuetTechManager.SWIPECLOSE, curTime);
			break;
		case Control.Intents.SWIPE_DIRECTION_LEFT:
			DuetTechManager.updateWatchGesture(DuetTechManager.SWIPEOPEN, curTime);
			break;
		}
	}


	@Override
	public void onResume() {
		setScreenState(Control.Intents.SCREEN_STATE_ON);

		// Start listening for sensor updates.
		if (sensor != null) {
			try {
				sensor.registerFixedRateListener(listener,
						Sensor.SensorRates.SENSOR_DELAY_FASTEST);
			} catch (AccessorySensorException e) {
				 Log.d(LOGTAG, "Failed to register listener");
				// LauncherManager.doAndriodToast("Failed to register listener");
			}
		}

		updateVisual();
	}
	
	@Override
	public void onPause() {
		// Stop sensor
		if (sensor != null) {
			sensor.unregisterListener();
		}
	}

	@Override
	public void onDestroy() {
		// Stop sensor
		if (sensor != null) {
			sensor.unregisterListener();
			sensor = null;
		}
	}

	private void updateVisual() {

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		layout.draw(canvas);

		showBitmap(bitmap);
	}

	public static int getSupportedControlWidth(Context context) {
		return context.getResources().getDimensionPixelSize(
				R.dimen.smart_watch_control_width);
	}

	public static int getSupportedControlHeight(Context context) {
		return context.getResources().getDimensionPixelSize(
				R.dimen.smart_watch_control_height);
	}
	
	public void updateVisual(Bitmap bitmap) {
		showBitmap(bitmap);
	}
}
