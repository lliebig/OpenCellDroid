package de.opencelldroid;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import de.opencelldroid.loc.Cell;
import de.opencelldroid.loc.LocationHelper;

public class MainActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "OpenCellDroid";
	
	private LocationManager mLocMan;
	private GpsListener mGpsListener;
	private Location mCurrentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGpsListener = new GpsListener();
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
		
		switch(viewId){
		case R.id.btn_main_submit_cell:
			try {
				Cell cell = LocationHelper.getCurrentCell(this);
				Log.d(TAG, cell.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			break;
		case R.id.btn_main_show_cells:
			
			try {
				//FIXME: Timeout???
				mLocMan = LocationHelper.requestGpsLocation(this, mGpsListener);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
	}

	//inner classes
	class GpsListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			mCurrentLocation = location;
			Log.d(TAG, mCurrentLocation.toString());
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
