package de.opencelldroid.net;

import java.io.File;

/**
 * A single cell object.
 * 
 * Communication with the opencellid.org server.
 * For more information see: http://www.opencellid.org/api
 * 
 * @author Tunnel1337
 */
public class Cell {
	
	private final String API_KEY = ""; // TODO: Read the API key from apikey.xml
	
	private final String URL = "http://www.opencellid.org/";
	
	private int mcc = 0;
	private int mnc = 0;
	private int lac = 0;
	private int cellId = 0;
	private float lat = 0.0f;
	private float lon = 0.0f;
	
	
	/**
	 * Constructor
	 */
	public Cell () {
		// TODO: Get the reference to the location object here
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
	 */
	public void add (int mcc, int mnc, int lac, int cellId, float lat, float lon) {
		// TODO: Implement the method
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
	public Cell get (int mcc, int mnc, int lac, int cellId) {
		Cell cell = new Cell ();
		cell.mcc = mcc;
		cell.mnc = mnc;
		cell.lac = lac;
		cell.cellId = cellId;
		// TODO: Implement method to get latitude and longitude
//		cell.lat = ;
//		cell.lon = ;
		return cell;
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
	public Cell[] getMeasures (int mcc, int mnc, int lac, int cellId) {
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
	public Cell[] getInArea (float[] bbox, int limit, int mcc, int mnc, String fmt) {
		if (limit <= 0 || limit > 200) {
			limit = 200;
		}
		
		// TODO: Implement the method
		
		Cell[] cells = null;
		return cells;
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
	public void delete (int id) {
		
	}
	
	/**
	 * Returns all cells that have been submitted yet by OpenCellDroid (this app)
	 * 
	 * @return
	 * 		An array of cells
	 */
	public Cell[] list () {
		// TODO: Implement the method
		Cell[] cells = null;
		return cells;
	}
	
	/**
	 * Get the Mobile Country Code.
	 * 
	 * @return
	 * 		Mobile Country Code
	 */
	public int getMcc () {
		return this.mcc;
	}
	
	/**
	 * Get the Mobile Network Code.
	 * 
	 * @return
	 * 		Mobile Network Code
	 */
	public int getMnc () {
		return this.mnc;
	}
	
	/**
	 * Get the Local Area Code
	 * 
	 * @return
	 * 		Local Area Code
	 */
	public int getLac () {
		return this.lac;
	}
	
	/**
	 * Get the cell ID
	 * 
	 * @return
	 * 		Cell ID
	 */
	public int getCellId () {
		return this.cellId;
	}
	
	/**
	 * Get the latitude of this cell
	 * 
	 * @return
	 * 	Latitude
	 */
	public float getLat () {
		return this.lat;
	}
	
	/**
	 * Get the longitude of this cell
	 * 
	 * @return
	 * 		Longitude
	 */
	public float getLon () {
		return this.lon;
	}
	
}
