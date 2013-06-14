package de.opencelldroid;

import java.util.List;

import de.opencelldroid.loc.Cell;
import de.opencelldroid.net.ServerCallback;
import de.opencelldroid.net.ServerRequest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener, ServerCallback {

	private static final String TAG = "MainActivity";
	private ServerRequest serverRequest;
	
	// Possible server responses
	public enum ResponseCode {
		NOT_OK,
		OK
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.serverRequest = new ServerRequest(getString(R.string.opencellid_apikey), getBaseContext(), this, true);
	}
	
	@Override
	public void onDestroy() {
		// Always call the superclass
		super.onDestroy();
		
		// Stop method tracing that the activity started during onCreate()
		android.os.Debug.stopMethodTracing();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		switch (viewId) {
		case R.id.btn_main_submit_cell:
			Log.d (TAG, "Clicked on submit cell");
			this.serverRequest.addCell(1, 1, 1, 1, 1.0f, 1.0f);
			break;
		case R.id.btn_main_show_cells:
			Log.d (TAG, "Clicked on show cells");
			break;
		default:
			Log.w (TAG, "No case for this button");
		}
	}

	@Override
	public void addCellCallback(de.opencelldroid.net.ServerRequest.ResponseCode code) {
		Log.d(TAG, "Cell got successfully added to opencellid.org!");
	}

	@Override
	public void getInAreaCallback(de.opencelldroid.net.ServerRequest.ResponseCode code, List<Cell> cells) {
		Log.d(TAG, "GetInArea request was successful!");
	}

}
