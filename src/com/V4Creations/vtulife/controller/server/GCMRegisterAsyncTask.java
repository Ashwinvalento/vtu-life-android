package com.V4Creations.vtulife.controller.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.V4Creations.vtulife.util.JSONParser;
import com.V4Creations.vtulife.util.Settings;
import com.V4Creations.vtulife.util.VTULifeConstance;
import com.V4Creations.vtulife.util.system.SystemFeatureChecker;
import com.V4Creations.vtulife.view.activity.VTULifeMainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import de.keyboardsurfer.android.widget.crouton.Style;

public class GCMRegisterAsyncTask extends AsyncTask<String, String, Boolean> {
	String TAG = "GCMRegisterAsyncTask";
	private String mGCMRegisterIdString;
	private VTULifeMainActivity mVtuLifeMainActivity;
	private static final String TAG_RESULT = "result",
			RESULT_SUCCESS_VALUSE = "success",
			POST_PARAM_REGISTER_ID = "gcm_regid",
			POST_PARAM_DEVICE_ID = "android_id";
	

	public GCMRegisterAsyncTask(VTULifeMainActivity vtuLifeMainActivity) {
		mVtuLifeMainActivity = vtuLifeMainActivity;
	}

	@Override
	protected Boolean doInBackground(String... asyncParams) {
		boolean result = true;
		if (SystemFeatureChecker.isInternetConnection(mVtuLifeMainActivity)) {
			try {
				mGCMRegisterIdString = GoogleCloudMessaging.getInstance(
						mVtuLifeMainActivity).register(VTULifeConstance.GCM_SENDER_ID);
				JSONParser jParser = new JSONParser();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(POST_PARAM_REGISTER_ID,
						mGCMRegisterIdString));
				params.add(new BasicNameValuePair(POST_PARAM_DEVICE_ID,
						SystemFeatureChecker.getDeviceUuid(mVtuLifeMainActivity)));
				JSONObject jsonObject = jParser.makeHttpRequest(
						VTULifeConstance.WEB_URL + VTULifeConstance.GCM_REGISTER, "POST",
						params);
				if (!jsonObject.getString(TAG_RESULT).equals(
						RESULT_SUCCESS_VALUSE))
					result = false;
			} catch (Exception e) {
				result = false;
			}
		} else
			result = false;
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			Settings.storeRegistrationIdWithAppVersion(mVtuLifeMainActivity,
					mGCMRegisterIdString);
			mVtuLifeMainActivity.showCrouton("Registered for notification",
					Style.INFO, true);
		}
	}
}