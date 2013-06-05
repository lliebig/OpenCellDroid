package de.opencelldroid.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Download XML from a given URL
 * 
 * @author Jose Martinez Gonzalez (Tunnel1337)
 */
public class DownloadXmlTask extends AsyncTask <String, Void, String> {

	private static final String TAG = "DownloadXmlTask";
	
	private InputStream inputStream = null;
	private InputStreamReader inputStreamReader = null;
	private BufferedReader bufferedReader = null;
	
	
	// XML download got cancelled
	@Override
	protected void onCancelled () {
		try {
			inputStream.close ();
			inputStreamReader.close ();
			bufferedReader.close ();
		}
		catch (IOException e) {
			Log.w (TAG, "Could not close stream while cancelling.");
		}
		finally {
			Log.d (TAG, "Cancelled server request");
		}
	}
	
	// Prepare XML download
	@Override
	protected void onPreExecute () {
		
	}
	
	// Download XML without blocking the UI
	@Override
	protected String doInBackground (String... urls) {
		try {
			URL url = new URL (urls[0]);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod ("GET");
			connection.setDoInput (true);
			connection.connect();
			
			inputStream = connection.getInputStream();

			// Convert InputStream to String
			inputStreamReader = new InputStreamReader (inputStream);
			bufferedReader = new BufferedReader (inputStreamReader);
			
			StringBuilder stringBuilder = new StringBuilder ();
			String nextLine = bufferedReader.readLine ();
			while (nextLine != null) {
				stringBuilder.append (nextLine);
				nextLine = bufferedReader.readLine ();
			}
			
			String xml = stringBuilder.toString();
			Log.d (TAG, "Server responded: " + xml);
			
			return xml;
		}
		catch (MalformedURLException e) {
			Log.e (TAG, "Given URL could not get retrieved.");
		}
		catch (IOException e) {
			Log.e (TAG, "Could not connect to server");
		}
		finally {
			try {
				inputStream.close ();
				inputStreamReader.close ();
				bufferedReader.close ();
			}
			catch (IOException e) {
				Log.w (TAG, "Could not close stream.");
			}
		}
		
		return null;
	}
	
	// When XML is fully loaded
	@Override
	protected void onPostExecute (String result) {
		
	}
	
}
