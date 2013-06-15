package de.opencelldroid.net;

import java.util.List;

import de.opencelldroid.loc.Cell;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Communication with the opencellid.org server.
 * For more information see: http://www.opencellid.org/api
 * 
 * @author Jose Martinez Gonzalez (Tunnel1337)
 */
public class ServerRequest {
	
	// Class variables
	private static final String TAG = "ServerRequest";
	private Context context = null;
	private ServerCallback callingInstance = null;
	private AsyncTask <String, Void, String> downloadXml = null;
	
	// Methods that can call DownloadXmlTask
	protected final String addCellMethod = "addCell";
	protected final String getInAreaMethod = "getInArea";
	
	// OpenCellID data
	private String apiKey = "";
	private final String SERVER_URL = "http://www.opencellid.org/";
	
	// Server test mode option
	private boolean testMode = false;
	private int mcc = 1; // 1 for test mode
	private int mnc = 1; // 1 for test mode
	
	// Possible server responses
	 public enum ResponseCode {
		 NOT_OK,
		 OK
	}
	
	
	/**
	 * Establish the connection to opencellid.org
	 * 
	 * @param apiKey
	 * 		The API Key from apikey.xml
	 * @param testMode
	 * 		If true, only test requests will be send to the opencellid.org server
	 */
	public ServerRequest(String apiKey, Context context, ServerCallback callingInstance, boolean testMode) {
		this.apiKey = apiKey;
		this.context = context;
		this.callingInstance = callingInstance;
		this.testMode = testMode;
	}
	
	/**
	 * Only used to get a reference for the callback
	 * 
	 * @param apiKey
	 * 		The API Key from apikey.xml
	 * @param testMode
	 * 		If true, only test requests will be send to the opencellid.org server
	 */
	protected ServerRequest() {
		this.testMode = true;
	}
	
	/**
	 * Submits a new cell measurement
	 * 
	 * @param mcc
	 * 		Mobile Country Code
	 * @param mnc
	 * 		Mobile Network Code
	 * @param lac
	 * 		Local Area Code
	 * @param cellId
	 * 		The cell ID
	 * @param lat
	 * 		Latitude of the cell
	 * @param lon
	 * 		Longitude of the cell
	 * @return
	 * 		OK, if cell got successfully added; NOT_OK otherwise
	 */
	public void addCell(int mcc, int mnc, int lac, int cellId, float lat, float lon) {
		if (!hasInternetConnection()) {
			Log.w(TAG, "No Internet connection available");
			return;
		}
		
		if (this.testMode) {
			this.mcc = 1;
			this.mnc = 1;
		}
		else {
			this.mcc = mcc;
			this.mnc = mnc;
		}
		
		final String url = SERVER_URL
				+ "measure/add?"
				+ "key=" + this.apiKey
				+ "&mnc=" + this.mnc
				+ "&mcc=" + this.mcc
				+ "&lac=" + lac
				+ "&cellid=" + cellId
				+ "&lat=" + lat
				+ "&lon=" + lon;
		
		Log.d(TAG, "Add cell...\n" +
				"Internet connection OK\n" +
				"Test mode: " + this.testMode + "\n" +
				"URL: " + url);
		
		downloadXml = new DownloadXmlTask().execute(url, this.addCellMethod);
		
		return;
	}
	
	/**
	 * Get a specific cell. This method can be used to get the position of a cell.
	 * 
	 * @param mcc
	 * 		Mobile Country Code
	 * @param mnc
	 * 		Mobile Network Code
	 * @param lac
	 * 		Local Area Code
	 * @param cellId
	 * 		The cell ID
	 * @return
	 * 		A single cell object.
	 */
//	public ResponseCode getCell(int mcc, int mnc, int lac, int cellId) {
//		
//		if (!hasInternetConnection()) {
//			return ResponseCode.NOT_OK;
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
//		
//		// TODO: Implement method to get latitude and longitude
//		
//		return ResponseCode.NOT_OK;
//	}
	
	/**
	 * Get all measure information from a specific cell
	 * 
	 * @param mcc
	 * 		Mobile Country Code
	 * @param mnc
	 * 		Mobile Network Code
	 * @param lac
	 * 		Local Area Code
	 * @param cellId
	 * 		The cell ID
	 * @return
	 * 		An array of the same cell with different latitude and longitude positions
	 */
//	public ResponseCode getMeasures(int mcc, int mnc, int lac, int cellId) {
//		
//		if (!hasInternetConnection()) {
//			return ResponseCode.NOT_OK;
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
//		
//		// TODO: Implement the method
//		
//		return ResponseCode.NOT_OK;
//	}
	
