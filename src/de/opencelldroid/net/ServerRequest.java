/*
 * Copyright 2013 Leo Liebig (lliebig, info@leoliebig.de), Dmitrij Ignatjew, Jose Martinez Gonzalez (Tunnel1337)
 * This file is part of OpenCellDroid.
 * 
 * OpenCellDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenCellDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenCellDroid.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Communication with the opencellid.org server. For more information see:
 * http://www.opencellid.org/api
 * 
 * @author Jose Martinez Gonzalez (Tunnel1337), info@leoliebig.de
 */
public class ServerRequest {

	// Class variables
	private static final String TAG = "ServerRequest";
	
	// Methods that can call DownloadXmlTask
	protected final static String ADD_CELL = "addCell";
	protected final static String GET_IN_AREA = "getInArea";
	private final static boolean DEBUG_MODE = false;
	private final static String SERVER_URL = "http://www.opencellid.org/";
	
	private Context context = null;
	private ServerCallback callingInstance = null;
	private AsyncTask<String, Void, String> requestTask = null;

	// OpenCellID data
	private String apiKey = "";

	// Server test mode option
	private int mcc = 1; // 1 for test mode
	private int mnc = 1; // 1 for test mode

	// Possible server responses
	public enum ResponseCode {
		NOT_OK, OK
	}

	/**
	 * Establish the connection to opencellid.org
	 * 
	 * @param apiKey
	 *            The API Key from apikey.xml
	 * @param DEBUG_MODE
	 *            If true, only test requests will be send to the opencellid.org
	 *            server
	 */
	public ServerRequest(String apiKey, Context context,
			ServerCallback callingInstance) {
		this.apiKey = apiKey;
		this.context = context;
		this.callingInstance = callingInstance;
	}

	/**
	 * Submits a new cell measurement
	 * 
	 * @param cell
	 * 		A single cell object
	 * @throws NetworkErrorException
	 * 		if no Internet connection is available
	 */
	public void addCell(Cell cell) throws NetworkErrorException {
		if (!hasInternetConnection()) {
			throw new NetworkErrorException("No internet connection available");
		}

		if (DEBUG_MODE) {
			this.mcc = 1;
			this.mnc = 1;
		} else {
			this.mcc = cell.getMcc();
			this.mnc = cell.getMnc();
		}
		
		final String url = SERVER_URL + "measure/add?" + "key=" + this.apiKey
				+ "&mnc=" + this.mnc + "&mcc=" + this.mcc + "&lac=" + cell.getLac()
				+ "&cellid=" + cell.getCellId() + "&lat=" + cell.getLat() + "&lon=" + cell.getLon();

		Log.d(TAG, "Add cell...\n" + "Internet connection OK\n" + "Test mode: "
				+ DEBUG_MODE);
		
		if (requestTask.getStatus() != AsyncTask.Status.PENDING || requestTask.getStatus() != AsyncTask.Status.RUNNING) {
			requestTask = new RequestTask().execute(url, ADD_CELL);
		}
		else {
			// nothing
		}
	}

	/**
	 * Get a specific cell. This method can be used to get the position of a
	 * cell.
	 * 
	 * @param mcc
	 *            Mobile Country Code
	 * @param mnc
	 *            Mobile Network Code
	 * @param lac
	 *            Local Area Code
	 * @param cellId
	 *            The cell ID
	 */
//	 public void getCell(int mcc, int mnc, int lac, int cellId) {
//		if (!hasInternetConnection()) {
//			return;
//		}
//		
//		if (this.testMode) {
//			this.mcc = 1;
//			this.mnc = 1;
//		}
//		else {
//			this.mcc = mcc;
//			this.mnc = mnc;
//		}
//	 }

	/**
	 * Get all measure information from a specific cell
	 * 
	 * @param mcc
	 *            Mobile Country Code
	 * @param mnc
	 *            Mobile Network Code
	 * @param lac
	 *            Local Area Code
	 * @param cellId
	 *            The cell ID
	 * @return An array of the same cell with different latitude and longitude
	 *         positions
	 */
//	 public void getMeasures(int mcc, int mnc, int lac, int cellId) {
//		if (!hasInternetConnection()) {
//			return;
//		}
//		
//		if (this.testMode) {
//			this.mcc = 1;
//			this.mnc = 1;
//		}
//		else {
//			this.mcc = mcc;
//			this.mnc = mnc;
//		}
//	 }

	/**
	 * Get a list of cells in a specified area
	 * 
	 * @param bbox
	 *            The bounding box where you want to look for cells. Contains
	 *            lonmin, latmin, lonmax, latmax - in this order!
	 * @param limit
	 *            The maximum size of the returned list. Maximum and default is
	 *            200.
	 * @param mcc
	 *            Restrict the result to a specific country. Chose 0 for no
	 *            restriction.
	 * @param mnc
	 *            Restrict the result to a specific operator. Chose 0 for no
	 *            restriction.
	 * @throws NetworkErrorException
	 * 		if no internet connection available
	 */
	public void getInArea(double[] bbox, int limit) throws NetworkErrorException {
		
		//cancel previous request
		cancel();

		if (!hasInternetConnection()) {
			throw new NetworkErrorException("No internet connection available");
		}

		if (limit < 0 || limit > 200) {
			limit = 200;
		}

		final String url = SERVER_URL + "cell/getInArea?" + "BBOX=" 
				+ bbox[0] + "," // lonmin
				+ bbox[1] + "," // latmin
				+ bbox[2] + "," // lonmax
				+ bbox[3] // latmax
				+ "&limit=" + limit
				+ "&fmt=xml";

		Log.d(TAG, "Get in area...\n" + "Internet connection OK\n"
				+ "Test mode: " + DEBUG_MODE + "\n" + "URL: " + url);
		
		if (requestTask.getStatus() != AsyncTask.Status.PENDING || requestTask.getStatus() != AsyncTask.Status.RUNNING) {
			requestTask = new RequestTask().execute(url, GET_IN_AREA);
		}
		else {
			// nothing
		}
	}

