package com.sypir.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.protobuf.ByteString;
import com.google.protobuf.NowPlayingProto.SongMessage;

public class MusicUtils
{
	/**
	 * Cursor for entire song library
	 */
	public static Cursor getSongLibraryCursor(Context context)
	{
		String[] proj = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST, 
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.TITLE, 
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.DATA
				};
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		Cursor musicCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection, null, null);
		return musicCursor;
	}

	public static List<SongMessage> getSongLibrary(Context context)
	{
		Cursor songcursor = getSongLibraryCursor(context);
		
		List<SongMessage> songdata = new ArrayList<SongMessage>();
		songcursor.moveToFirst();
		while(!songcursor.isAfterLast())
		{			
			int artistIndx = songcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
			int albumIndx = songcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
			int titleIndx = songcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			int idIndx = songcursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
			int dataIndx = songcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			
			String artist = songcursor.getString(artistIndx);
			String album = songcursor.getString(albumIndx);
			String title = songcursor.getString(titleIndx);
			int id = songcursor.getInt(idIndx);
			String data = songcursor.getString(dataIndx);
			
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			byte[] rawArt = null;
			mmr.setDataSource(context, Uri.parse(data));
			rawArt = mmr.getEmbeddedPicture();

			//Check if null
			if(artist == null || artist == ""){ artist = "Unknown Artist"; }
			if(album == null || album == ""){ album = "Unknown Album"; }
			if(title == null || title == ""){ title = "No Data"; }
			
			SongMessage songToAdd;
			if(rawArt == null)
			{
				songToAdd = SongMessage.newBuilder()
						.setArtist(artist)
						.setTitle(title).build();
			}
			else
			{
			songToAdd = SongMessage.newBuilder()
					.setArtist(artist)
					.setTitle(title)
					.setSongIcon(ByteString.copyFrom(rawArt)).build();
			}
			songdata.add(songToAdd);
			songcursor.moveToNext();
		}
		songcursor.close();
		return songdata;
	}
}
