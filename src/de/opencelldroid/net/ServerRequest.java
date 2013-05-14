package de.opencelldroid.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * Communication with the opencellid.org server.
 * For more information see: http://www.opencellid.org/api
 * 
 * @author Tunnel1337
 */
public class ServerRequest {
	
	private final String TAG = "ServerRequest";
	
	// OpenCellID data
	private String apiKey = "";
	private final String SERVER_URL = "http://www.opencellid.org/";
	
	// Server connection
	private final HttpClient httpClient = new DefaultHttpClient ();
	
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
	public ServerRequest (String apiKey, boolean testMode) {
		this.apiKey = apiKey;
		
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
	 * 		OK if cell got successfully added, NOT_OK if cell could not get added
	 */
	public int addCell (int mcc, int mnc, int lac, int cellId, float lat, float lon) {
		if (this.testMode) {
			this.mcc = 1;
			this.mnc = 1;
		}
		else {
			this.mcc = mcc;
			this.mnc = mnc;
		}
		
		final String uri = SERVER_URL
				+ "measure/add?"
				+ "key=" + this.apiKey
				+ "&mnc=" + Integer.toString(this.mnc)
				+ "&mcc=" + Integer.toString(this.mcc)
				+ "&lac=" + Integer.toString(lac)
				+ "&cellid=" + Integer.toString(cellId)
				+ "&lat=" + Float.toString(lat)
				+ "&lon=" + Float.toString(lon);
		
		final String xml = getXmlContent (uri);
		
		Document document = null;
		if (xml != null) {
			document = parseXml (xml);
		}
		else {
			return NOT_OK;
		}
		
		if (document != null) {
			// TODO: Return XML values
		}
		else {
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
	 * 		An array of cells, where only the latitude and longitude is different
	 */
	public Object[] getMeasures (int mcc, int mnc, int lac, int cellId) {
		
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
	public void deleteCell (int id) {
		
		// TODO: Implement the method
		
	}
	
	/**
	 * Returns all cells which have been submitted yet by OpenCellDroid (this app)
	 * 
	 * @return
	 * 		An array of cells
	 */
	public Object[] listCells () {
		
		// TODO: Implement the method
		
		return null;
	}

	/**
	 * Save the XML response from opencellid.org in a String
	 * 
	 * @param uri
	 * 		The URI which points to the XML content
	 * @return
	 * 		String which contains XML
	 */
	private String getXmlContent (final String uri) {
		HttpResponse response = null;
		
		try {
			final HttpGet connection = new HttpGet (uri);
			response = httpClient.execute (connection);
			
			StatusLine statusLine = response.getStatusLine ();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				return response.getEntity().getContent().toString();
			}
			else {
				Log.e (TAG, "Could not connect to server");
				return null;
			}
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				response.getEntity().getContent().close();
			}
			catch (IllegalStateException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
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
