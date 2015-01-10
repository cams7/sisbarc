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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.com.cams7.sisbarc.aal.vo.Led;
import br.com.cams7.util.RestUtil;

/**
 * @author cesar
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "MainActivity";
	private static final String URL = "http://192.168.0.150:8080/acende_apaga_leds/rest/arduino/led";

	private TextView tvIsConnected;
	private TextView tvResponse;

	private Button btnRed;
	private Button btnGreen;
	private Button btnYellow;

	private Led led;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "Activity State: onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.acende_apaga_leds);

		tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
		tvResponse = (TextView) findViewById(R.id.tvResponse);

		// Obtain handles to UI objects
		btnRed = (Button) findViewById(R.id.btn_red);
		btnGreen = (Button) findViewById(R.id.btn_green);
		btnYellow = (Button) findViewById(R.id.btn_yellow);

		// add click listener to Button "POST"
		btnRed.setOnClickListener(this);
		btnGreen.setOnClickListener(this);
		btnYellow.setOnClickListener(this);

		// check if you are connected or not
		if (RestUtil.isConnected(this)) {
			tvIsConnected.setBackgroundColor(0xFF00CC00);
			tvIsConnected.setText(R.string.msg_connected);
		} else {
			tvIsConnected.setText(R.string.msg_not_connected);
		}

		led = new Led();

	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_red:
			// call AsynTask to perform network operation on separate thread
			new HttpAsyncTask().execute(URL + "/VERMELHA");

			Toast.makeText(getBaseContext(),
					getString(R.string.msg_btn_red_clicked), Toast.LENGTH_LONG)
					.show();

			break;

		case R.id.btn_green:
			new HttpAsyncTask().execute(URL + "/VERDE");
			Toast.makeText(getBaseContext(),
					getString(R.string.msg_btn_green_clicked),
					Toast.LENGTH_LONG).show();

			break;
		case R.id.btn_yellow:
			new HttpAsyncTask().execute(URL + "/AMARELA");
			Toast.makeText(getBaseContext(),
					getString(R.string.msg_btn_yellow_clicked),
					Toast.LENGTH_LONG).show();

			break;

		default:
			break;
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
			Toast.makeText(getBaseContext(), getString(R.string.msg_received),
					Toast.LENGTH_LONG).show();

			String errorMsg = getString(R.string.msg_error);

			tvResponse.setVisibility(View.VISIBLE);
			if (!errorMsg.equals(result)) {
				try {
					JSONObject json = new JSONObject(result);

					led.setCor(json.getString("cor"));
					led.setAcesa(json.getBoolean("acesa"));

					if (led.getAcesa() != null)
						tvResponse.setText(led.getAcesa() ? R.string.msg_led_on
								: R.string.msg_led_off);
					else
						tvResponse.setText(R.string.msg_error);

				} catch (JSONException e) {
					Log.d(TAG, e.getLocalizedMessage(), e.getCause());
				}
			} else
				tvResponse.setText(errorMsg);

		}
	}
}
