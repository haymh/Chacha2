package com.example.chacha2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

public class ChatActivity extends Activity {
	
	private final static int LOGIN_FAIL = -1;
	private final static int LOGIN_SUCCESS = 1;
	private final static int CONNECTION_ERROR = 0;
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private TableLayout messages;
	private EditText messageEditText;
	private ScrollView scroll;
	private int code;
	private int messageIndex = 0;
	private AlertDialog.Builder builder;
	private ClientThread thread;
	private Stack<String> messageStack;
	
	final Handler handler = new Handler();
	
	@Override
	protected void onStart(){
		super.onStart();
		Intent intent = getIntent();
		String email = intent.getStringExtra(MainActivity.EMAIL);
		String password = intent.getStringExtra(MainActivity.PASSWORD);
		thread = new ClientThread(email,password);
		Log.v("point A", "a");
		thread.start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		socket = null;
		out = null;
		in = null;
		code = -1;
		messageStack = new Stack<String>();
		setContentView(R.layout.activity_chat);
		messages = (TableLayout)findViewById(R.id.Messages);
		messageEditText = (EditText)findViewById(R.id.messageEditText);
		scroll = (ScrollView)findViewById(R.id.messageScrollView);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		disconnect();
	}
	
	private void disconnect(){
		try{
			if(socket != null)
				socket.close();
			if(out != null)
				out.close();
			if(in != null)
				in.close();
		}catch(UnknownHostException e){
			e.printStackTrace();			
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	private void showError(int code){
		builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.loginInfo);
		switch(code){
		case LOGIN_FAIL:
			builder.setMessage(R.string.loginFail);
			break;
		case CONNECTION_ERROR:
			builder.setMessage(R.string.connectionError);
			break;
		default:
			builder.setMessage(R.string.loginFail);	
		}
		builder.create().show();
	}
	
	private void publishMessage(){
		while(!messageStack.empty())
			addMessage(messageStack.pop());
	}
	
	private void addMessage(String message){
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View messageRow = inflater.inflate(R.layout.message_row, null);
		TextView messageText = (TextView)messageRow.findViewById(R.id.messageTextView);
		messageText.setText(message);
		messages.addView(messageRow,messageIndex++);
		scroll.fullScroll(View.FOCUS_DOWN);
	}
	
	private void pushMessage(String message){
		messageStack.push(message);
	}
	
	public void send(View view){
		String s = messageEditText.getText().toString();
		if(s == null || s.equals(""))
			return;
		out.println(s);
		messageEditText.setText("");
	}
	
	private class ClientThread extends Thread{
		private String email;
		private String password;
		public ClientThread(String email,String password){
			this.email = email;
			this.password = password;
		}
		public void run(){
			try{
				socket = new Socket("192.168.1.112", 2222);
				out = new PrintWriter(socket.getOutputStream(),true);
				in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				String send = email + ":" + password;
				out.println(send);
				int code = Integer.parseInt(in.readLine());
				if(code == LOGIN_FAIL)
				{
					//showError(LOGIN_FAIL);
					finish();
				}
				/* original code without using XML
				String inputLine;
				while((inputLine = in.readLine()) != null){
					pushMessage(inputLine);
					handler.post(new Runnable(){
						public void run(){
							publishMessage();
						}
					});
				}
				*/
				
				//XML parsing
				while(true){
					XmlPullParser parser = Xml.newPullParser();
					parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		            parser.setInput(socket.getInputStream(), null);
		            parser.nextTag();
		            parser.require(XmlPullParser.START_TAG, null, "message");
		            String username = "", text = "";
		            while(parser.next() != XmlPullParser.END_TAG){
		            	if(parser.getEventType() != XmlPullParser.START_TAG){
		            		continue;
		            	}
		            	String name = parser.getName();
		            	if(name.equals("body"))
		            		continue ;
		            	else if(name.equals("user")){
		            		parser.require(XmlPullParser.START_TAG, null, "user");
		            		parser.next();
		            		username = parser.getText();
		            		parser.nextTag();
		            	}else if(name.equals("text")){
		            		parser.require(XmlPullParser.START_TAG, null, "user");
		            		parser.next();
		            		text = parser.getText();
		            		break;
		            	}
		            		 
		            }
		            String inputLine = username + " : " + text;
		            pushMessage(inputLine);
					handler.post(new Runnable(){
						public void run(){
							publishMessage();
						}
					});
				}
				
					
			}catch (UnknownHostException e) {
				//showError(CONNECTION_ERROR);
				finish();
			} catch (IOException e) {
				//showError(CONNECTION_ERROR);
				finish();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
