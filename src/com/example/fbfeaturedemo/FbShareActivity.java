package com.example.fbfeaturedemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class FbShareActivity extends Activity implements OnClickListener{

	
	Button btn_share;
	 
	 //============ Fb Edit =======================================
	 private static List<String> permissions;
	 //=============== fb Edit ==============================
	 
		//============== For Testing post images ========================================================//
		private UiLifecycleHelper uiHelper;
	    private Session.StatusCallback callback_uiHelper = new Session.StatusCallback() {
	        @Override
	        public void call(final Session session, final SessionState state, final Exception exception) 
	        {
	            onSessionStateChange(session, state, exception);
	        }
	    };

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		uiHelper = new UiLifecycleHelper(FbShareActivity.this, callback_uiHelper);
        uiHelper.onCreate(savedInstanceState);
		
		btn_share = (Button)findViewById(R.id.btn_share);
		btn_share.setOnClickListener(this);
		/***** FB Permissions *****/
		permissions = new ArrayList<String>();
		permissions.add("email");
		/***** End FB Permissions *****/ 
	}
	
	
	public  void onClickLogin()
	{
		openActiveSession(FbShareActivity.this, true, Arrays.asList("email", "user_birthday", "user_hometown", "user_location"), new Session.StatusCallback() 
		{
			@Override
			public void call(Session session, SessionState state, Exception exception) 
			{
				if (exception != null) 
				{
					//Log.d("Facebook", exception.getMessage());
				}
				
				if(state.isOpened())
				{
					//Log.d("Facebook", "Session State: " + session.getState());
					//System.out.println("=========== Share Type in call back========================="+shareType);
					/*if(shareType != null)
					{
						publishStory();	
					}
					else*/
					{	
						publishFeedDialog();
					}
				}
				if(state.isClosed())
				{
					//Log.d("Facebook", "Session State: " + session.getState());
				}
				
			}
		});
	}

	
	private  Session openActiveSession(Activity activity,boolean allowLoginUI, List permissions, StatusCallback callback) 
	{
		OpenRequest openRequest = new OpenRequest(activity).setPermissions(permissions).setCallback(callback);
		Session session = new Session.Builder(activity).build();
		
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) 
		{
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) 
	{
        if (state.isOpened()) 
        {
        	/*if(shareType != null)
			{
				publishStory();	
			}
			else*/
			{	
				publishFeedDialog();
			}
        }
        else if (state.isClosed()) 
        {
        	onClickLogin(); 
        }
    }

	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	Session.getActiveSession().onActivityResult(FbShareActivity.this, requestCode, resultCode, data);
    	uiHelper.onActivityResult(requestCode, resultCode, data);
    }



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) 
		{
			onSessionStateChange(session, session.getState(), null);
		}
		else
		{
			
			System.out.println("===== Else Part ======");
			onClickLogin();
		}
	}
	
	private void publishFeedDialog() 
	{
			
		Bundle params = new Bundle();
		params.putString("name", "Smoke Therapy");
		params.putString("caption",
				"I am using smoke therapy");
		params.putString(
				"description","This is sample app share");
		params.putString("link", "https://developers.facebook.com/android");
		

		// Invoke the dialog
		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(FbShareActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,FacebookException error) 
					{
						if (error == null) 
						{
							// When the story is posted, echo the success
							// and the post Id.
							final String postId = values.getString("post_id");
							if (postId != null) {
								
								Toast.makeText(getApplicationContext(), "Post Successfully",Toast.LENGTH_SHORT).show();
								/*Toast.makeText(getApplicationContext(),
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();*/
							} else {
								// User clicked the Cancel button
								Toast.makeText(
										getApplicationContext()
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							// User clicked the "x" button
							Toast.makeText(
									getApplicationContext()
											.getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							// Generic, ex: network error
							Toast.makeText(
									getApplicationContext()
											.getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
						
						//FbShareActivity.this.finish();
					}

				}).build();
		feedDialog.show();
	}
	
}
