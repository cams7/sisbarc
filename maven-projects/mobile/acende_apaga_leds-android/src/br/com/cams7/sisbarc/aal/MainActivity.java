/**
 * 
 */
package br.com.cams7.sisbarc.aal;

import java.io.IOException;
import java.io.InputStream;

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
import br.com.cams7.sisbarc.aal.vo.Led.Color;
import br.com.cams7.sisbarc.aal.vo.Led.Status;
import br.com.cams7.util.AppException;
import br.com.cams7.util.RestUtil;

/**
 * @author cesar
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final String URL = "http://192.168.1.7:8080/acende_apaga_leds/rest/arduino/led";

	private static final Color[] LEDs = Color.values();
	private static final byte TOTAL_LEDS = (byte) LEDs.length;

	private TextView tvResponse;

	private ImageButton[] btnSwitch;

	private Led.Status[] currentStatus;
	private boolean statusChanged;

	private boolean isConnected;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.acende_apaga_leds);

		tvResponse = (TextView) findViewById(R.id.tvResponse);

		btnSwitch = new ImageButton[TOTAL_LEDS];

		// flash switch button
		btnSwitch[0] = (ImageButton) findViewById(R.id.btn_yellow);
		btnSwitch[0].setOnClickListener(this);

		btnSwitch[1] = (ImageButton) findViewById(R.id.btn_green);
		btnSwitch[1].setOnClickListener(this);

		btnSwitch[2] = (ImageButton) findViewById(R.id.btn_red);
		btnSwitch[2].setOnClickListener(this);

		isConnected = RestUtil.isConnected(this);

		// check if you are connected or not
		if (isConnected)
			tvResponse.setText(R.string.msg_connected);
		else
			tvResponse.setText(R.string.msg_not_connected);

		currentStatus = new Status[TOTAL_LEDS];
		for (byte i = 0x00; i < TOTAL_LEDS; i++)
			currentStatus[i] = Led.Status.OFF;

	}

	public void onClick(View view) {
		Byte ledIndex = null;
		switch (view.getId()) {
		case R.id.btn_yellow:
			ledIndex = 0;
			break;
		case R.id.btn_green:
			ledIndex = 1;
			break;
		case R.id.btn_red:
			ledIndex = 2;
			break;
		default:
			break;
		}

		if (ledIndex != null) {
			if (currentStatus[ledIndex] == Led.Status.ON)
				// turn off flash
				turnOffLED(ledIndex);
			else
				// turn on flash
				turnOnLED(ledIndex);

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

				String jsonColor = json.getString("color");
				if (!STRING_NULL.equals(jsonColor))
					led.setColor(Color.valueOf(jsonColor));

				String jsonStatus = json.getString("status");
				if (!STRING_NULL.equals(jsonStatus))
					led.setStatus(Led.Status.valueOf(jsonStatus));

				if (led.getColor() == null)
					throw new AppException(getString(R.string.msg_error));

				if (led.getStatus() == null)
					throw new AppException(
							getString(R.string.msg_error_arduino));

				switch (led.getColor()) {
				case YELLOW:
					currentStatus[0] = led.getStatus();
					break;
				case GREEN:
					currentStatus[1] = led.getStatus();
					break;
				case RED:
					currentStatus[2] = led.getStatus();
					break;
				default:
					break;
				}

				switch (led.getStatus()) {
				case ON:
					tvResponse.setText(getString(R.string.msg_led_on, led
							.getColor().name()));
					break;
				case OFF:
					tvResponse.setText(getString(R.string.msg_led_off, led
							.getColor().name()));
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
	private void turnOnLED(byte ledIndex) {
		if (currentStatus[ledIndex] == Led.Status.OFF) {
			// play sound
			playSound(ledIndex);

			new HttpAsyncTask().execute(URL + "?led=" + LEDs[ledIndex].name()
					+ "&status=" + Led.Status.ON.name());

			if (statusChanged)
				toggleButtonImage(ledIndex);
		}
	}

	/*
	 * Turning Off flash
	 */
	private void turnOffLED(byte ledIndex) {
		if (currentStatus[ledIndex] == Led.Status.ON) {
			playSound(ledIndex);

			new HttpAsyncTask().execute(URL + "?led=" + LEDs[ledIndex].name()
					+ "&status=" + Led.Status.OFF.name());

			if (statusChanged)
				toggleButtonImage(ledIndex);
		}
	}

	/*
	 * Playing sound will play button toggle sound on flash on / off
	 */
	private void playSound(byte ledIndex) {
		if (currentStatus[ledIndex] == Led.Status.ON)
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
	private void toggleButtonImage(byte ledIndex) {
		if (currentStatus[ledIndex] == Led.Status.ON)
			btnSwitch[ledIndex].setImageResource(R.drawable.btn_switch_off);
		else
			btnSwitch[ledIndex].setImageResource(R.drawable.btn_switch_on);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash
		for (byte i = 0x00; i < TOTAL_LEDS; i++)
			turnOffLED(i);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume turn on the flash
		if (isConnected)
			for (byte i = 0x00; i < TOTAL_LEDS; i++)
				turnOffLED(i);
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
