package de.opencelldroid.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	
	private static final String TAG = "ServerRequest";
	private Context context = null;
	private AsyncTask <String, Void, String> downloadXml = null;
	private final int MAX_RESPONSE_TIME_IN_SEC = 10;
	
	// OpenCellID data
	private String apiKey = "";
	private final String SERVER_URL = "http://www.opencellid.org/";
	
	// Server test mode option
	private boolean testMode = false;
	private int mcc = 1;
	private int mnc = 1;
	
	// Possible server responses
	public final int NOT_OK = 0;
	public final int OK = 1;
	
	
	/**
	 * Establish the connection to opencellid.org
	 * 
	 * @param apiKey
	 * 		The API Key from apikey.xml
	 * @param testMode
	 * 		If true, only test requests will be send to the opencellid.org server
	 */
	public ServerRequest (String apiKey, Context context, boolean testMode) {
		this.apiKey = apiKey;
		this.context = context;
		
		if (testMode) {
			this.testMode = true;
		}
		else {
			this.testMode = false;
		}
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
	public int addCell (int mcc, int mnc, int lac, int cellId, float lat, float lon) {
		
		if (!hasInternetConnection()) {
			return NOT_OK;
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
				+ "&mnc=" + Integer.toString(this.mnc)
				+ "&mcc=" + Integer.toString(this.mcc)
				+ "&lac=" + Integer.toString(lac)
				+ "&cellid=" + Integer.toString(cellId)
				+ "&lat=" + Float.toString(lat)
				+ "&lon=" + Float.toString(lon);
		
		downloadXml = new DownloadXmlTask ().execute (url);
		
		String xml = null;
		try {
			xml = downloadXml.get (MAX_RESPONSE_TIME_IN_SEC, TimeUnit.SECONDS);
		}
		catch (CancellationException e) {
			Log.d (TAG, "Server Request was cancelled.");
			return NOT_OK;
		}
		catch (InterruptedException e) {
			Log.wtf (TAG, "Thread error while downloading XML");
		}
		catch (ExecutionException e) {
			Log.e (TAG, "Exception: " + e.getCause());
			return NOT_OK;
		}
		catch (TimeoutException e) {
			Log.e (TAG, "Time out while connecting to server.");
			return NOT_OK;
		}
		
		return NOT_OK;
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
	public Object getCell (int mcc, int mnc, int lac, int cellId) {
		
		if (!hasInternetConnection()) {
			return NOT_OK;
		}
		
		if (this.testMode) {
			this.mcc = 1;
			this.mnc = 1;
		}
		else {
			this.mcc = mcc;
			this.mnc = mnc;
		}
		
		// TODO: Implement method to get latitude and longitude
		
		return null;
	}
	
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
	public Object[] getMeasures (int mcc, int mnc, int lac, int cellId) {
		
		if (!hasInternetConnection()) {
			return null;
		}
		
		if (this.testMode) {
			this.mcc = 1;
			this.mnc = 1;
		}
		else {
			this.mcc = mcc;
			this.mnc = mnc;
		}
		
		// TODO: Implement the method
		
		return null;
	}
	
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
	 * @param fmt
	 * 		Format of the result: KML, XML or TXT. Default is KML. TXT returns a CSV file with the first line as a headline.
	 * @return
	 * 		An array of cells
	 */
	public Object[] getInArea (float[] bbox, int limit, int mcc, int mnc, String fmt) {
		
		if (!hasInternetConnection()) {
			return null;
		}
		
		if (limit <= 0 || limit > 200) {
			limit = 200;
		}
		
		if (this.testMode) {
			this.mcc = 1;
			this.mnc = 1;
		}
		else {
			this.mcc = mcc;
			this.mnc = mnc;
		}
		
		// TODO: Implement the method
		
		return null;
	}
	
	/**
	 * Submits multiple cell measurements
	 * 
	 * @param datafile
	 * 		CSV file which contains all cell measurements
	 */
	public void uploadCsv (File datafile) {
		
		// TODO: Implement the method
		
	}
	
	/**
	 * Delete a cell. Only cells submitted by OpenCellDroid (this app) can get deleted
	 * 
	 * @param id
	 * 		The id of the cell that shall get deleted. To get the id use the list method.
	 */
	public boolean deleteCell (int id) {
		
		if (!hasInternetConnection()) {
			return false;
		}
		
		// TODO: Implement the method
		
		return false;
	}
	
	/**
	 * Return all cells which have been submitted yet by OpenCellDroid (this app)
	 * 
	 * @return
	 * 		An array of cells
	 */
	public Object[] listCells () {
		
		if (!hasInternetConnection()) {
			return null;
		}
		
		// TODO: Implement the method
		
		return null;
	}
	
	/**
	 * Check if the device can connect to the Internet
	 * 
	 * @return
	 * 		true, if the device can connect to the Internet; false otherwise
	 */
	private boolean hasInternetConnection () {
		ConnectivityManager connMgr = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		else {
			Log.w (TAG, "No Internet connection available");
			return false;
		}
	}
	
	/**
	 * Cancel server request
	 */
	public void cancel () {
		downloadXml.cancel(true);
	}
	
	/**
	 * Parse XML to a DOM
	 * 
	 * @param xml
	 * 		The XML reference as String
	 * @return
	 * 		XML DOM
	 */
	private Document parseXml (final String xml) {
		Document document = null;
		
		try {
			InputStream inputStream = new ByteArrayInputStream (xml.getBytes ("UTF-8"));
			DocumentBuilder builder = DocumentBuilderFactory.newInstance ().newDocumentBuilder ();
			document = builder.parse (inputStream);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return document;
	}
	
}
