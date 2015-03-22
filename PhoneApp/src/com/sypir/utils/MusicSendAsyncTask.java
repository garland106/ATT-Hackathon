package com.sypir.utils;

import com.sypir.MusicActivity;
import com.sypir.bluetooth.TvConnectionService;

import android.os.AsyncTask;
import android.util.Log;

public class MusicSendAsyncTask extends AsyncTask<Void, Void, Void>
{
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
    	TvConnectionService.send(MusicActivity.songToSend);
        return null;
    }

    protected void onPostExecute(Void result)
    {
        Log.d("Done Transfering", "Done Transferring");
    }
}
