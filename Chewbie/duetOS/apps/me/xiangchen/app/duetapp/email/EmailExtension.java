package me.xiangchen.app.duetapp.email;

import java.util.Calendar;

import me.xiangchen.app.duetapp.AppExtension;
import me.xiangchen.app.duetos.LauncherManager;
import me.xiangchen.app.duetos.R;
import me.xiangchen.technique.doubleflip.xacAuthenticSenseFeatureMaker;
import me.xiangchen.technique.flipsense.xacFlipSenseFeatureMaker;
import me.xiangchen.technique.handsense.xacHandSenseFeatureMaker;
import me.xiangchen.technique.sharesense.xacShareSenseFeatureMaker;
import me.xiangchen.technique.touchsense.xacTouchSenseFeatureMaker;
import me.xiangchen.ui.xacToast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

@SuppressLint("NewApi")
public class EmailExtension extends AppExtension {

	xacToast toast;
	
	public EmailExtension(Context context) {
		EmailManager.setWatch(this);
		
		toast = new xacToast(context);
		toast.setImage(R.drawable.email_small);
	}
	
	@Override
	public void doResume() {
		showText("Email");
	}
	
	@Override
	public void doAccelerometer(float[] values) {
		xacHandSenseFeatureMaker.updateWatchAccel(values);
		xacHandSenseFeatureMaker.addWatchFeatureEntry();
		
		xacTouchSenseFeatureMaker.updateWatchAccel(values);
		xacTouchSenseFeatureMaker.addWatchFeatureEntry();
		
		xacFlipSenseFeatureMaker.updateWatchAccel(values);
		xacFlipSenseFeatureMaker.addWatchFeatureEntry();
		
		xacShareSenseFeatureMaker.updateWatchAccel(values);
		xacShareSenseFeatureMaker.addWatchFeatureEntry();
		
		xacAuthenticSenseFeatureMaker.updateWatchAccel(values);
		xacAuthenticSenseFeatureMaker.addWatchFeatureEntry();
		
//		if(norm(values) > 10) {
//			EmailManager.freezeInterface(norm(values) > 10);
//		}
		
//		Log.d(LauncherManager.LOGTAG, String.format("%.2f", norm(values)));
	}
	
	@Override
	public void doTouch(ControlTouchEvent event) {

		int action = event.getAction();

		switch (action) {
		case Control.Intents.TOUCH_ACTION_PRESS:
			int watchMode = xacShareSenseFeatureMaker.doClassification();
			if (watchMode == xacShareSenseFeatureMaker.PRIVATE) {
				LauncherManager.showText(EmailManager.getNumUnnotifiedEmails() + " new email(s)");
			}
			break;
		case Control.Intents.TOUCH_ACTION_RELEASE:
			
			break;
		}
	}

	@Override
	public void doSwipe(int direction) {
		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTimeInMillis();
		switch (direction) {
		case Control.Intents.SWIPE_DIRECTION_RIGHT:
			EmailManager.updateWatchGesture(
					EmailManager.SWIPECLOSE, curTime);
			break;
		case Control.Intents.SWIPE_DIRECTION_LEFT:
			EmailManager.updateWatchGesture(
					EmailManager.SWIPEOPEN, curTime);
			break;
		}
	}
	
	public void showNotification(int flag) {
		if(LauncherManager.getWatchMuteness() == true) {
			toast.setImage(R.drawable.mute_small);
		}
		else {
			toast.setImage(R.drawable.email_small);
		}
		
		if(flag > 0) {
			toast.fadeIn(null);
			Bitmap bitmap = toast.getBitmap();
			updateWatchVisual(bitmap, false);
			buzz(100);
			
		} else {
			toast.fadeOut();
			if(toast.getAlpha() <= xacToast.LOWALPHA * 2) {
				updateWatchVisual(null, false);
			}
		}
	}
	
	private float norm(float[] values) {
		float tmp = 0;

		for (int i = 0; i < values.length; i++) {
			tmp += values[i] * values[i];
		}

		return (float) Math.sqrt(tmp);
	}
}