	/**
	 * Submits multiple cell measurements
	 * 
	 * @param csvFile
	 *            CSV file which contains all cell measurements
	 */
//	public void uploadCsv(File csvFile) {
//		
//	}

	/**
	 * Delete a cell. Only cells submitted by OpenCellDroid (this app) can get
	 * deleted
	 * 
	 * @param id
	 *            The id of the cell that shall get deleted. To get the id use
	 *            the list method.
	 */
//	 public void deleteCell(int id) {
//		if (!hasInternetConnection()) {
//			return;
//		}
//	 }

	/**
	 * Return all cells which have been submitted yet by OpenCellDroid (this
	 * app)
	 */
//	 public void listCells() {
//		if (!hasInternetConnection()) {
//			return;
//		}
//	 }

	/**
	 * Check if the device can connect to the Internet
	 * 
	 * @return true, if the device can connect to the Internet; false otherwise
	 */
	private boolean hasInternetConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Cancels current server request
	 */
	public void cancel() {
		if(requestTask != null) {
			requestTask.cancel(true);
		}
	}

	/**
	 * Will get called when {@link RequestTask} is done to add a cell
	 * 
	 * @param requestType
	 *            The method why downloadXml was called, e. g. addCell
	 */
	protected void addCellRequestDone(boolean state) {
		if (state) {
			callingInstance.addCellCallback(ResponseCode.OK);
		} else {
			callingInstance.addCellCallback(ResponseCode.NOT_OK);
		}
	}

	/**
	 * Will get called when {@link RequestTask} is done to get cells in an
	 * area
	 * 
	 * @param requestType
	 *            The method why downloadXml was called, e. g. addCell
	 */
	protected void getInAreaRequestDone(boolean state,
			List<Cell> listOfCells) {
		if (state) {
			this.callingInstance
					.getInAreaCallback(ResponseCode.OK, listOfCells);
		} else {
			this.callingInstance.getInAreaCallback(ResponseCode.NOT_OK,
					listOfCells);
		}
	}
	
	class RequestTask extends AsyncTask<String, Void, String> {
		
		// Class variables
		private static final String TAG = "DownloadXmlTask";
		private final int TIMEOUT_IN_MILLIS = 10000;
		private String requestType = "";
		
		// Return values
		private boolean state = false;
		private List<Cell> listOfCells = new ArrayList<Cell>();
		
		private InputStream inputStream = null;
		private InputStreamReader inputStreamReader = null;
		private BufferedReader bufferedReader = null;
		
		// request download got cancelled
		@Override
		protected void onCancelled() {
			try {
				inputStream.close();
				inputStreamReader.close();
				bufferedReader.close();
			} catch (IOException e) {
				Log.w(TAG, "Could not close stream while cancelling.");
			} catch (NullPointerException e) {
				Log.d(TAG, "Could not close stream while cancelling.");
			} finally {
				Log.d(TAG, "Cancelled server request");
			}
		}
		
		@Override
		protected String doInBackground(String... urls) {
			try {
				// Get XML
				URL url = new URL(urls[0]);
				this.requestType = urls[1];
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
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
					stringBuilder.append(nextLine);
					nextLine = this.bufferedReader.readLine();
				}

				String xml = stringBuilder.toString();
				Log.d(TAG, "Server responded: " + xml);

				// Parse XML
				XmlParser xmlParser = new XmlParser();
				if (this.requestType.equals(ADD_CELL)) {
					this.state = xmlParser.parseAddCellRequest(xml);
				} else if (this.requestType.equals(GET_IN_AREA)) {
					this.state = true;
					this.listOfCells = xmlParser.parseGetInAreaRequest(xml);
				} else {
					Log.e(TAG,
							"Didn't parse XML because there was no original method set in ServerRequest");
				}

				return xml;
			} catch (MalformedURLException e) {
				Log.e(TAG, "Given URL could not get retrieved.");
			} catch (IOException e) {
				Log.e(TAG, "Could not connect to server");
			} catch (IllegalArgumentException e) {
				Log.wtf(TAG, "Timeout value set wrong. Timeout was set to: "
						+ TIMEOUT_IN_MILLIS);
			} finally {
				try {
					inputStream.close();
					inputStreamReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					Log.d(TAG, "Could not close stream.");
				} catch (NullPointerException e) {
					Log.d(TAG, "No stream to server.");
				}
			}

			return null;
		}

		// When XML is fully loaded
		@Override
		protected void onPostExecute(String xml) {
			if (this.requestType.equals(ADD_CELL)) {
				Log.d(TAG, "Callback to " + ADD_CELL
						+ " method with status: " + this.state);
				addCellRequestDone(this.state);
			} else if (this.requestType.equals(GET_IN_AREA)) {
				Log.d(TAG, "Callback to " + GET_IN_AREA
						+ " method with status: " + this.state);
				getInAreaRequestDone(this.state, this.listOfCells);
			}
		}

	}

}
