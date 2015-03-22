package com.sypir.sypertv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.NowPlayingProto.SongMessage;
import com.sypir.bluetooth.TvConnectionService;
import com.sypir.utils.MusicUtils;

import java.util.List;


public class MainActivity extends Activity
{
    private static ListView musicList;
    private static SongListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicList = (ListView) findViewById(R.id.musiclist);
        mAdapter = new SongListAdapter(getApplicationContext(), MusicUtils.getSongLibrary(getApplicationContext()));
        musicList.setAdapter(mAdapter);

        Toast.makeText(getApplicationContext(),"starting app", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), TvConnectionService.class);
        startService(intent);
    }

    public static void addSong(SongMessage song)
    {
        mAdapter.songs.add(song);
        mAdapter.notifyDataSetChanged();
    }

    private static class SongListAdapter extends ArrayAdapter
    {
        private Context mContext = null;
        private LayoutInflater inflater = null;
        public static List<SongMessage> songs = null;

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
                viewholder.songIcon.setImageResource(R.drawable.sypir);
            }

            viewholder.songPB = song;

            convertView.setTag(viewholder);
            return convertView;
        }
    }

}
