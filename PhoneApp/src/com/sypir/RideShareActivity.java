package com.sypir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class RideShareActivity extends Activity {

	private ImageView lyftAction;
	private ImageView uberAction;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ride_share);
		
		lyftAction = (ImageView) findViewById(R.id.lyft);
		uberAction = (ImageView) findViewById(R.id.uber);
		
		lyftAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		uberAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = getPackageManager().getLaunchIntentForPackage("com.ubercab");
				startActivity(intent);
			}
		});
	}
}
