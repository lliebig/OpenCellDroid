package de.opencelldroid;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.opencelldroid.loc.Cell;
import de.opencelldroid.loc.LocationHelper;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "OpenCellDroid";

	private GpsListener mGpsListener;
	private Location mCurrentLocation;

	static final LatLng BERLIN = new LatLng(52.519171, 13.406091);
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGpsListener = new GpsListener();

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		moveMapTo(BERLIN, "Berlin", "Default position");
	}

	private void moveMapTo(LatLng position, String title, String snippet) {
		Marker m = map.addMarker(new MarkerOptions().position(position));
		m.setTitle(title);
		m.setSnippet(snippet);
		m.showInfoWindow();
		
		// move the camera to the new position with a zoom of 15
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		switch (viewId) {
		case R.id.btn_main_submit_cell:
			try {
				Cell cell = LocationHelper.getCurrentCell(this);
				Log.d(TAG, cell.toString());
				Toast.makeText(this, "You are connected with:\n" + cell.toString(), Toast.LENGTH_LONG).show();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case R.id.btn_main_show_cells:

			try {
				// FIXME: Timeout???
				LocationHelper.requestGpsLocation(this, mGpsListener);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

	}

	// inner classes
	class GpsListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			mCurrentLocation = location;
			Log.d(TAG, mCurrentLocation.toString());
			LatLng curPos = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
			moveMapTo(curPos, "Your position", "You are here!");
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
