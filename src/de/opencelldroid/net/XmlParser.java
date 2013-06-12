package de.opencelldroid.net;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import de.opencelldroid.loc.Cell;

import android.util.Log;


/**
 * Parses XML responses from opencellid.org
 * 
 * @author info@leoliebig.de, Jose Martinez Gonzalez (Tunnel1337)
 */
public class XmlParser {
	
	private static final String TAG = "XmlParser";
	
	private XmlPullParserFactory xmlPullParserFactory = null;
	
	
	// Constructor
	public XmlParser() {
		try {
			this.xmlPullParserFactory = XmlPullParserFactory.newInstance();
		}
		catch (XmlPullParserException e){
			Log.e(TAG, "Could not create new instance of XmlPullParserFactory");
		}
	}
	
	/**
	 * Parses the XML response from the addCell method
	 * 
	 * @param xml
	 * 		The XML you want to parse
	 * @return
	 * 		true, if the cell was added to opencellid.org, false otherwise
	 */
	public boolean parseAddCellRequest(String xml) {
		// Response strings
		String state = null;
		String errorCode = null;
		String errorInfo = null;
		
		// Parse XML
		try {
			XmlPullParser xmlPullParser = this.xmlPullParserFactory.newPullParser();
			xmlPullParser.setInput(new StringReader (xml));
			
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					Log.d(TAG, "Start to read XML");
					break;
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					Log.d(TAG, "Tag found: " + xmlPullParser.getName());
					
					if (tag.equals("rsp")) {
						state = xmlPullParser.getAttributeValue(null, "stat");
					}
					else if (tag.equals("err")) {
						errorInfo = xmlPullParser.getAttributeValue(null, "info");
						errorCode = xmlPullParser.getAttributeValue(null, "code");
					}
					
					break;
				case XmlPullParser.TEXT:
					Log.d(TAG, "Text found: " + xmlPullParser.getText());
					break;
				case XmlPullParser.END_TAG:
					Log.d(TAG, "Tag closed: " + xmlPullParser.getName());
					break;
				default:
					Log.w(TAG, "Not handled XML passage");
					break;
				}
				eventType = xmlPullParser.next();
			}
			Log.d(TAG, "XML end");
		}
		catch (XmlPullParserException e){
			Log.e(TAG, "XML parsing gone wrong");
		}
		catch (IOException e) {
			Log.e(TAG, "XML reading gone wrong");
		}
		
		// Handle result and return
		if(state.equals("fail")) {
			Log.e(TAG, "Cell got not added to opencellid.org!\n" +
					"Error code " + errorCode + ": " + errorInfo);
			return false;
		}
		else if (state.equals("ok")){
			Log.d(TAG, "Cell got successfully added to opencellid.org!");
			return true;
		}
		else {
			Log.e(TAG, "Cell got not added to opencellid.org!\n" +
					"Neither ok or fail response from opencellid.org");
			return false;
		}
	}
	
	/**
	 * Parses the XML response from the getInArea method
	 * 
	 * @param xml
	 * 		The XML you want to parse
	 * @return
	 * 		A list of Cell objects or null, if an error occurred or the response was empty
	 */
	public List<Cell> parseGetInAreaRequest(String xml) {
		List<Cell> listOfCells = new ArrayList<Cell>();
		
		// Response strings
		String state = null;
		String errorCode = null;
		String errorInfo = null;
		
		// Parse XML
		try {
			XmlPullParser xmlPullParser = this.xmlPullParserFactory.newPullParser();
			xmlPullParser.setInput(new StringReader (xml));
			
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					Log.d(TAG, "Start to read XML");
					break;
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					Log.d(TAG, "Tag found: " + xmlPullParser.getName());
					
					if (tag.equals("rsp")) {
						state = xmlPullParser.getAttributeValue(null, "stat");
					}
					else if (tag.equals("err")) {
						errorInfo = xmlPullParser.getAttributeValue(null, "info");
						errorCode = xmlPullParser.getAttributeValue(null, "code");
					}
					else if (tag.equals("cell")) {
						int mcc = Integer.parseInt(xmlPullParser.getAttributeValue(null, "mcc"));
						int mnc = Integer.parseInt(xmlPullParser.getAttributeValue(null, "mnc"));
						int lac = Integer.parseInt(xmlPullParser.getAttributeValue(null, "lac"));
						int cellId = Integer.parseInt(xmlPullParser.getAttributeValue(null, "cellId"));
						float lat = Float.parseFloat(xmlPullParser.getAttributeValue(null, "lat"));
						float lon = Float.parseFloat(xmlPullParser.getAttributeValue(null, "lon"));
						Cell cell = new Cell(mcc, mnc, lac, cellId, lat, lon);
						listOfCells.add(cell);
					}
					
					break;
				case XmlPullParser.TEXT:
					Log.d(TAG, "Text found: " + xmlPullParser.getText());
					break;
				case XmlPullParser.END_TAG:
					Log.d(TAG, "Tag closed: " + xmlPullParser.getName());
					break;
				default:
					Log.w(TAG, "Not handled XML passage");
					break;
				}
				eventType = xmlPullParser.next();
			}
			Log.d(TAG, "XML end");
		}
		catch (XmlPullParserException e){
			Log.e(TAG, "XML parsing gone wrong");
		}
		catch (IOException e) {
			Log.e(TAG, "XML reading gone wrong");
		}
		
		// Handle result and return
		if(state.equals("fail")) {
			Log.e(TAG, "GetInArea request failed!\n" +
					"Error code " + errorCode + ": " + errorInfo);
			return null;
		}
		else if (state.equals("ok") && listOfCells.isEmpty()){
			Log.d(TAG, "GetInArea request was successful but output was empty!");
			return null;
		}
		else if (state.equals("ok")){
			Log.d(TAG, "GetInArea request was successful!");
			return listOfCells;
		}
		else {
			Log.e(TAG, "GetInArea request failed!\n" +
					"Neither ok or fail response from opencellid.org");
			return null;
		}
	}
	
}
