package com.sypir.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BluetoothClient extends Thread
{
	private static final String TAG = "Bluetooth Server";
	private static final UUID MY_UUID = UUID.fromString("e0b436a0-a885-4d10-b197-bf9f23bdf3b9");
	private BluetoothSocket mSocket;
	private Handler activityHandler;
	public Handler incomingHandler;

	public BluetoothClient(Handler handler, BluetoothDevice device)
	{
		this.activityHandler = handler;
		BluetoothSocket tempSocket = null;
		try
		{
			tempSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} 
		catch (Exception e)
		{
			Log.e(TAG, "Error getting server socket ");
		}
		this.mSocket = tempSocket;

	}

	public BluetoothClient(Handler handler)
	{
		Log.i(TAG, "Didn't Receive Device, Initiating with only handler");
		this.activityHandler = handler;
	}
	
	public synchronized void connect(BluetoothDevice device)
	{

		BluetoothSocket tempSocket = null;
		try
		{
			// close any open connections
			if (mSocket.isConnected())
			{
				mSocket.close();
			}
			// create new connection with new device
			tempSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			tempSocket.connect();
			// send message for successful connection
			if (tempSocket.isConnected())
			{
				activityHandler.sendEmptyMessage(MessageType.SUCCESSFUl_CONNECTION);
			}
		} catch (Exception e)
		{
			Log.e(TAG, "error connecting to server socket ");
		}
		this.mSocket = tempSocket;

	}

	public synchronized void send(byte[] buffer, BluetoothDevice device)
	{
		connect(device);
		Log.d(TAG, "received data to send");
		try
		{
			Message msg = new Message();
			msg.what = MessageType.SENDING_DATA;
			msg.obj = buffer;
			activityHandler.sendMessage(msg);
			OutputStream outStream = mSocket.getOutputStream();
			InputStream inStream = mSocket.getInputStream();

			// sends headers
			outStream.write(Constants.HEADER_MSB);
			outStream.write(Constants.HEADER_LSB);

			// writes size of bytes to send
			outStream.write(Utils.intToByteArray(buffer.length));

			// write digest
			byte[] digest = Utils.getDigest(buffer);
			outStream.write(digest);

			// write main data now
			outStream.write(buffer);
			outStream.flush();

			Log.d(TAG, "Waiting for confirmation of data reception");
			byte[] incoming = new byte[16];
			int incomingIndex = 0;
			try
			{
				while (true)
				{
					//System.out.println(incomingIndex);
					byte[] header = new byte[1];
					inStream.read(header, 0, 1);
					incoming[incomingIndex++] = header[0];
					if (incomingIndex == 16)
					{

						if (Utils.digestMatch(buffer, incoming))
						{
							Log.d(TAG, "Data Received OK");
							BluetoothClient.this.activityHandler.sendEmptyMessage(MessageType.DATA_SENT_OK);
						} else
						{
							Log.e(TAG, "Digest didn't match, resend");
							BluetoothClient.this.activityHandler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
						}
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void run()
	{
		System.out.println("running");
		try
		{
			Log.d(TAG, "Opening Socket for Client");
			mSocket.connect();
			Log.d(TAG, "Connection Established");
			activityHandler.sendEmptyMessage(MessageType.READY_FOR_DATA);
		} 
		catch (Exception e)
		{
			Log.e(TAG, "Error -- Socket Closed");
			activityHandler.sendEmptyMessage(MessageType.CONNECTION_ERROR);
			e.printStackTrace();
		}

		Looper.prepare();

		incomingHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				if (msg.obj != null)
				{
					Log.d(TAG, "Handler received data to send");
					byte[] buffer = (byte[]) msg.obj;
					try
					{
						activityHandler
								.sendEmptyMessage(MessageType.SENDING_DATA);
						OutputStream outStream = mSocket.getOutputStream();
						InputStream inStream = mSocket.getInputStream();

						// sends headers
						outStream.write(Constants.HEADER_MSB);
						outStream.write(Constants.HEADER_LSB);

						// writes size of bytes to send
						outStream.write(Utils.intToByteArray(buffer.length));

						// write main data now
						outStream.write(buffer);
						outStream.flush();

						Log.d(TAG, "Waiting for confirmation of data reception");
						byte[] incoming = new byte[16];
						int incomingIndex = 0;
						try
						{
							while (true)
							{
								byte[] header = new byte[1];
								inStream.read(header, 0, 1);
								incoming[incomingIndex++] = header[0];
								if (incomingIndex == 16)
								{
									if (Utils.digestMatch(buffer, incoming))
									{
										Log.d(TAG, "Data Received OK");
										BluetoothClient.this.activityHandler
												.sendEmptyMessage(MessageType.DATA_SENT_OK);
									} else
									{
										Log.e(TAG,
												"Digest didn't match, resend");
										BluetoothClient.this.activityHandler
												.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
									}
									break;
								}
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}

					} catch (Exception e)
					{
						e.printStackTrace();
					}

				}
			}
		};

		Looper.loop();
	}

	public void cancel()
	{
		try
		{
			Log.d(TAG, "Closing Socket -- if open");
			if (mSocket.isConnected())
			{
				mSocket.close();
			}
		} 
		catch (Exception e)
		{
			Log.e(TAG, "Error Closing Server Socket");
		}
	}
}
