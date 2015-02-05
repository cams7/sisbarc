/**
 * 
 */
package br.com.cams7.sisbarc.aal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import br.com.cams7.sisbarc.aal.vo.Led;
import br.com.cams7.util.AppException;
import br.com.cams7.util.RestUtil;

/**
 * @author cesar
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final String URL = "http://192.168.1.7:8080/acende_apaga_leds/rest/arduino/led";

	private TextView tvResponse;

	private Map<Short, ImageButton> btnSwitch;

	private Map<Short, Led.Status> currentStatus;
	private boolean statusChanged;

	private boolean isConnected;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.acende_apaga_leds);

		tvResponse = (TextView) findViewById(R.id.tvResponse);

		btnSwitch = new HashMap<Short, ImageButton>();
		// flash switch button
		btnSwitch.put((short) 11, (ImageButton) findViewById(R.id.btn_yellow));
		btnSwitch.get((short) 11).setOnClickListener(this);

		btnSwitch.put((short) 10, (ImageButton) findViewById(R.id.btn_green));
		btnSwitch.get((short) 10).setOnClickListener(this);

		btnSwitch.put((short) 9, (ImageButton) findViewById(R.id.btn_red));
		btnSwitch.get((short) 9).setOnClickListener(this);

		isConnected = RestUtil.isConnected(this);

		// check if you are connected or not
		if (isConnected)
			tvResponse.setText(R.string.msg_connected);
		else
			tvResponse.setText(R.string.msg_not_connected);

		currentStatus = new HashMap<Short, Led.Status>();
		currentStatus.put((short) 11, Led.Status.OFF);// LED Amarela
		currentStatus.put((short) 10, Led.Status.OFF);// LED Verde
		currentStatus.put((short) 9, Led.Status.OFF);// LED Vermelha

	}

	public void onClick(View view) {
		Short ledPin = null;
		switch (view.getId()) {
		case R.id.btn_yellow:
			ledPin = 11;
			break;
		case R.id.btn_green:
			ledPin = 10;
			break;
		case R.id.btn_red:
			ledPin = 9;
			break;
		default:
			break;
		}

		if (ledPin != null) {
			if (currentStatus.get(ledPin) == Led.Status.ON)
				// turn off flash
				turnOffLED(ledPin);
			else
				// turn on flash
				turnOnLED(ledPin);

		}
	}

	private static String GET(String url, String errorMsg) {
		// create HttpClient
		HttpClient httpclient = new DefaultHttpClient();

		try {
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			// receive response as inputStream
			InputStream inputStream = httpResponse.getEntity().getContent();
			if (inputStream == null) {
				Log.d(RestUtil.TAG_INPUT_STREAM, "InputStream is null");
				return errorMsg;
			}

			return RestUtil.convertInputStreamToString(inputStream);
		} catch (ClientProtocolException e) {
			Log.d(RestUtil.TAG_INPUT_STREAM, e.getLocalizedMessage(),
					e.getCause());
		} catch (IOException e) {
			Log.d(RestUtil.TAG_INPUT_STREAM, e.getLocalizedMessage(),
					e.getCause());
		}

		return errorMsg;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		protected String doInBackground(String... urls) {
			return GET(urls[0], getString(R.string.msg_error_wildfly));
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			statusChanged = false;

			String wildflyError = getString(R.string.msg_error_wildfly);

			try {
				if (wildflyError.equals(result))
					throw new AppException(wildflyError);

				JSONObject json = new JSONObject(result);

				Led led = new Led();

				final String STRING_NULL = "null";

				String jsonPinType = json.getString("pin_type");
				if (!STRING_NULL.equals(jsonPinType))
					led.setPinType(Led.PinType.valueOf(jsonPinType));

				String jsonPin = json.getString("pin");
				if (!STRING_NULL.equals(jsonPin))
					led.setPin(Short.valueOf(jsonPin));

				String jsonStatus = json.getString("status");
				if (!STRING_NULL.equals(jsonStatus))
					led.setStatus(Led.Status.valueOf(jsonStatus));

				if (led.getPinType() == null || led.getPin() == null)
					throw new AppException(getString(R.string.msg_error));

				if (led.getStatus() == null)
					throw new AppException(
							getString(R.string.msg_error_arduino));

				currentStatus.put(led.getPin(), led.getStatus());

				switch (led.getStatus()) {
				case ON:
					tvResponse.setText(getString(R.string.msg_led_on, led
							.getPinType().name() + " " + led.getPin()));
					break;
				case OFF:
					tvResponse.setText(getString(R.string.msg_led_off, led
							.getPinType().name() + " " + led.getPin()));
					break;
				default:
					break;
				}

				statusChanged = true;
			} catch (JSONException e) {
				tvResponse.setText(R.string.msg_error_monitor);
				Log.d(TAG, e.getLocalizedMessage(), e.getCause());
			} catch (AppException e) {
				tvResponse.setText(e.getMessage());
				Log.d(TAG, e.getLocalizedMessage(), e.getCause());
			}

		}
	}

	/*
	 * Turning On flash
	 */
	private void turnOnLED(short ledPin) {
		if (currentStatus.get(ledPin) == Led.Status.OFF) {
			// play sound
			playSound(ledPin);

			new HttpAsyncTask().execute(URL + "?pin_type=DIGITAL&pin=" + ledPin
					+ "&status=" + Led.Status.ON.name());

			if (statusChanged)
				toggleButtonImage(ledPin);
		}
	}

	/*
	 * Turning Off flash
	 */
	private void turnOffLED(short ledPin) {
		if (currentStatus.get(ledPin) == Led.Status.ON) {
			playSound(ledPin);

			new HttpAsyncTask().execute(URL + "?pin_type=DIGITAL&pin=" + ledPin
					+ "&status=" + Led.Status.OFF.name());

			if (statusChanged)
				toggleButtonImage(ledPin);
		}
	}

	/*
	 * Playing sound will play button toggle sound on flash on / off
	 */
	private void playSound(short ledPin) {
		if (currentStatus.get(ledPin) == Led.Status.ON)
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
		else
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);

		mp.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.release();
			}
		});
		mp.start();
	}

	/*
	 * Toggle switch button images changing image states to on / off
	 */
	private void toggleButtonImage(short ledPin) {
		if (currentStatus.get(ledPin) == Led.Status.ON)
			btnSwitch.get(ledPin).setImageResource(R.drawable.btn_switch_off);
		else
			btnSwitch.get(ledPin).setImageResource(R.drawable.btn_switch_on);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash
		if (currentStatus != null)
			for (Short pin : currentStatus.keySet())
				turnOffLED(pin);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume turn on the flash
		if (isConnected && currentStatus != null)
			for (Short pin : currentStatus.keySet())
				turnOffLED(pin);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