	/**
	 * Get a list of cells in a specified area
	 * 
	 * @param bbox
	 * 		The bounding box where you want to look for cells. Contains latmin, lonmin, latmax, lonmax - in this order!
	 * @param limit
	 * 		The maximum size of the returned list. Maximum and default is 200.
	 * @param mcc
	 * 		Restrict the result to a specific country. Chose 0 for no restriction.
	 * @param mnc
	 * 		Restrict the result to a specific operator. Chose 0 for no restriction.
	 * @return
	 * 		An array of cells
	 */
	public void getInArea(float[] bbox, int limit, int mcc, int mnc) {
		
		if (!hasInternetConnection()) {
			return;
		}
		
		if (limit < 0 || limit > 200) {
			limit = 200;
		}
		
		String theMcc = "";
		if(mcc != 0) {
			theMcc = Integer.toString(mcc);
		}

		String theMnc = "";
		if(mnc != 0) {
			theMnc = Integer.toString(mnc);
		}
		
		final String url = SERVER_URL
				+ "cell/getInArea?"
				+ "BBOX="
					+ bbox[0] + "," // latmin
					+ bbox[1] + "," // lonmin
					+ bbox[2] + "," // latmax
					+ bbox[3] // lonmax
				+ "&mnc=" + theMnc
				+ "&mcc=" + theMcc
				+ "&fmt=xml";
		
		Log.d(TAG, "Get in area...\n" +
				"Internet connection OK\n" +
				"Test mode: " + this.testMode + "\n" +
				"URL: " + url);
		
		downloadXml = new DownloadXmlTask().execute(url, this.getInAreaMethod);
		
		return;
	}
	
	/**
	 * Submits multiple cell measurements
	 * 
	 * @param csvFile
	 * 		CSV file which contains all cell measurements
	 */
//	public ResponseCode uploadCsv(File csvFile) {
//		
//		// TODO: Implement the method
//		
//		return ResponseCode.NOT_OK;
//		
//	}
	
	/**
	 * Delete a cell. Only cells submitted by OpenCellDroid (this app) can get deleted
	 * 
	 * @param id
	 * 		The id of the cell that shall get deleted. To get the id use the list method.
	 */
//	public ResponseCode deleteCell(int id) {
//		
//		if (!hasInternetConnection()) {
//			return false;
//		}
//		
//		// TODO: Implement the method
//		
//		return ResponseCode.NOT_OK;
//	}
	
	/**
	 * Return all cells which have been submitted yet by OpenCellDroid (this app)
	 * 
	 * @return
	 * 		An array of cells
	 */
//	public ResponseCode listCells() {
//		
//		if (!hasInternetConnection()) {
//			return ResponseCode.NOT_OK;
//		}
//		
//		// TODO: Implement the method
//		
//		return ResponseCode.NOT_OK;
//	}
	
	/**
	 * Check if the device can connect to the Internet
	 * 
	 * @return
	 * 		true, if the device can connect to the Internet; false otherwise
	 */
	private boolean hasInternetConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Cancels current server request
	 */
	public void cancel() {
		downloadXml.cancel(true);
	}
	
	/**
	 * Will get called when {@link DownloadXmlTask} is done to add a cell
	 * 
	 * @param originalMethod
	 * 		The method why downloadXml was called, e. g. addCell
	 */
	protected void downloadXmlAddCellCallback(boolean state) {
		if (state) {
			this.callingInstance.addCellCallback(ResponseCode.OK);
		}
		else {
			this.callingInstance.addCellCallback(ResponseCode.NOT_OK);
		}
	}
	
	/**
	 * Will get called when {@link DownloadXmlTask} is done to get cells in an area
	 * 
	 * @param originalMethod
	 * 		The method why downloadXml was called, e. g. addCell
	 */
	protected void downloadXmlGetInAreaCallback(boolean state, List<Cell> listOfCells) {
		if (state) {
			this.callingInstance.getInAreaCallback(ResponseCode.OK, listOfCells);
		}
		else {
			this.callingInstance.getInAreaCallback(ResponseCode.NOT_OK, listOfCells);
		}
	}
	
}
