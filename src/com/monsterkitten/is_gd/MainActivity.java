package com.monsterkitten.is_gd;

/* 
 “We go about our daily lives understanding almost 
 nothing of the world. We give little thought to the 
 machinery that generates the sunlight that makes life 
 possible, to the gravity that glues us to an Earth that 
 would otherwise send us spinning off into space, or to 
 the atoms of which we are made and on whose stability 
 we fundamentally depend. Except for children (who 
 don’t know enough not to ask the important questions), 
 few of us spend much time wondering why nature is the 
 way it is; where the cosmos came from, or whether it is 
 always here; if time will one day flow backward and
 effects precede causes; or whether there are ultimate 
 limits to what humans can know.” 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

@SuppressWarnings("unused")
public class MainActivity extends Activity {

    public static Context context;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set main layout
		setContentView(R.layout.activity_main);
	    MainActivity.context = getApplicationContext();
		EditText edit = (EditText) findViewById(R.id.editText1);
		edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		AdView adview = (AdView)findViewById(R.id.adView);
		AdRequest req = new AdRequest();
		adview.loadAd(req);
	
	}

	// Create options menu with SHORTEN URL button (R.id.menu_go)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Set up actionbar nav
	    ActionBar actionBar = getActionBar();
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_go:
			PreHTTPGet();
			return true;

		case R.id.menu_email:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"monsterkittenthread@gmail.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "is.gd application");
			try {
			    startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
			    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.menu_git:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/monsterkitten"));
			startActivity(browserIntent);
			return true;
		case R.id.menu_play:
			Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.monsterkitten.is_gd"));
			startActivity(browserIntent1);	
			return true;
		case R.id.menu_license:
			Intent browserIntent11 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apache.org/licenses/LICENSE-2.0.txt"));
			startActivity(browserIntent11);	
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
    }

	
	public void PreHTTPGet() {

		// UrlValidator urlValidator = new UrlValidator();
		// urlValidator.isValid(edit.getText().toString());
		EditText edit = (EditText) findViewById(R.id.editText1);
		new GetData().execute(edit.getText().toString());

	}
	

	public static void done(String result) {
		
		if(result == "IOException") {
			Toast toast = Toast.makeText(getAppContext(), R.string.connectError, Toast.LENGTH_LONG);
			toast.show();
			
		} if (result =="MURLException") {
			
			Toast toast = Toast.makeText(getAppContext(), "Bad URL.", Toast.LENGTH_LONG);
			toast.show();
			
		} if (result == "IOExceptionOriginal") {
			
			Toast toast = Toast.makeText(getAppContext(), "The specified website does not exist or can't be reached.", Toast.LENGTH_LONG);
			toast.show();
			
		} else {
		
			//TODO: Fix clipboard
			//ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
			//clipboard.setText(result); //deprecated, but I can't be arsed to fix it
			Toast mainToast = Toast.makeText(getAppContext(), result, Toast.LENGTH_SHORT); /*replace result with R.string.copiedToClip*/
			mainToast.show();
		}
	}	
		
		////////////////////////////// HELPER METHODS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

			public static Context getAppContext() {
				return MainActivity.context;
			}
}

class GetData extends AsyncTask<String, String, String> {
	
    protected String doInBackground(String... params) {
    	   
    	//Make sure the site exists and the URL is well-formed
    	//The former is for the sake of is.gd
    	try {
   		    URL url = new URL(params[0]);
   		    @SuppressWarnings("unused")
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    		   
    	   } catch (MalformedURLException ex){
				return "MURLException";
			} catch (IOException ex) {
				return "IOExceptionOriginal";
			}
    	
	    try {
			// Create a URL for the desired page
			@SuppressWarnings("deprecation")
			String finalURL ="http://is.gd/create.php?format=simple&url=" + URLEncoder.encode(params[0]);
			Log.d("Monsterkitten", "Variable finalURL set. (" + finalURL + ")");
		    URL url = new URL(finalURL);

		    // Read all the text returned by the server
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    Log.d("Monsterkitten", "Stream opened");
		    String str;
		    while ((str = in.readLine()) != null) {
		    // str is one line of text; readLine() strips the newline character(s)
			    in.close();
			    Log.d("Monsterkitten", "HTTP code here");
			    Log.d("Monsterkitten", "Stream closed");
			    Log.d("Monsterkitten", "Short URL is '" + str +"'");
			    return str;
		    }
		    return str;
		} catch (IOException ex) {
			return "IOException";
		}
	}

    protected void onPostExecute(String result) {
 
    	MainActivity.done(result);
    	
    }

    
}