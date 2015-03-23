package com.sypir;

import com.parse.Parse;
import com.sypir.bluetooth.TvConnectionService;
import com.sypir.utils.ParseAPIUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

public class FragmentManagerActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private GuestlistViewPagerAdapter mViewPagerAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_manager);
		
		mViewPager = (ViewPager) findViewById(R.id.fragment_manager);
		mViewPagerAdapter = new GuestlistViewPagerAdapter(getSupportFragmentManager());
      	mViewPager.setAdapter(mViewPagerAdapter);
      	
      	Parse.initialize(getApplicationContext(), ParseAPIUtils.appID, ParseAPIUtils.clientKey);
      	ParseAPIUtils.login("garlnd106@gmail.com", "password", "admin", getApplicationContext());
      	Intent intent = new Intent(getApplicationContext(), TvConnectionService.class);
		startService(intent);
	}

}
