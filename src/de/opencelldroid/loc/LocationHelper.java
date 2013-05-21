package de.opencelldroid.loc;

import java.security.ProviderException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 * Helper class for determining the current GPS location and information about 
 * the mobile cell
 * @author info@leoliebig.de
 */
public class LocationHelper {

	private final static String TAG = "LocationHelper";
	
	@SuppressWarnings("serial")
	static class NoServiceException extends Exception{}
	
	/**
	 * Returns the current {@link Cell}
	 * @param context {@link Context} for using the telephony service
	 * @return Returns the current {@link Cell} of the device or null if no cell information is available.
	 * @throws NoServiceException in case there is no mobile network available
	 * @throws NetworkErrorException in case the available network is not GSM
	 */
	public static Cell getCurrentCell(final Context context) throws NoServiceException, NetworkErrorException{

		//get TelephonyManager and check whether the device is connected with a mobile network
		TelephonyManager telMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telMan == null) return null; 
		if (telMan.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN){
			Log.d(TAG, "No service!");
			throw new NoServiceException();
		}
		
		//get mobile country code
		int mcc;
		int mnc;
		String mccmnc = telMan.getNetworkOperator();
		
		//valid length?
		if(mccmnc.length() >= 3){
			try {
				// necessary because CDMA networks sometimes return non-numeric values
				mcc = Integer.parseInt(telMan.getNetworkOperator().substring(0, 3));
				mnc = Integer.parseInt(telMan.getNetworkOperator().substring(3));
			} catch (Exception e) {
				Log.e(TAG, "Invalid MCC/MNC: " + telMan.getNetworkOperator());
				throw new NetworkErrorException();
			}
		}
		else{
			Log.d(TAG, "No service!");
			throw new NoServiceException();
		}
		
		CellLocation cell = telMan.getCellLocation();
		if (cell == null) return null;
		
		if (telMan.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM){
			Log.d(TAG, "Is GSM cell");
			GsmCellLocation gsmCell = (GsmCellLocation) cell;
			
			int lac = gsmCell.getLac();
			int cid = gsmCell.getCid();

			if( (lac == -1) || (cid == -1) ) return null;
			else return new Cell(mcc, mnc, lac, cid);
		}
		else{
			Log.e(TAG, "No GSM network avialable: " + telMan.getPhoneType());
			throw new NetworkErrorException();
		}
	}
	
	/**
	 * Requests a single GPS location update. The passed {@link LocationListener} will receive
	 * the callback once the location update is done.
	 * @param context {@link Context} for using the location service
	 * @param gpsLocListener the {@link LocationListener} that is called on location changes
	 * @throws ProviderException in case the GPS provider is not available
	 * @throws Exception in case the location service ins not available
	 * @returns locMan a reference to the {@link LocationManager}
	 */
	public static LocationManager requestGpsLocation(final Context context, final LocationListener gpsLocListener) throws ProviderException, Exception{
		LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locMan == null) throw new Exception("Location service not available!");
		
		if (locMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			
			// request the current GPS pos
			locMan.requestSingleUpdate(LocationManager.GPS_PROVIDER, gpsLocListener, context.getMainLooper());

		} else {
			throw new ProviderException("GPS provider not available");
		}
		
		return locMan;
	}
	
}
