package com.sypir;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
		
		//Create Mocklist
		final List<Pair<String,String>> mGuests = new ArrayList<Pair<String,String>>();
		mGuests.add(new Pair("Chris O'Brien", "408-437-2379"));
		mGuests.add(new Pair("Kanye West", "525-232-3321"));
		
		mAdapter = new GuestlistAdapter(v.getContext(), mGuests);
		mGuestlist.setAdapter(mAdapter);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("guestlist");
        query.setLimit(200);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, com.parse.ParseException e) 
			{
				if(e == null)
                {
                	for(ParseObject p : messages)
                	{
                		Pair newG = new Pair(p.getString("name"),p.getString("phonenumber"));
                		mAdapter.guests.add(newG);
                	}
                }
				mAdapter.notifyDataSetChanged();
			}
        });
        return v;
    }
	
	private static class GuestlistAdapter extends ArrayAdapter
	{
		private Context mContext = null;
		private LayoutInflater inflater = null;
		public static List<Pair<String,String>> guests = null;
		
		static class ViewHolder 
		{
            TextView name;
            TextView phonenumber;
            ImageView picture;
        }
		
		public GuestlistAdapter(Context context, List<Pair<String,String>> items)
		{
			super(context, R.layout.songlistitem, items);
			this.guests = items;
			this.mContext = context;
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public Pair<String,String> getItem(int position)
		{
			return guests.get(position);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ViewHolder viewholder;
			Pair<String,String> g = (Pair<String,String>) getItem(position);
			convertView = inflater.inflate(R.layout.songlistitem, parent, false);

			viewholder = new ViewHolder();
			viewholder.name = (TextView) convertView.findViewById(R.id.songname);
			viewholder.phonenumber = (TextView) convertView.findViewById(R.id.songartist);
			viewholder.picture = (ImageView) convertView.findViewById(R.id.songicon);
			
			viewholder.name.setText(g.first);
			viewholder.phonenumber.setText(g.second);
			viewholder.picture.setImageResource(R.drawable.ic_launcher);
			
			convertView.setTag(viewholder);
			return convertView;
		}
	}
}
