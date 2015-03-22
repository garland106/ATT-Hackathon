package com.sypir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.venmo.VenmoLibrary;

public class SelectionActivity extends Fragment
{
	private ImageView venmoAction;
	private ImageView musicAction;
	private ImageView dropboxAction;
	private ImageView taxiAction;
	private ImageView brightnesAction;
	private ImageView volumeAction;
	private ImageView playpause;
	private TextView songtitle;
	private TextView artist;
	private final int REQUEST_CODE_VENMO_APP_SWITCH = 999;
	
	public static Fragment newInstance() 
	{
        SelectionActivity f = new SelectionActivity();
        return f;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Initialize UI Elements
		View v = inflater.inflate(R.layout.activity_selection, container, false);
		//declare UI elements
				venmoAction = (ImageView) v.findViewById(R.id.venmoaction);
				musicAction = (ImageView) v.findViewById(R.id.musicaction);
				dropboxAction = (ImageView) v.findViewById(R.id.dropboxaction);
				taxiAction = (ImageView) v.findViewById(R.id.caraction);
				brightnesAction = (ImageView) v.findViewById(R.id.brightnessaction);
				volumeAction = (ImageView) v.findViewById(R.id.volumeaction);
				playpause = (ImageView) v.findViewById(R.id.playpause);
				songtitle = (TextView) v.findViewById(R.id.songtitle);
				artist = (TextView) v.findViewById(R.id.songartist);
				
				//set action listeners
				venmoAction.setOnTouchListener(venmoListener);
				musicAction.setOnTouchListener(musicListener);
				taxiAction.setOnTouchListener(taxiListener);
				dropboxAction.setOnTouchListener(dropboxListener);
				brightnesAction.setOnTouchListener(brightnessListener);
				playpause.setOnClickListener(playpauseListener);
				volumeAction.setOnTouchListener(volumeListener);
        return v;
    }

	private OnTouchListener venmoListener = new OnTouchListener(){
		
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			boolean returnVal = false;
			
			switch (event.getAction()) 
			{
				case MotionEvent.ACTION_DOWN: 
				{
					venmoAction.setImageResource(R.drawable.venmo_highlighted);
					returnVal = true;
					break;
				}
				case MotionEvent.ACTION_UP: 
				{
					venmoAction.setImageResource(R.drawable.venmo);
					if(VenmoLibrary.isVenmoInstalled(getActivity()))
	                {
	                    Intent venmoIntent = VenmoLibrary.openVenmoPayment("2407", "party", "8083926263", "0.01", "party", "pay");
	                    startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);
	                }
					returnVal = true;
					break;
				}
			}
			return returnVal;
		}
	};
	
	private OnTouchListener musicListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			boolean returnVal = false;
			
			switch (event.getAction()) 
			{
				case MotionEvent.ACTION_DOWN: 
				{
					musicAction.setImageResource(R.drawable.music_circle_highlighted);
					returnVal = true;
					break;
				}
				case MotionEvent.ACTION_UP: 
				{
					musicAction.setImageResource(R.drawable.music_circle);
					Intent intent = new Intent(getActivity(), MusicActivity.class);
					startActivity(intent);
					returnVal = true;
					break;
				}
			}
			return returnVal;
		}
	};
	
	private OnTouchListener dropboxListener = new OnTouchListener(){	
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			boolean returnVal = false;
			
			switch (event.getAction()) 
			{
				case MotionEvent.ACTION_DOWN: 
				{
					dropboxAction.setImageResource(R.drawable.dropbox_highlighted);
					returnVal = true;
					break;
				}
				case MotionEvent.ACTION_UP: 
				{
					dropboxAction.setImageResource(R.drawable.dropbox);
					returnVal = true;
					break;
				}
			}
			return returnVal;
		}
	};
	
	private OnTouchListener taxiListener = new OnTouchListener(){	
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			boolean returnVal = false;
			
			switch (event.getAction()) 
			{
				case MotionEvent.ACTION_DOWN: 
				{
					taxiAction.setImageResource(R.drawable.car_highlighted);
					returnVal = true;
					break;
				}
				case MotionEvent.ACTION_UP: 
				{
					taxiAction.setImageResource(R.drawable.car);
					returnVal = true;
					break;
				}
			}
			return returnVal;
		}
	};
	
	private OnTouchListener brightnessListener = new OnTouchListener(){	
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			boolean returnVal = false;
			
			switch (event.getAction()) 
			{
				case MotionEvent.ACTION_DOWN: 
				{
					brightnesAction.setImageResource(R.drawable.brightness_highlighted);
					returnVal = true;
					break;
				}
				case MotionEvent.ACTION_UP: 
				{
					brightnesAction.setImageResource(R.drawable.brightness);
					returnVal = true;
					break;
				}
			}
			return returnVal;
		}
	};
	
	private OnTouchListener volumeListener = new OnTouchListener(){	
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			boolean returnVal = false;
			
			switch (event.getAction()) 
			{
				case MotionEvent.ACTION_DOWN: 
				{
					volumeAction.setImageResource(R.drawable.volume_highlighted);
					returnVal = true;
					break;
				}
				case MotionEvent.ACTION_UP: 
				{
					volumeAction.setImageResource(R.drawable.volume);
					returnVal = true;
					break;
				}
			}
			return returnVal;
		}
	};
	
	private OnClickListener playpauseListener = new View.OnClickListener() {
		
		private boolean play = true;
		@Override
		public void onClick(View v) 
		{
			if(play)
			{
				playpause.setImageResource(R.drawable.pause);
			}
			else
			{
				playpause.setImageResource(R.drawable.playbutton);
			}
			play = !play;
		}
	};
}
