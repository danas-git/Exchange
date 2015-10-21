package com.example.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;

public class GetReverseGeoCoding {

	private String Address1, Address2, City, State, Country, County, PIN;

    public GetReverseGeoCoding(final double mLatitude,final double mLongitude) {
        Address1 = "";
        Address2 = "";
        City = "";
        State = "";
        Country = "";
        County = "";
        PIN = "";
        
     //   Log.d("dhana","reversegeocode lat:"+mLatitude+"lon:"+mLongitude);
        
        AsyncTask<String, String, String> asyncTask = new AsyncTask<String,String,String>() {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				 try {

			            JSONObject jsonObj = parser_Json.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?region=DE&language=de&latlng=" + mLatitude + ","
			                    + mLongitude + "&sensor=true");
			            String Status = jsonObj.getString("status");
			            if (Status.equalsIgnoreCase("OK")) {
			                JSONArray Results = jsonObj.getJSONArray("results");
			               // Log.d("dhana", "results"+Results.toString());
			                JSONObject zero = Results.getJSONObject(0);
			                JSONArray address_components = zero.getJSONArray("address_components");
			                for (int i = 0; i < address_components.length(); i++) {
			                    JSONObject zero2 = address_components.getJSONObject(i);
			                    String long_name = zero2.getString("long_name");
			                    JSONArray mtypes = zero2.getJSONArray("types");
			                    String Type = mtypes.getString(0);

			                    if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
			                    	
			                        if (Type.equalsIgnoreCase("street_number")) {
			                            Address1 = long_name + " ";
			                        } else if (Type.equalsIgnoreCase("route")) {
			                            Address1 = Address1 + long_name;
			                        } else if (Type.equalsIgnoreCase("sublocality")) {
			                            Address2 = long_name;
			                        } else if (Type.equalsIgnoreCase("locality")) {
			                            // Address2 = Address2 + long_name + ", ";
		                        	//Log.d("dhana", "citylong_name"+long_name);
			                            City = long_name;
			                            
			                        } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
			                            County = long_name;
			                        } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
			                            State = long_name;
			                        } else if (Type.equalsIgnoreCase("country")) {
			                            Country = long_name;
			                        } else if (Type.equalsIgnoreCase("postal_code")) {
			                            PIN = long_name;
			                        }
			                    }

			                    // JSONArray mtypes = zero2.getJSONArray("types");
			                    // String Type = mtypes.getString(0);
			                    // Log.e(Type,long_name);
			                }
			            }

			        } catch (Exception e) {
			            e.printStackTrace();
			        }
				return null;

			}
		};
		asyncTask.execute("parameter");

       
    }

    public String getAddress1() {
        return Address1;

    }

    public String getAddress2() {
        return Address2;

    }

    public String getCity() {
        return City;

    }

    public String getState() {
        return State;

    }

    public String getCountry() {
        return Country;

    }

    public String getCounty() {
        return County;

    }

    public String getPIN() {
        return PIN;

    }
}

