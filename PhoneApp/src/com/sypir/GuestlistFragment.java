package com.sypir;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.protobuf.NowPlayingProto.SongMessage;

public class GuestlistFragment extends Fragment
{
	private ListView mGuestlist;
	private GuestlistAdapter mAdapter;
	
	public static Fragment newInstance() 
	{
        GuestlistFragment f = new GuestlistFragment();
        return f;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Initialize UI Elements
		View v = inflater.inflate(R.layout.guestlist_frag, container, false);
		
		mGuestlist = (ListView) v.findViewById(R.id.guestlist);
		mAdapter = new GuestlistAdapter(v.getContext(), null);
		//mGuestlist.setAdapter(mAdapter);
        return v;
    }
	
	private static class GuestlistAdapter extends ArrayAdapter
	{
		private Context mContext = null;
		private LayoutInflater inflater = null;
		private static List<SongMessage> songs = null;
		
		static class ViewHolder 
		{
            TextView name;
            TextView phonenumber;
            ImageView picture;
        }
		
		public GuestlistAdapter(Context context, List<SongMessage> items)
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
			viewholder.name = (TextView) convertView.findViewById(R.id.songname);
			viewholder.phonenumber = (TextView) convertView.findViewById(R.id.songartist);
			viewholder.picture = (ImageView) convertView.findViewById(R.id.songicon);
			
			viewholder.name.setText(song.getTitle());
			viewholder.phonenumber.setText(song.getArtist());
			
			byte[] rawArt = song.getSongIcon().toByteArray();
			Bitmap mIcon = null;
			System.out.println(rawArt.length);
			if(rawArt.length > 0)
			{
				mIcon = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length);
				viewholder.picture.setImageBitmap(mIcon);
			}
			else
			{
				viewholder.picture.setImageResource(R.drawable.music);
			}
			
			convertView.setTag(viewholder);
			return convertView;
		}
	}
}
