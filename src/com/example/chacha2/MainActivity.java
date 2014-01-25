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
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {
	public final static String EMAIL = "EMAIL";
	public final static String PASSWORD = "PASSWORD";
	
	
	private final static int EMAIL_EMPTY = 1;
	private final static int PASSWORD_EMPTY = 2;

	private AlertDialog.Builder builder;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		
		builder = new AlertDialog.Builder(this);
		setContentView(R.layout.activity_main);
		EditText password = (EditText)findViewById(R.id.editPassword);
		/*
		password.setOnEditorActionListener( new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
				{
					login();
					return true;
				}
				return false;
			}
			
		});
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void login(View view){
		login();
	}
	
	private void login(){
		EditText editEmail = (EditText)findViewById(R.id.editEmail);
		EditText editPassword = (EditText)findViewById(R.id.editPassword);
		String email = editEmail.getText().toString();
		if(email == null || email.equals(""))
		{
			showWarning(EMAIL_EMPTY);
			return;
		}
		String password = editPassword.getText().toString();
		if(password == null || password.equals(""))
		{
			showWarning(PASSWORD_EMPTY);
			return;
		}
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(EMAIL, email);
		intent.putExtra(PASSWORD, password);
		startActivity(intent);
	}
	
	public void register(View view){
		
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
		
	private void showWarning(int code){
		builder.setTitle(R.string.warning);
		switch(code){
		case EMAIL_EMPTY:
			builder.setMessage(R.string.emailEmpty);
			break;
		case PASSWORD_EMPTY:
			builder.setMessage(R.string.passwordEmpty);
			break;
		}
		builder.create().show();
	}
	
	

}
