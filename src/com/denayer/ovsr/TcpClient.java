/*
 * Copyright (C) <2014> <Dries Goossens / driesgoossens93@gmail.com , Koen Daelman / koendaelman@gmail.com >
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
*/
package com.denayer.ovsr;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {
	public static String DEFAULT_IP_ADDR="192.168.0.198";
	public static int DEFAULT_PORT= 64000;
	public static String SERVER_IP = DEFAULT_IP_ADDR; //your computer IP address
	public static int SERVER_PORT = DEFAULT_PORT;
	
	static SharedPreferences settings;
	
	// message to send to the server
	private String mServerMessage;
	// sends message received notifications
	private OnMessageReceived mMessageListener = null;
	// while this is true, the server will continue running
	private boolean mRun = false;
	// used to send messages
	private PrintWriter mBufferOut;
	// used to read messages from the server
	private BufferedReader mBufferIn;
	public static boolean isConnected = false;
	public DataInputStream mByteStream;
	public static boolean isByte = false;
	private byte[] mByteServerMessage = new byte[64000];

	/*! \brief Constructor of the class. OnMessagedReceived listens for the messages received from server
	 * @param listener is a listener for the OnMessageReceived. The listener is created in Java and is passed to be able to call the listener from the TcpClient functions.
	 */
	public TcpClient(Context mainContext,OnMessageReceived listener) {
		mMessageListener = listener;
		settings = mainContext.getSharedPreferences("Preferences", 0);
		if(settings.getBoolean("UseDefault", true))
		{
			SERVER_IP = DEFAULT_IP_ADDR;
			SERVER_PORT = DEFAULT_PORT;
		}
		else
		{
			SERVER_IP = settings.getString("ServerIP", DEFAULT_IP_ADDR);
			SERVER_PORT = settings.getInt("ServerPort", DEFAULT_PORT);
		}
	}

	/*! \brief Sends the message entered by client to the server
	 *
	 * @param message text entered by client
	 */
	public void sendMessage(String message) {
		if (mBufferOut != null && !mBufferOut.checkError()) {
			mBufferOut.println(message);
			mBufferOut.flush();
		}
	}

	/*! \brief Close the connection and release the members
	 */
	public void stopClient() {
		mRun = false;

		if (mBufferOut != null) {
			mBufferOut.flush();
			mBufferOut.close();
		}

		mMessageListener = null;
		mBufferIn = null;
		mBufferOut = null;
		mServerMessage = null;

		isConnected = false;

	}
	/*! \brief Thread that listens for incomming data.
	 * 
	 * In this thread the socket will be created and connection will be made. 
	 * The while loop listens for messages sent by the server.
	 * When a message is received, the messageReceived function will be called.
	 */
	public void run() {

		mRun = true;

		try {
			//here you must put your computer's IP address.
			InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

			Log.e("TCP Client", "C: Connecting...");

			//create a socket to make the connection with the server
			Socket socket = new Socket(serverAddr, SERVER_PORT);

			try {
				isConnected = true;
				//sends the message to the server
				mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

				//receives the message which the server sends back
				mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				mByteStream = new DataInputStream(socket.getInputStream());
				
				//in this while the client listens for the messages sent by the server
				while (mRun) {
					mServerMessage = mBufferIn.readLine();
					if (mByteServerMessage != null && mMessageListener != null) {
						//call the method messageReceived from MyActivity class
						mMessageListener.messageReceived(mServerMessage);
					}
				}
				Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

			} catch (Exception e) {

				Log.e("TCP", "S: Error", e);

			} finally {
				//the socket must be closed. It is not possible to reconnect to this socket
				// after it is closed, which means a new socket instance has to be created.
				socket.close();
			}

		} catch (Exception e) {

			Log.e("TCP", "C: Error", e);

		}

	}

	/*! /brief Declare the interface. 
	 *The method messageReceived(String message) must be implemented in the MyActivity
	 *class at on asynckTask doInBackground
	 */
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
}
