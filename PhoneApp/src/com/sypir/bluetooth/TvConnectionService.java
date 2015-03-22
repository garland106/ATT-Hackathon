package com.sypir.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.protobuf.BluetoothProto.BTMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.NowPlayingProto.SongMessage;
import com.sypir.utils.Constants;

public class TvConnectionService extends Service 
{

	// Bluetooth Connection components
	public static BluetoothServer mServer;
	public static BluetoothClient mClient;
	public static Handler serverHandler;
	public static Handler clientHandler;

	// Bluetooth Devices
	public static BluetoothAdapter mAdapter;
	public static BluetoothDevice pairedPhone;
	
	private static final String TAG = "Bluetooth Background Service";
	
	
	@Override
	public void onCreate() 
	{
		Log.d(TAG,"Starting Phone connection Bluetooth service");
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		//The hardcoded bluetooth address will be supplied by the settings app in the future
		String myTv = "F8:8F:CA:22:19:30";
		pairedPhone = mAdapter.getRemoteDevice(myTv); //Hardcoded your phone
		
		serverHandler = new Handler() 
		{
	        @Override
	        public void handleMessage(Message message) 
	        {
	            switch (message.what) 
	            {
		            case MessageType.DATA_RECEIVED:
		            {
		            	Toast.makeText(TvConnectionService.this, "Data Received, Now Parsing", Toast.LENGTH_SHORT).show();
		            	byte[] buffer = (byte[]) message.obj;
		            	try
		            	{
		            		BTMessage receivedMsg = BTMessage.parseFrom(buffer);
		            		readBluetoothData(receivedMsg);//Process handling of different commands here
		            	}
		            	catch(Exception e)
		            	{
		            		e.printStackTrace();
		            	}
		            	break;
		            }
	            }
	        }
	    };
	    clientHandler = new Handler() 
		{
	        @Override
	        public void handleMessage(Message message) 
	        {
	            switch (message.what) 
	            {
	                case MessageType.CONNECTION_ERROR: 
	                {
	                    Toast.makeText(TvConnectionService.this, "Cannot Connect to Bluetooth Device", Toast.LENGTH_SHORT).show();
	                    break;
	                }
	                case MessageType.SUCCESSFUl_CONNECTION:
	                {
	                	Toast.makeText(TvConnectionService.this, "Connected Successfully", Toast.LENGTH_SHORT).show();
	                	break;
	                }
	            }
	        }
	    };
	    
	  //Start Bluetooth Server
	    if(mServer == null)
    	{
    		Log.d(TAG, "Initalizing server");
    		mServer = new BluetoothServer(serverHandler, mAdapter);
    		mServer.start();
    	}
	    
	    if(pairedPhone != null)
	    {
	    	mClient = new BluetoothClient(clientHandler, pairedPhone);
	    	mClient.connect(pairedPhone);
	    }
	    else
	    {
	    	mClient = new BluetoothClient(clientHandler);
	    }
	    
	    BTMessage test = BTMessage.newBuilder()
	    		.setMessageCommand(0)
	    		.build();
	    send(test);
	    
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	public static void send(BTMessage btMsg)
	{
		//Ensures that the bluetooth server is open to receive response
		if(!mServer.isServerOnline())
    	{
    		Log.d(TAG, "Initalizing server");
    		mServer = new BluetoothServer(serverHandler, mAdapter);
    		mServer.start();
    	}
		
		byte[] outbuffer = btMsg.toByteArray();
		mClient.send(outbuffer, pairedPhone);
	}
	
	private void readBluetoothData(BTMessage msg)
	{
		int command = msg.getMessageCommand();
		switch(command)
		{
			case Constants.UPDATE_MUSIC_INFO:
			{
				try 
				{
					SongMessage song = SongMessage.parseFrom(msg.getMessageByteString());
					String songName = song.getTitle();
					String artistName = song.getArtist();
					
				} 
				catch (InvalidProtocolBufferException e) 
				{
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
