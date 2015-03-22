package com.sypir;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.Files;
import com.google.protobuf.BluetoothProto.BTMessage;
import com.google.protobuf.NowPlayingProto.SongMessage;
import com.sypir.utils.MusicSendAsyncTask;
import com.sypir.utils.MusicUtils;


public class MusicActivity extends Activity 
{
	private ListView mSongList;
	private static List<SongMessage> mSongs;
	private SongListAdapter adapter;
	private static int SCREEN_WIDTH = 0;
	private static Context mContext;
	//private 
    
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_selection);
        
        mSongList = (ListView) findViewById(R.id.list);
        mSongs = MusicUtils.getSongLibrary(getApplicationContext());
        
        adapter = new SongListAdapter(getApplicationContext(), mSongs);
		mSongList.setAdapter(adapter);
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		SCREEN_WIDTH = size.x;
		mContext = getApplicationContext();
    }
	
	private static class SongListAdapter extends ArrayAdapter
	{
		private Context mContext = null;
		private LayoutInflater inflater = null;
		private static List<SongMessage> songs = null;
		
		static class ViewHolder 
		{
            TextView line1;
            TextView line2;
            ImageView songIcon;
            SongMessage songPB;
        }
		
		public SongListAdapter(Context context, List<SongMessage> items)
		{
			super(context, R.layout.songlistitem, items);
			this.songs = items;
			this.mContext = context;
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public SongMessage getItem(int position)
		{
			return songs.get(position);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder viewholder;
			SongMessage song = (SongMessage) getItem(position);
			convertView = inflater.inflate(R.layout.songlistitem, parent, false);

			viewholder = new ViewHolder();
			viewholder.line1 = (TextView) convertView.findViewById(R.id.songname);
			viewholder.line2 = (TextView) convertView.findViewById(R.id.songartist);
			viewholder.songIcon = (ImageView) convertView.findViewById(R.id.songicon);
			
			viewholder.line1.setText(song.getTitle());
			viewholder.line2.setText(song.getArtist());
			
			byte[] rawArt = song.getSongIcon().toByteArray();
			Bitmap mIcon = null;
			System.out.println(rawArt.length);
			if(rawArt.length > 0)
			{
				mIcon = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length);
				viewholder.songIcon.setImageBitmap(mIcon);
			}
			else
			{
				viewholder.songIcon.setImageResource(R.drawable.music);
			}
			
			viewholder.songPB = song;
			
			convertView.setTag(viewholder);
			setItemTouch(convertView, position);
			return convertView;
		}
	}
	
	private static void setItemTouch(View v, final int pos)
	{
		v.setOnTouchListener(new OnTouchListener() {
			
			private float initial_xPos;
			private float diff = 0;
		    private float newX = 0;
		    private boolean firstTouch = true;
		    private int position = pos;
	        @Override
	        public boolean onTouch(View v, MotionEvent event) 
	        {
	        	boolean returnVal = false;
	        	
	        	switch(event.getAction())
	        	{
		        	case MotionEvent.ACTION_DOWN:
		        	{
		        		if(firstTouch)
		        		{
		        			initial_xPos = v.getX();
		        			firstTouch = false;
		        		}
		        		diff = event.getRawX() - v.getX();
		        		returnVal = true;
		        		break;
		        	}
		        	case MotionEvent.ACTION_MOVE:
		        	{
		        		//Set the new Position
		        		newX = event.getRawX();
		        		if(newX >= diff && newX < SCREEN_WIDTH-diff)
		        		{
		        			v.setX(newX-diff);
		        		}
		        		returnVal = true;
		        		break;
		        	}
		        	case MotionEvent.ACTION_UP:
		        	{
		        		if(v.getX() >= SCREEN_WIDTH-(diff*3))
		        		{
		        			Toast.makeText(mContext, "Sending Song...", Toast.LENGTH_SHORT).show();
		        			System.out.println(mSongs.get(position).getDataURI());
		        			File file = new File(mSongs.get(position).getDataURI());
		        			byte[] songData;
							try 
							{
								songData = Files.toByteArray(file);
								System.out.println(songData.length);
								
								SongMessage songMsg = SongMessage.newBuilder()
										.setTitle(mSongs.get(position).getTitle())
										.setArtist(mSongs.get(position).getArtist())
										.setAlbum(mSongs.get(position).getAlbum())
										//.setArtistBytes(ByteString.copyFrom(songData))
										.build();
								BTMessage btMessage = BTMessage.newBuilder()
										.setMessageCommand(1)
										.setMessageByteString(songMsg.toByteString())
										.build();
								songData = null;
								sendMusic(btMessage);
								
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
		        			
		        		}
		        		v.setX(initial_xPos);
		        		break;
		        	}
		        	default:
		        	{
		        		v.setX(initial_xPos);
		        		break;
		        	}
	        	}
	        	return returnVal;
	        }
	    });
	}
	
	public static BTMessage songToSend = null;
	public static void sendMusic(BTMessage song)
	{
		songToSend = song;
		if(songToSend != null)
		{
			new MusicSendAsyncTask().execute();
		}
	}
}
