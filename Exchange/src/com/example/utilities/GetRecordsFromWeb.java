package com.example.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.message.BufferedHeader;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Telephony.Sms.Conversations;
import android.util.Log;
import android.widget.ProgressBar;

public class GetRecordsFromWeb extends AsyncTask<String, String, String> {
	
	private GetRecords getRecords;
	private String targetUrl;
	URL urlToConnect;
	HttpURLConnection connection;
	ProgressDialog dialog;
	
	Context c;
	public interface GetRecords{
		public void returnUpdatedRecords(String response);
	}
	
	public GetRecordsFromWeb(GetRecords getdata,Context c) {
		//Log.d("dhana", "async constructor url="+url);
		// TODO Auto-generated constructor stub
		this.getRecords=getdata;
		this.c=c;
		dialog=new ProgressDialog(c);
		dialog.setMessage("Loading");
		dialog.setCancelable(false);
		
		//this.targetUrl=url;
		//this.c=c;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		
		
		Log.d("dhana", "async background");
		String jsoninput = params[0];
		this.targetUrl=params[1];
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
			wr.writeBytes(jsoninput);
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
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		getRecords.returnUpdatedRecords(result);
		
		if(dialog.isShowing()){
			dialog.dismiss();
		}
		
	}

}
