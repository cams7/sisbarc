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
import br.com.cams7.sisbarc.aal.vo.Led.Cor;
import br.com.cams7.util.RestUtil;

/**
 * @author cesar
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final String URL = "http://192.168.1.7:8080/acende_apaga_leds/rest/arduino/led?";

	private static final String[] LEDs = { Cor.AMARELA.name(),
			Cor.VERDE.name(), Cor.VERMELHA.name() };
	private static final byte TOTAL_LEDS = (byte) LEDs.length;

	private TextView tvResponse;

	private ImageButton[] btnSwitch;

	private Led.Status[] statusLEDs = { Led.Status.APAGADA, Led.Status.APAGADA,
			Led.Status.APAGADA };

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
		if (isConnected) {
			tvResponse.setBackgroundColor(0x0000FF);
			tvResponse.setText(R.string.msg_connected);
		} else {
			tvResponse.setText(R.string.msg_not_connected);
		}

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
			if (statusLEDs[ledIndex] == Led.Status.ACESA)
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
			return GET(urls[0], getString(R.string.msg_error));
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			String errorMsg = getString(R.string.msg_error);

			tvResponse.setBackgroundColor(0x0000FF);
			if (!errorMsg.equals(result)) {
				try {
					JSONObject json = new JSONObject(result);

					Cor cor = Cor.valueOf(json.getString("cor"));
					Led.Status status = Led.Status.valueOf(json
							.getString("status"));

					Led led = new Led();
					led.setCor(cor);
					led.setStatus(status);

					if (led.getStatus() != null) {
						switch (led.getCor()) {
						case AMARELA:
							statusLEDs[0] = led.getStatus();
							break;
						case VERDE:
							statusLEDs[1] = led.getStatus();
							break;
						case VERMELHA:
							statusLEDs[2] = led.getStatus();
							break;
						default:
							break;
						}

						switch (led.getStatus()) {
						case ACESA:
							tvResponse.setText(getString(R.string.msg_led_on,
									led.getCor().name()));
							break;
						case APAGADA:
							tvResponse.setText(getString(R.string.msg_led_off,
									led.getCor().name()));
							break;
						default:
							break;
						}
					} else {
						tvResponse.setBackgroundColor(0xF7F7F7);
						tvResponse.setText(R.string.msg_error);
					}

				} catch (JSONException e) {
					Log.d(TAG, e.getLocalizedMessage(), e.getCause());
				}
			} else {
				tvResponse.setBackgroundColor(0xF7F7F7);
				tvResponse.setText(errorMsg);
			}
		}
	}

	/*
	 * Turning On flash
	 */
	private void turnOnLED(byte ledIndex) {
		if (statusLEDs[ledIndex] == Led.Status.APAGADA) {
			// play sound
			playSound(ledIndex);

			new HttpAsyncTask().execute(URL + "led=" + LEDs[ledIndex]
					+ "&status=" + Led.Status.ACESA);

			// isLEDsOn[ledIndex] = true;

			// changing button/switch image
			toggleButtonImage(ledIndex);
		}
	}

	/*
	 * Turning Off flash
	 */
	private void turnOffLED(byte ledIndex) {
		if (statusLEDs[ledIndex] == Led.Status.ACESA) {
			playSound(ledIndex);

			new HttpAsyncTask().execute(URL + "led=" + LEDs[ledIndex]
					+ "&status=" + Led.Status.APAGADA);

			// isLEDsOn[ledIndex] = false;

			// changing button/switch image
			toggleButtonImage(ledIndex);
		}
	}

	/*
	 * Playing sound will play button toggle sound on flash on / off
	 */
	private void playSound(byte ledIndex) {
		if (statusLEDs[ledIndex] == Led.Status.ACESA)
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
		if (statusLEDs[ledIndex] == Led.Status.ACESA)
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
