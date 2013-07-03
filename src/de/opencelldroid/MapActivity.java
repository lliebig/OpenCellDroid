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

import java.util.ArrayList;
import java.util.List;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.opencelldroid.loc.Cell;
import de.opencelldroid.net.ServerCallback;
import de.opencelldroid.net.ServerRequest;
import de.opencelldroid.net.ServerRequest.ResponseCode;

/**
 * Main Activity for OpenCellDroid
 * 
 * @author Dmitrij Ignatjew, info@leoliebig.de
 */
public class MapActivity extends FragmentActivity implements ServerCallback{

	private static final String TAG = "OpenCellDroid";
	public static final String EXTRA_CUR_LAT = "currentLatitudeExtra";
	public static final String EXTRA_CUR_LON = "currentLongitudeExtra";
	public static final String EXTRA_CELL_LIST = "cellListExtra";
	
	private ServerRequest mServerRequest;
	private GoogleMap mMap;
	private LatLngBounds mCurrentBoundingBox;
	private Marker mCurrentPosition;
	
	private boolean updatesBypassed = false;
	private CheckBox checkBoxBypassUpdates;

	private ArrayList<Cell> receivedCellList;
	private SparseArray<SparseArray<String>> operatorDataMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//add the progrss indicator
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.activity_map); 
		checkBoxBypassUpdates = (CheckBox) findViewById(R.id.checkbox_bypass);
		checkBoxBypassUpdates.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updatesBypassed = isChecked;
				if(!isChecked) updateCells();
				
			}
		});

		initMap();
		
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition arg0) {
				
				if(!updatesBypassed){
					//check whether it's a zoom in only (new bounding box is within old bounding box
					LatLngBounds newBoundingBox = mMap.getProjection().getVisibleRegion().latLngBounds;
					
					if(mCurrentBoundingBox != null 
							&& mCurrentBoundingBox.contains(newBoundingBox.northeast) 
							&& mCurrentBoundingBox.contains(newBoundingBox.southwest)) {
						Log.d(TAG, "Zoom in");
					}
					else {
						updateCells();
					}
					
				}
			}
		});
		mServerRequest = new ServerRequest(getString(R.string.googlemaps_apikey), this, this);
		
		//get current pos and move map to!!!
		Intent i = getIntent();
		LatLng currentPosition = new LatLng(i.getDoubleExtra(EXTRA_CUR_LAT,  52.412222222222), i.getDoubleExtra(EXTRA_CUR_LON,  13.3675));
		moveMapTo(currentPosition, "Your are here");
		
		long start = System.currentTimeMillis();
		operatorDataMap = CSVParser.getOperatorData(this);
		Log.d(TAG, "Parsing time: " + (System.currentTimeMillis() - start) + "ms");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mServerRequest.cancel();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initMap() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}
	}

	private void addMarkersToMap() {
		
		MarkerOptions markOpt;
		SparseArray<String> providerList = null;
		String providerName = null;
		
		
		for (Cell c : receivedCellList){
			
			providerList = operatorDataMap.get(c.getMcc());
			if(providerList != null) providerName = providerList.get(c.getMnc());
			if(providerName == null) providerName = "Unknown";
			
			markOpt = new MarkerOptions();
			markOpt.position( new LatLng(c.getLat(),c.getLon()) );
			markOpt.title(providerName);
			markOpt.snippet(c.getCellIdString()); // + "(MCC:MNC:LAC:CID)"
			markOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.celltower));
			
			mMap.addMarker(markOpt);
			
		}
	}

	private void moveMapTo(LatLng position, String title) {
		if (mCurrentPosition != null) mCurrentPosition.remove();
		mCurrentPosition = mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.droid)));
		mCurrentPosition.setTitle(title);
		mCurrentPosition.showInfoWindow();

		// move the camera to the new position with a zoom of 15
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
	}

	/**
	 * Gets the bounding box from the map view and requests a cell list update from the server
	 */
	private void updateCells() {
		
		setProgressBarIndeterminateVisibility(true);
		
		mCurrentBoundingBox = mMap.getProjection().getVisibleRegion().latLngBounds;
		LatLng ne = mCurrentBoundingBox.northeast;
		LatLng sw = mCurrentBoundingBox.southwest;
		
//		Log.d(TAG, "BBox: " + ne.toString() + " | " + sw.toString());
		
		if(ne.latitude == 0 && ne.longitude == 0 && sw.latitude == 0 && sw.longitude== 0){
			Log.e(TAG, "Could not create bounding box from map view!");
			setProgressBarIndeterminateVisibility(false);
			return;
		}
		
		try {
			//the OpenCellId API documentation is not correct, longitudes first!
			mServerRequest.getInArea(
					new double[]{sw.longitude, sw.latitude, ne.longitude, ne.latitude}, 
					50);
		} catch (NetworkErrorException e) {
			setProgressBarIndeterminateVisibility(false);
			Toast.makeText(this, "No network connection. Please try again!", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public void addCellCallback(ResponseCode code) {
		// ignore
		
	}

	@Override
	public void getInAreaCallback(ResponseCode code, List<Cell> cells) {
		if(code == ResponseCode.OK && cells != null){
			receivedCellList = (ArrayList<Cell>) cells;
			addMarkersToMap();
			setProgressBarIndeterminateVisibility(false);
		}
		else{
			setProgressBarIndeterminateVisibility(false);
		}
		
	}

}
