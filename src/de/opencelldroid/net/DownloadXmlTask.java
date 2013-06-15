package de.opencelldroid.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.opencelldroid.loc.Cell;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Download XML from a given URL
 * 
 * @author Jose Martinez Gonzalez (Tunnel1337)
 */
public class DownloadXmlTask extends AsyncTask <String, Void, String> {
	
	// Class variables
	private static final String TAG = "DownloadXmlTask";
	private final int TIMEOUT_IN_MILLIS = 10000;
	private String originalMethod = "";
	
	// Return values
	private boolean state = false;
	private List<Cell> listOfCells = new ArrayList<Cell>();
	
	private InputStream inputStream = null;
	private InputStreamReader inputStreamReader = null;
	private BufferedReader bufferedReader = null;
	
	
	// XML download got cancelled
	@Override
	protected void onCancelled() {
		try {
			inputStream.close ();
			inputStreamReader.close ();
			bufferedReader.close ();
		}
		catch (IOException e) {
			Log.w (TAG, "Could not close stream while cancelling.");
		}
		catch (NullPointerException e) {
			Log.d (TAG, "Could not close stream while cancelling.");
		}
		finally {
			Log.d(TAG, "Cancelled server request");
		}
	}
	
	// Prepare XML download
	@Override
	protected void onPreExecute () {
		
	}
	
	// Download XML without blocking the UI
	@Override
	protected String doInBackground(String... urls) {
		try {
			// Get XML
			URL url = new URL(urls[0]);
			this.originalMethod = urls[1];
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(TIMEOUT_IN_MILLIS);
			connection.setConnectTimeout(TIMEOUT_IN_MILLIS);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();
			
			this.inputStream = connection.getInputStream();

			// Convert InputStream to String
			this.inputStreamReader = new InputStreamReader(this.inputStream);
			this.bufferedReader = new BufferedReader(this.inputStreamReader);
			
			StringBuilder stringBuilder = new StringBuilder();
			String nextLine = this.bufferedReader.readLine();
			while (nextLine != null) {
				stringBuilder.append (nextLine);
				nextLine = this.bufferedReader.readLine();
			}
			
			String xml = stringBuilder.toString();
			Log.d (TAG, "Server responded: " + xml);
			
			// Parse XML
			XmlParser xmlParser = new XmlParser();
			ServerRequest serverRequest = new ServerRequest();
			if (originalMethod.equals(serverRequest.addCellMethod)) {
				this.state = xmlParser.parseAddCellRequest(xml);
			}
			else if (originalMethod.equals(serverRequest.getInAreaMethod)) {
				this.state = true;
				this.listOfCells = xmlParser.parseGetInAreaRequest(xml);
			}
			else {
				Log.e(TAG, "Didn't parse XML because there was no original method set in ServerRequest");
			}
			
			return xml;
		}
		catch (MalformedURLException e) {
			Log.e(TAG, "Given URL could not get retrieved.");
		}
		catch (IOException e) {
			Log.e(TAG, "Could not connect to server");
		}
		catch (IllegalArgumentException e) {
			Log.wtf(TAG, "Timeout value set wrong. Timeout was set to: " + TIMEOUT_IN_MILLIS);
		}
		finally {
			try {
				inputStream.close();
				inputStreamReader.close();
				bufferedReader.close();
			}
			catch (IOException e) {
				Log.d(TAG, "Could not close stream.");
			}
			catch (NullPointerException e) {
				Log.d(TAG, "No stream to server.");
			}
		}
		
		return null;
	}
	
	// When XML is fully loaded
	@Override
	protected void onPostExecute(String originalMethod) {
		ServerRequest serverRequest = new ServerRequest();
		if (this.originalMethod.equals(serverRequest.addCellMethod)) {
			Log.d(TAG, "Callback to " + serverRequest.addCellMethod+ " method with status: " + this.state);
			serverRequest.downloadXmlAddCellCallback(this.state);
		}
		else if (this.originalMethod.equals(serverRequest.getInAreaMethod)) {
			Log.d(TAG, "Callback to " + serverRequest.getInAreaMethod + " method with status: " + this.state);
			serverRequest.downloadXmlGetInAreaCallback(this.state, this.listOfCells);
		}
	}
	
}
