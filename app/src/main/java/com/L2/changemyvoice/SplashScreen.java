package com.L2.changemyvoice;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity
{

	protected void onCreate(Bundle paramBundle)
	  {
	    super.onCreate(paramBundle);
	    requestWindowFeature(1);
	    setContentView(R.layout.splesh);
	    hideStatusBar();
	    
	    Thread background = new Thread()
	    {
	    	 public void run() {
	             
	    	try {
	            // Thread will sleep for 5 seconds
	            sleep(1*1000);
	             
	            // After 5 seconds redirect to another intent
	            Intent i=new Intent(getBaseContext(),MainActivity.class);
	            startActivity(i);
	            //Remove activity
	            finish();
//	            startAppAd.showAd();
//				startAppAd.loadAd();
	             
	        } catch (Exception e) {
	         
	        }
	    }
	};
	    background.start();
	  }
	@Override
    public void onPause() {
        super.onPause();
        // Remove the activity when its off the screen
        //finish();
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }

	  @Override
	  public void onBackPressed() {
		
	  	super.onBackPressed();
	  }
	public void hideStatusBar(){

		//Hide the status bar on Android 4.0 and Lower
		if (Build.VERSION.SDK_INT < 16) {
			Window w=getWindow();
			w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

		}
		else{
			View decorView = getWindow().getDecorView();
			// Hide the status bar.
			int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(visibility);


		}

	}
}