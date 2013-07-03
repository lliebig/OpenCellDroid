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
package de.opencelldroid.loc;

import java.security.ProviderException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
	public final static int MAX_GPS_FIX_AGE = 120000; // 2 min
	private final static int GPS_TIME_OUT = 45000; // 45 sek
	
	private Timer mTimer;
	private LocationManager mLocMan;
	private GpsListener mGpsListener;
	private LocationHelperInterface mCaller;
	private Context mContext;
	
	@SuppressWarnings("serial")
	public static class NoServiceException extends Exception{}
	
	/**
	 * Creates an instance of LocationHelper
	 * @param context {@link Context} for using the location service
	 */
	public LocationHelper(Context context, LocationHelperInterface caller){
		mGpsListener = new GpsListener();
		mLocMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mCaller = caller;
		mContext = context;
	}
	
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
		if (telMan == null) throw new NoServiceException();
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
		if (cell == null) throw new NoServiceException();
		
		if (telMan.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM){
			Log.d(TAG, "Is GSM cell");
			GsmCellLocation gsmCell = (GsmCellLocation) cell;
			
			int lac = gsmCell.getLac();
			int cid = gsmCell.getCid();

			if( (lac == -1) || (cid == -1) ) throw new NoServiceException();
			else return new Cell(mcc, mnc, lac, cid);
		}
		else{
			//TODO: support CDMA cells?
			Log.e(TAG, "No GSM network avialable: " + telMan.getPhoneType());
			throw new NetworkErrorException();
		}
	}
	
	/**
	 * Requests a single GPS location update. 
	 * @throws ProviderException in case the GPS provider is not available
	 * @throws Exception in case the location service ins not available
	 * @returns locMan a reference to the {@link LocationManager}
	 */
	public void requestGpsLocation() throws ProviderException, Exception{
		if(mLocMan == null) throw new Exception("Location service not available!");
		
		//FIXME: add busy flag
		Location lastKnownLocation = getLastKnownGpsLoc2Min();
		if(lastKnownLocation != null){
			mCaller.onGpsFix(lastKnownLocation);
			return;
		}
		
		if (mLocMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// request the current GPS pos
			mLocMan.requestSingleUpdate(LocationManager.GPS_PROVIDER, mGpsListener, mContext.getMainLooper() );
			
			mTimer = new Timer();
			Date death = new Date( System.currentTimeMillis() + GPS_TIME_OUT );
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					mLocMan.removeUpdates(mGpsListener);
					mCaller.onGpsTimeOut();
				}
			}, 
			death);

		} else {
			throw new ProviderException("GPS provider not available");
		}
	}
	
	public void cancelOngoingGpsRequests(){
		mLocMan.removeUpdates(mGpsListener);
	}
	
	/**
	 * Returns the last known GPS location if it's not older than 5 minutes, otherwise it returns null
	 * @param context {@link Context} for using the location service
	 * @return last known GPS location if it's not older than 5 minutes, otherwise null
	 */
	private Location getLastKnownGpsLoc2Min(){
		
		if (mLocMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			
			Location lastKnownLoc = mLocMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(lastKnownLoc == null) return null;
			
			if(gpsFixIsUpToDate(lastKnownLoc)) return lastKnownLoc;
			else return null;
			
		} else {
			throw new ProviderException("GPS provider not available");
		}
	}
	
	/**
	 * Determines whether a Location is not older than 2 minutes
	 * @param location {@link Location}  to analyze
	 * @return true if up do date, false if outdated
	 */
	public boolean gpsFixIsUpToDate(Location location){
		
		long now = System.currentTimeMillis();
		
		if((now - location.getTime()) < MAX_GPS_FIX_AGE) return true;
		else return false;
	}
	
	// inner classes
	class GpsListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			mTimer.cancel();
			Log.d(TAG, location.toString());
			mCaller.onGpsFix(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
	}
}
