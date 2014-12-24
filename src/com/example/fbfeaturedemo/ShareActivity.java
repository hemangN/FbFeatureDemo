package com.example.fbfeaturedemo;

import java.util.ArrayList;
import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShareActivity extends Activity implements OnClickListener	{
	
	 Button btn_share;
	 
	 //============ Fb Edit =======================================
	 private static List<String> permissions;
	 Session.StatusCallback statusCallback = new SessionStatusCallback();
	 ProgressDialog dialog; 
	 //=============== fb Edit ==============================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		btn_share = (Button)findViewById(R.id.btn_share);
		btn_share.setOnClickListener(this);
		/***** FB Permissions *****/
		permissions = new ArrayList<String>();
		permissions.add("email");
		/***** End FB Permissions *****/ 
		
		
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			session.addCallback(statusCallback);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(
						statusCallback).setPermissions(permissions));
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_share:
			
			Session session = Session.getActiveSession();
			if(!session.isOpened()) {
			session.openForRead(new Session.OpenRequest(ShareActivity.this).setCallback(statusCallback).setPermissions(permissions));
			} else {
			Session.openActiveSession(ShareActivity.this, true, statusCallback);
			} 
			
			break;

		default:
			break;
		}
	}
	
	
	
	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// Check if Session is Opened or not
			//processSessionStatus(session, state, exception);
		}
	}
	
	
	/********** Activity Methods **********/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(ShareActivity.this,
				requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Save current session
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onStart() {
		// TODO Add status callback
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	protected void onStop() {
		// TODO Remove callback
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

}
