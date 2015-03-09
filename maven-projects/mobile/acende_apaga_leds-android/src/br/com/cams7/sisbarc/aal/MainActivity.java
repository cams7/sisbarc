/**
 * 
 */
package br.com.cams7.sisbarc.aal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
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
import br.com.cams7.sisbarc.aal.vo.LED;
import br.com.cams7.sisbarc.aal.vo.LED.EstadoLED;
import br.com.cams7.util.AppException;
import br.com.cams7.util.RestUtil;

/**
 * @author cesar
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final String URL = "http://192.168.1.7:8080/acende_apaga_leds/rest/arduino";

	private static final byte D11_LED_AMARELO = 11; // Pino 11 PWM
	private static final byte D10_LED_VERDE = 10; // Pino 10 PWM
	private static final byte D09_LED_VERMELHO = 9; // Pino 09 PWM
	private static final byte D06_LED_AMARELO = 6; // Pino 06 PWM
	private static final byte D05_LED_VERDE = 5; // Pino 05 PWM
	private static final byte D04_LED_VERMELHO = 4; // Pino 04 Digital

	private static final byte PINOS_LEDS[] = { D11_LED_AMARELO, D10_LED_VERDE,
			D09_LED_VERMELHO, D06_LED_AMARELO, D05_LED_VERDE, D04_LED_VERMELHO };
	private static final int CORES_LEDS[] = { R.string.btn_yellow,
			R.string.btn_green, R.string.btn_red, R.string.btn_yellow,
			R.string.btn_green, R.string.btn_red };

	private TextView tvResponse;

	private ImageButton botoesLEDs[];

	private EstadoLED[] estadosLEDs;

	private boolean isConnected;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.acende_apaga_leds);

		tvResponse = (TextView) findViewById(R.id.tvResponse);

		botoesLEDs = new ImageButton[PINOS_LEDS.length];
		// flash switch button
		botoesLEDs[0] = (ImageButton) findViewById(R.id.btn_d11_led_amarelo);
		botoesLEDs[0].setOnClickListener(this);

		botoesLEDs[1] = (ImageButton) findViewById(R.id.btn_d10_led_verde);
		botoesLEDs[1].setOnClickListener(this);

		botoesLEDs[2] = (ImageButton) findViewById(R.id.btn_d09_led_vermelho);
		botoesLEDs[2].setOnClickListener(this);

		botoesLEDs[3] = (ImageButton) findViewById(R.id.btn_d06_led_amarelo);
		botoesLEDs[3].setOnClickListener(this);

		botoesLEDs[4] = (ImageButton) findViewById(R.id.btn_d05_led_verde);
		botoesLEDs[4].setOnClickListener(this);

		botoesLEDs[5] = (ImageButton) findViewById(R.id.btn_d04_led_vermelho);
		botoesLEDs[5].setOnClickListener(this);

		isConnected = RestUtil.isConnected(this);

		// check if you are connected or not
		if (isConnected)
			tvResponse.setText(R.string.msg_connected);
		else
			tvResponse.setText(R.string.msg_not_connected);

		estadosLEDs = new EstadoLED[PINOS_LEDS.length];
		apagaLEDs();

	}

	private final void apagaLEDs() {
		for (byte i = 0x00; i < PINOS_LEDS.length; i++) {
			estadosLEDs[i] = EstadoLED.APAGADO;
			alteraImagemBotao(i);
		}
	}

	private final byte getIndicePino(final byte pin) {
		byte indicePino = 0x00;
		for (; indicePino < PINOS_LEDS.length; indicePino++)
			if (PINOS_LEDS[indicePino] == pin)
				break;
		return indicePino;
	}

	public void onClick(View view) {
		Byte pino = null;
		switch (view.getId()) {
		case R.id.btn_d11_led_amarelo:
			pino = D11_LED_AMARELO;
			break;
		case R.id.btn_d10_led_verde:
			pino = D10_LED_VERDE;
			break;
		case R.id.btn_d09_led_vermelho:
			pino = D09_LED_VERMELHO;
			break;
		case R.id.btn_d06_led_amarelo:
			pino = D06_LED_AMARELO;
			break;
		case R.id.btn_d05_led_verde:
			pino = D05_LED_VERDE;
			break;
		case R.id.btn_d04_led_vermelho:
			pino = D04_LED_VERMELHO;
			break;
		default:
			break;
		}

		if (pino != null)
			alteraEstadoLED(getIndicePino(pino));

	}

	private static String GET(String url, String wildflyError,
			String monitorError) {

		// create HttpClient
		HttpClient httpclient = new DefaultHttpClient();

		try {
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			// receive response as inputStream
			InputStream inputStream = httpResponse.getEntity().getContent();
			if (inputStream == null) {
				Log.d(RestUtil.TAG_INPUT_STREAM, "InputStream is null");
				return wildflyError;
			}

			return RestUtil.convertInputStreamToString(inputStream);
		} catch (ClientProtocolException e) {
			Log.d(RestUtil.TAG_INPUT_STREAM, e.getLocalizedMessage(),
					e.getCause());
		} catch (IOException e) {
			Log.d(RestUtil.TAG_INPUT_STREAM, e.getLocalizedMessage(),
					e.getCause());
		} catch (NullPointerException e) {
			Log.d(RestUtil.TAG_INPUT_STREAM, e.getLocalizedMessage(),
					e.getCause());
			return monitorError;
		}

		return wildflyError;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		protected String doInBackground(String... urls) {
			return GET(urls[0], getString(R.string.msg_error_wildfly),
					getString(R.string.msg_error_monitor));
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			String wildflyError = getString(R.string.msg_error_wildfly);
			String monitorError = getString(R.string.msg_error_monitor);

			try {
				if (wildflyError.equals(result))
					throw new AppException(wildflyError);

				if (monitorError.equals(result))
					throw new AppException(monitorError);

				try {
					for (LED led : getLEDs(new JSONArray(result)))
						setEstadoLED(led, false);
				} catch (JSONException e) {
					LED led = getLED(new JSONObject(result));
					setEstadoLED(led, true);
				}

			} catch (JSONException e) {
				tvResponse.setText(e.getMessage());
				Log.d(TAG, e.getLocalizedMessage(), e.getCause());
			} catch (AppException e) {
				tvResponse.setText(e.getMessage());
				Log.d(TAG, e.getLocalizedMessage(), e.getCause());
			}

		}
	}

	private LED getLED(JSONObject json) throws AppException, JSONException {
		LED led = new LED();

		final String STRING_NULL = "null";

		JSONObject id = json.getJSONObject("id");
		if (id != null) {
			String jsonPin = id.getString("pin");
			if (!STRING_NULL.equals(jsonPin))
				led.setPino(Byte.valueOf(jsonPin));

		}

		String jsonEstado = json.getString("estado");
		if (!STRING_NULL.equals(jsonEstado))
			led.setEstado(EstadoLED.valueOf(jsonEstado));

		if (led.getPino() == null)
			throw new AppException(getString(R.string.msg_error));

		if (led.getEstado() == null)
			throw new AppException(getString(R.string.msg_error_arduino));

		return led;
	}

	private List<LED> getLEDs(JSONArray array) throws AppException,
			JSONException {
		List<LED> leds = new ArrayList<LED>();
		for (int i = 0; i < array.length(); i++)
			leds.add(getLED(array.getJSONObject(i)));

		return leds;
	}

	private void setEstadoLED(LED led, boolean viewMessage) {
		byte indicePino = getIndicePino(led.getPino().byteValue());

		estadosLEDs[indicePino] = led.getEstado();

		if (viewMessage)
			switch (led.getEstado()) {
			case ACESO:
				tvResponse.setText(getString(R.string.msg_led_on,
						getString(CORES_LEDS[indicePino])));
				break;
			case APAGADO:
				tvResponse.setText(getString(R.string.msg_led_off,
						getString(CORES_LEDS[indicePino])));
				break;
			default:
				break;
			}

		alteraImagemBotao(indicePino);
	}

	/*
	 * Turning On flash
	 */
	private void alteraEstadoLED(byte indicePino) {
		EstadoLED estado = null;

		switch (estadosLEDs[indicePino]) {
		case ACESO:
			estado = EstadoLED.APAGADO;
			break;
		case APAGADO:
			estado = EstadoLED.ACESO;
			break;
		default:
			break;
		}

		if (estado != null) {
			// play sound
			playSound(indicePino);

			String url = URL + "/led?tipo_pino=DIGITAL&pino="
					+ PINOS_LEDS[indicePino] + "&estado=" + estado.name();
			new HttpAsyncTask().execute(url);
		}

	}

	/*
	 * Playing sound will play button toggle sound on flash on / off
	 */
	private void playSound(byte indicePino) {
		switch (estadosLEDs[indicePino]) {
		case ACESO:
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
			break;
		case APAGADO:
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
			break;
		default:
			break;
		}

		if (mp != null) {
			mp.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.release();
				}
			});
			mp.start();
		}
	}

	/*
	 * Toggle switch button images changing image states to on / off
	 */
	private void alteraImagemBotao(byte indicePino) {
		switch (estadosLEDs[indicePino]) {
		case ACESO:
			botoesLEDs[indicePino].setImageResource(R.drawable.btn_switch_on);
			break;
		case APAGADO:
			botoesLEDs[indicePino].setImageResource(R.drawable.btn_switch_off);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "MainActivity.onDestroy()");
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash
		if (estadosLEDs != null && botoesLEDs != null)
			apagaLEDs();
		Log.v(TAG, "MainActivity.onPause()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.v(TAG, "MainActivity.onRestart()");
	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume turn on the flash
		if (isConnected && estadosLEDs != null && botoesLEDs != null)
			new HttpAsyncTask().execute(URL + "/leds");

		Log.v(TAG, "MainActivity.onResume()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.v(TAG, "MainActivity.onStart()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v(TAG, "MainActivity.onStop()");
	}
}
