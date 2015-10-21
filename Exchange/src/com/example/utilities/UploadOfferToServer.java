package com.example.utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.dataobjects.Offer;
import com.example.exchange.MainPageListFragment;
import com.example.exchange.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.Browser.BookmarkColumns;
import android.support.v4.app.FragmentManager;
import android.util.Log;

/*
 * This Class is used to upload the offer to the Serve.
 * The details of the offer are stored in Json and sent to the webserver
 */
public class UploadOfferToServer extends AsyncTask<String, String, String>{
	Offer bookupload = new Offer();
	String encodedBitmapString;
	String name;
	String jsonInputString;
	double latitude;
	double longitude;
	
	private String targetUrl;
	URL urlToConnect;
	HttpURLConnection connection;
	ProgressDialog dialog;
	Context c;

	/*Constructor to set the context and the Offer object passed from the calling function */
	public UploadOfferToServer(Offer bookupload, Context c) {
		// TODO Auto-generated constructor stub
		this.bookupload = bookupload;
		this.c=c;
		dialog=new ProgressDialog(c);
		dialog.setMessage("Loading");
		dialog.setCancelable(false);
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		encodedBitmapString=BitmapUtilities.BitMapToString(bookupload.getBitmap());
		name = bookupload.getName();
		latitude = bookupload.getLatitude();
		longitude = bookupload.getLongitude();
		
		Log.d("dhana", "all json values"+name+latitude+longitude);
		JSONObject jsoninput = new JSONObject();
		try {
			jsoninput.put("bitmap", encodedBitmapString);
			jsoninput.put("name", name);
			jsoninput.put("latitude", latitude);
			jsoninput.put("longitude", longitude);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonInputString=jsoninput.toString();
		Log.d("dhana", "printing upload json"+jsonInputString);
		
		Log.d("dhana", "async background");
		this.targetUrl=params[0];
		Log.d("dhana", "background jsonString=" +jsoninput);
		try{
			urlToConnect= new URL(targetUrl);
			connection=(HttpURLConnection) urlToConnect.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
			
			/*Send Request*/
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(jsonInputString);
			wr.flush();
			wr.close();
			
			/*Get Response*/
			InputStream is= connection.getInputStream();
			BufferedReader rd=new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line=rd.readLine()) !=null){
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		finally{
			if(connection!=null){
				connection.disconnect();
			}
		}
	
		
		
	}
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Log.d("dhana", "result = "+result);
		
		if(dialog.isShowing()){
			dialog.dismiss();
		}
		
		
		final Activity activity = (Activity) c;
		android.app.FragmentManager fragmentManager = activity.getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		Fragment fragment = new MainPageListFragment();
		fragmentTransaction.replace(R.id.content_frame, fragment);
		fragmentTransaction.commit();
	}

}
