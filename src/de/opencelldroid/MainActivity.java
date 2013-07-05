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
package de.opencelldroid;

import java.security.ProviderException;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.opencelldroid.loc.Cell;
import de.opencelldroid.loc.LocationHelper;
import de.opencelldroid.loc.LocationHelper.NoServiceException;
import de.opencelldroid.loc.LocationHelperInterface;
import de.opencelldroid.net.ServerCallback;
import de.opencelldroid.net.ServerRequest;
import de.opencelldroid.net.ServerRequest.ResponseCode;

/**
 * Implements the menu of OpenCellDroid
 * TODO: resourcify strings
 * @author info@leoliebig.de
 */
public class MainActivity extends Activity implements OnClickListener,
		ServerCallback, LocationHelperInterface {

	private static final String TAG = "OpenCellDroid";

	private ServerRequest mServerRequest;

	private Button mBtnAddCell;
	private Button mBtnShowInArea;
	private Button mBtnTryAgain;
	private TextView mTextViewStatus;

	private LocationHelper mLocHelper;
	private Location mCurrentLocation;
	private LinearLayout mProgressLayout;
	
	private static final boolean DEBUG_MODE = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//hide the actionbar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
		
		setContentView(R.layout.activity_main);
		
		mBtnAddCell = (Button) findViewById(R.id.btn_main_submit_cell);
		mBtnShowInArea = (Button) findViewById(R.id.btn_main_show_cells);
		mBtnTryAgain = (Button) findViewById(R.id.btn_try_again);
		mTextViewStatus = (TextView) findViewById(R.id.textview_status);
		
		if(!DEBUG_MODE) mBtnAddCell.setEnabled(false);
		if(!DEBUG_MODE) mBtnShowInArea.setEnabled(false);
		
		mLocHelper = new LocationHelper(this, this);
		mServerRequest = new ServerRequest(
				getString(R.string.opencellid_apikey), MainActivity.this, this);

		mProgressLayout = (LinearLayout) findViewById(R.id.layout_progress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume called");
		if(!DEBUG_MODE && (mCurrentLocation == null || !mLocHelper.gpsFixIsUpToDate(mCurrentLocation)) ) getGpsFix();
		else{
			mBtnAddCell.setEnabled(true);
			mBtnShowInArea.setEnabled(true);
		}
	}

	/**
	 * Convenient method for requesting a GPS location from the
	 * {@link LocationHelper} and handling the corresponding exceptions
	 */
	private void getGpsFix() {
		try {
			showStatus("Getting GPS position...");
			mBtnTryAgain.setVisibility(View.GONE);
			mLocHelper.requestGpsLocation();
		} catch (ProviderException e) {
			removeStatus(false);
			mBtnTryAgain.setVisibility(View.VISIBLE);
			Toast.makeText(this,
					"GPS not available, please check your system settings!",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
			removeStatus(false);
			mBtnTryAgain.setVisibility(View.VISIBLE);
			// LocMan not available, this should never happen...
			Log.e(TAG, "LocationManager not available");
			Toast.makeText(this, "Something went wrong, please try again!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		mLocHelper.cancelOngoingGpsRequests();
		removeStatus(false);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		switch (viewId) {
		case R.id.btn_try_again:
			getGpsFix();
			break;
		case R.id.btn_main_submit_cell:
			uploadCell();
			break;
		case R.id.btn_main_show_cells:
			
			Intent map = new Intent(this, MapActivity.class);
			if(!DEBUG_MODE) map.putExtra(MapActivity.EXTRA_CUR_LAT, mCurrentLocation.getLatitude());
			if(!DEBUG_MODE) map.putExtra(MapActivity.EXTRA_CUR_LON, mCurrentLocation.getLongitude());
			startActivity(map);
			break;
		}

	}

	/**
	 * Convenient method for getting cell information an uploading it to the server
	 */
	private void uploadCell() {
		
		//if gps fix is outdated
		if(!mLocHelper.gpsFixIsUpToDate(mCurrentLocation)){
			mBtnTryAgain.setVisibility(View.VISIBLE);
			Toast.makeText(this,"GPS position outdated, please get a new GPS fix!",Toast.LENGTH_LONG).show();
			return;
		}
		
		try {
			Cell cell = LocationHelper.getCurrentCell(this);

			if(mCurrentLocation == null){
				Log.e(TAG, "No GPS loc!!!");
				return;
			}
			cell.setLat((float) mCurrentLocation.getLatitude());
			cell.setLon((float) mCurrentLocation.getLongitude());
			
			showStatus("Submitting cell...");
			
			mServerRequest.addCell(cell);

		} catch (NetworkErrorException e) {
			e.printStackTrace();
			Toast.makeText(this,"No internet connection, try again when it is back.", Toast.LENGTH_LONG).show();
			
		} catch (NoServiceException e1) {
			e1.printStackTrace();
			Toast.makeText(this,"No service, try again when it is back.", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Shows a {@link ProgressBar} and {@link TextView} that displays
	 * the passed message
	 * @param statusMsg message to display
	 */
	private void showStatus(String statusMsg){
		mBtnAddCell.setEnabled(false);
		mBtnShowInArea.setEnabled(false);
		mTextViewStatus.setText(statusMsg);
		mProgressLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Removes the {@link ProgressBar} and {@link TextView} visible that display a status
	 */
	private void removeStatus(boolean enableButtons){
		mBtnAddCell.setEnabled(enableButtons);
		mBtnShowInArea.setEnabled(enableButtons);
		mProgressLayout.setVisibility(View.GONE);
	}

	// callbacks
	@Override
	public void addCellCallback(ResponseCode code) {
		
		removeStatus(true);
		
		if(code == ResponseCode.OK){
			Toast.makeText(this,"Cell uploaded. Thanks!",Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(this,"Something went wrong, try again!",Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void getInAreaCallback(ResponseCode code, List<Cell> cells) {
		//ignore
	}

	@Override
	public void onGpsFix(Location location) {
		removeStatus(true);
		mCurrentLocation = location;
		mBtnTryAgain.setVisibility(View.GONE);

	}

	@Override
	public void onGpsTimeOut() {
		// jepp, code smell... sorry!
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBtnAddCell.setEnabled(false);
				mBtnShowInArea.setEnabled(false);
				mBtnTryAgain.setVisibility(View.VISIBLE);
				removeStatus(false);
				Toast.makeText(
						MainActivity.this,
						"Cannot get GPS position. Are you inside a building?",
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
