package com.monsterkitten.is_gd;

/* 
 "We go about our daily lives understanding almost
 nothing of the world. We give little thought to the 
 machinery that generates the sunlight that makes life 
 possible, to the gravity that glues us to an Earth that 
 would otherwise send us spinning off into space, or to 
 the atoms of which we are made and on whose stability 
 we fundamentally depend. Except for children (who 
 don't know enough not to ask the important questions), 
 few of us spend much time wondering why nature is the 
 way it is; where the cosmos came from, or whether it is 
 always here; if time will one day flow backward and
 effects precede causes; or whether there are ultimate 
 limits to what humans can know."
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

@SuppressWarnings("unused")
public class MainActivity extends Activity {

    public static Context context;
    static String shortURLv;
    static ProgressDialog dialog;
    static ProgressBar mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        EditText edit = (EditText) findViewById(R.id.editText1);
        edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        EditText edit1 = (EditText) findViewById(R.id.editText2);
        edit1.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest req = new AdRequest();
        adview.loadAd(req);
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);
        mProgress.setVisibility(View.INVISIBLE);

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
                String versionName = null;
                try {
                versionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException ex) {
                    //This will never happen
            }
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"monsterkittenthread@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "is.gd application v" + versionName);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getAppContext(), R.string.noEC, Toast.LENGTH_SHORT).show();
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

    public static void done(String result) {

        mProgress.setVisibility(View.INVISIBLE);
        if (result == "IOException") {
            Toast toast = Toast.makeText(getAppContext(), R.string.connectError, Toast.LENGTH_LONG);
            toast.show();

        }
        if (result == "MURLException") {

            Toast toast = Toast.makeText(getAppContext(), R.string.badURL, Toast.LENGTH_LONG);
            toast.show();

        }
        if (result == "IOExceptionOriginal") {

            Toast toast = Toast.makeText(getAppContext(), R.string.doesNotExist, Toast.LENGTH_LONG);
            toast.show();

        }
        if (result == "pleaseWait"){

            Toast toast = Toast.makeText(getAppContext(), R.string.wait, Toast.LENGTH_LONG);
            toast.show();

        }
        if (result == "badShortURL"){

            Toast toast = Toast.makeText(getAppContext(), R.string.URLTaken, Toast.LENGTH_LONG);
            toast.show();

        }
        if ((!(result.equals("IOException"))) && (!(result.equals("MURLException"))) && (!(result.equals("IOExceptionOriginal")))) {
            finish(result);
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public void PreHTTPGet() {
        // UrlValidator urlValidator = new UrlValidator();
        // urlValidator.isValid(edit.getText().toString());
        mProgress.setVisibility(View.VISIBLE);
        EditText longURL = (EditText) findViewById(R.id.editText1);
        EditText shortURL = (EditText) findViewById(R.id.editText2);
        shortURLv = shortURL.getText().toString();
        int length = shortURLv.length();
        Log.d("Monsterkitten", "Length is " + length);
        if ((length < 31) || (length > 4) || (length == 0)) {
            new GetData().execute(longURL.getText().toString(), shortURLv);

        } else {
            Toast toast = Toast.makeText(getAppContext(), R.string.badLength, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void finish(String result) {

        if (((result.equals("http://is.gd/" + shortURLv)) || (shortURLv.equals("")))) {
            Context mainAppContext = getAppContext();                                                          //
            ClipboardManager clipboard = (ClipboardManager) mainAppContext.getSystemService(CLIPBOARD_SERVICE);//
            Uri shortenedUri = Uri.parse(result);                                                              // THANK YOU DEVNULL
            ClipData clipUri = ClipData.newRawUri("is.gd", shortenedUri);                                      //
            clipboard.setPrimaryClip(clipUri);                                                                 //
            Toast mainToast = Toast.makeText(getAppContext(), R.string.copiedToClip, Toast.LENGTH_SHORT);
            mainToast.show();
            mProgress.setVisibility(View.INVISIBLE);
        } else {
            mProgress.setVisibility(View.INVISIBLE);
            Log.d("Monsterkitten", "URL taken");
            Toast toast = Toast.makeText(getAppContext(), R.string.URLTaken, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

class GetData extends AsyncTask<String, String, String> {

    ProgressDialog dialog;

    Toast msg;

    protected String doInBackground(String... params) {

        String shorturl = "";
        if (!(params[1].equals(""))) {
            Log.d("Monsterkitten", "Params[1] is '" + params[1] + "'");
            try {
            shorturl = "&shorturl=" + URLEncoder.encode(params[1], "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Log.d("Monsterkitten", "UnsupportedEncodingException: " + ex);
            }
        }
        //Make sure the site exists and the URL is well-formed
        //The former is for the sake of is.gd
        try {
            URL url = new URL(params[0]);
            @SuppressWarnings("unused")
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        } catch (MalformedURLException ex) {
            return "MURLException";
        } catch (IOException ex) {
            return "IOExceptionOriginal";
        }


        /*  Response Codes

            400 Bad Request - error code 1 - Problem with original long URL
            406 Not Acceptable - error code 2 - Problem with short URL
            502 Bad Gateway - error code 3 - Rate limit exceeded, wait before trying again
            503 Service Unavailable - error code 4  - Service error, maintenance
        */

        try {
            // Create a URL for the desired page
            String finalURL = "http://is.gd/create.php?format=simple&url=" + URLEncoder.encode(params[0], "UTF-8") + shorturl;
            Log.d("Monsterkitten", "Variable finalURL set. (" + finalURL + ")");
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(finalURL);
            HttpResponse response = client.execute(request);

            // Get the response
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                int code = response.getStatusLine().getStatusCode();
                Log.d("Monsterkitten", "HTTP code is " + code);
                if(code == 400) {
                    return "MURLException";
                }
                if(code == 406) {
                    return "badShortURL";
                }
                if(code == 502) {
                    return "pleaseWait";
                }
                if((code == 503)||(code == 404)) {
                    return "IOException";
                }
                return line;
            }
            return line;
        } catch (IOException ex) {
            return "IOException";
        }
    }

    protected void onPostExecute(String result) {
        MainActivity.done(result);

    }


}
