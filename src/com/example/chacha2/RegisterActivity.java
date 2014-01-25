package com.example.chacha2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	private AlertDialog.Builder builder;
	
	private String replyFormServer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		builder = new AlertDialog.Builder(this);
		setContentView(R.layout.activity_register);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	public void register(View view){
		EditText editRegisterEmail = (EditText)findViewById(R.id.editEmailRegister);
		EditText editRegisterName= (EditText)findViewById(R.id.editNameRegister);
		EditText editRegisterPassword = (EditText)findViewById(R.id.editPasswordRegister);
		String email = editRegisterEmail.getText().toString();
		String name = editRegisterName.getText().toString();
		String password = editRegisterPassword.getText().toString();
		
		new MyAsyncTask().execute(email,name,password);
	}

	private class MyAsyncTask extends AsyncTask<String, String , String>{

		@Override
		protected String doInBackground(String... args) {
				Socket socket = null;			
				PrintWriter out = null;
				BufferedReader in = null;
				try{
					socket = new Socket("192.168.1.112", 1234);
					out = new PrintWriter(socket.getOutputStream(),true);
					in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String id = args[0];
					String name = args[1];
					String password = args[2];
					String send = id + ":" + name + ":" + password;
					out.println(send);
					replyFormServer = in.readLine();
					socket.close();
		        	out.close();
		        	in.close();
				}catch (UnknownHostException e) {
					e.printStackTrace();
		        } catch (IOException e) {
		        	e.printStackTrace();
		        }
			return null;
		}
		
		protected void onPostExecute(String result){
			builder.setTitle(R.string.registerResult);
			int code = -1;
			code = Integer.parseInt(replyFormServer);
			switch(code){
			case -1:
				builder.setMessage(R.string.registerFail);
				break;
			case 0:
				builder.setMessage(R.string.emailExists);
				break;
			case 1:
				builder.setMessage(R.string.registerSuccess);
			}			
			builder.create().show();
			
		}
	}
	

}
