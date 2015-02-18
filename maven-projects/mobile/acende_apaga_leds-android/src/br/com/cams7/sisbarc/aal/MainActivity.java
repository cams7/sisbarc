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
	private static final String URL = "http://192.168.1.7:8080/acende_apaga_leds/rest/arduino/led";

	private static final byte PINO_LED_AMARELO = 11; // Pino 11 PWM
	private static final byte PINO_LED_VERDE = 10; // Pino 10 PWM
	private static final byte PINO_LED_VERMELHO = 9; // Pino 09 PWM

	private static final byte PINOS_LEDS[] = { PINO_LED_AMARELO,
			PINO_LED_VERDE, PINO_LED_VERMELHO };
	private static final int CORES_LEDS[] = { R.string.btn_yellow,
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
		botoesLEDs[0] = (ImageButton) findViewById(R.id.btn_yellow);
		botoesLEDs[0].setOnClickListener(this);

		botoesLEDs[1] = (ImageButton) findViewById(R.id.btn_green);
		botoesLEDs[1].setOnClickListener(this);

		botoesLEDs[2] = (ImageButton) findViewById(R.id.btn_red);
		botoesLEDs[2].setOnClickListener(this);

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
		case R.id.btn_yellow:
			pino = PINO_LED_AMARELO;
			break;
		case R.id.btn_green:
			pino = PINO_LED_VERDE;
			break;
		case R.id.btn_red:
			pino = PINO_LED_VERMELHO;
			break;
		default:
			break;
		}

		if (pino != null)
			alteraEstadoLED(getIndicePino(pino));

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
			String wildflyError = getString(R.string.msg_error_wildfly);

			try {
				if (wildflyError.equals(result))
					throw new AppException(wildflyError);

				JSONObject json = new JSONObject(result);

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
					throw new AppException(
							getString(R.string.msg_error_arduino));

				byte indicePino = getIndicePino(led.getPino().byteValue());

				estadosLEDs[indicePino] = led.getEstado();

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

			String url = URL + "?tipo_pino=DIGITAL&pino="
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
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash
		if (estadosLEDs != null && botoesLEDs != null)
			apagaLEDs();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// on resume turn on the flash
		if (isConnected && estadosLEDs != null && botoesLEDs != null)
			apagaLEDs();
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
