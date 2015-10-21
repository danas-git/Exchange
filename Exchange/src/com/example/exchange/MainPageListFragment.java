package com.example.exchange;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.exchange.R.layout;
import com.example.imagefromUrl.ImageLoader;
import com.example.utilities.Connectivity;
import com.example.utilities.EndlessScrollListener;
import com.example.utilities.GetRecordsFromWeb;
import com.example.utilities.GetRecordsFromWeb.GetRecords;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainPageListFragment extends ListFragment implements GetRecords,SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener{
	
	private TextView textview;
	private View fragmentview;
	SwipeRefreshLayout swipelayout;
	private static final String url = "http://www.danas.comeze.com/androidexchange/getrecords.php";
	String jsonInputString;
	MainListAdapter mainListAdapter;
	ArrayList<adapterdata> adapterDataList;
	GetRecordsFromWeb getRecordsFromWeb;
	private MenuItem menuitem;
	SeekBar radiusBar;
	TextView radiusTextView;
	int localPage=1;
	String EMPTY[] ={};
	ListView ls;

	private SharedPreferences sharedpreferences;
	private Editor editor;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = sharedpreferences.edit();
		adapterDataList=new ArrayList<adapterdata>();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragmentview=inflater.inflate(R.layout.main_page_list, container,false);
		swipelayout = (SwipeRefreshLayout) fragmentview.findViewById(R.id.swipelayoutmainlist);
		swipelayout.setOnRefreshListener(this);
		
		ls = (ListView) fragmentview.findViewById(android.R.id.list);
		TextView tv = (TextView) fragmentview.findViewById(android.R.id.empty);
		
		/*if this fragment is not called for first time then it is called due to configuration
		 * changes. Hence try to reload data saved on saveinstance
		 */
		if(savedInstanceState!=null){
			Log.d("dhana", "inside savedinstance");
			adapterDataList=savedInstanceState.getParcelableArrayList("mainlistdata");
			setListAdapter(mainListAdapter);
			if(adapterDataList.isEmpty()){
				swipelayout.setVisibility(View.GONE);
			}
		}
		else {
			if(Connectivity.isConnected(getActivity())){

				loadNewData(ls);
			}
			else{
					Log.d("dhana", "empty data list");

					Toast.makeText(getActivity(), "Could not connect to the Network", Toast.LENGTH_LONG).show();
					swipelayout.setVisibility(View.GONE);			
			}
		}

		return fragmentview;
	}

	private void loadNewData(ListView ls) {
		adapterDataList.clear();
		JSONObject jsoninput = new JSONObject();
		try {
			jsoninput.put("page", String.valueOf(localPage));
			jsoninput.put("latitude",sharedpreferences.getString("latitude", "0"));
			jsoninput.put("longitude",sharedpreferences.getString("longitude", "0"));
			jsoninput.put("radius", sharedpreferences.getInt("radius", 0));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonInputString=jsoninput.toString();
		Log.d("dhana", "before calling getrecords url="+url+"json"+jsonInputString);
		
		Log.d("dhana", "asynctask call from main");
		getRecordsFromWeb=new GetRecordsFromWeb(new GetRecords() {
			
			@Override
			public void returnUpdatedRecords(String response) {
				// TODO Auto-generated method stub
				if(response!=null){
				parseAndNotifyAdapter(response);
				}
				
			}
		}, getActivity());
		getRecordsFromWeb.execute(jsonInputString,url); 


		ls.setOnScrollListener(new EndlessScrollListener() {
		
		@Override
		public void onLoadMore(int page, int totalItemsCount) {
			// TODO Auto-generated method stub
			Log.d("dhana", "page "+page +" totalcount "+totalItemsCount);
			localPage=page;
			JSONObject jsoninput = new JSONObject();
			try {
				jsoninput.put("page", String.valueOf(localPage));
				jsoninput.put("latitude",Exchange.latitude);
				jsoninput.put("longitude", Exchange.longitude);
				jsoninput.put("radius", sharedpreferences.getInt("radius", 0));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonInputString=jsoninput.toString();
			if(getRecordsFromWeb!=null && getRecordsFromWeb.getStatus() == AsyncTask.Status.RUNNING){
				Log.d("dhana", "Async Task running - from onloadmore");
			}
			else{
				Log.d("dhana", "async task call from onloadmore");
				getRecordsFromWeb=new GetRecordsFromWeb(new GetRecords() {
					
					@Override
					public void returnUpdatedRecords(String response) {
						// TODO Auto-generated method stub
						if(response!=null){
							parseAndNotifyAdapter(response);
						}
						
					}
				}, getActivity());
				getRecordsFromWeb.execute(jsonInputString,url); 
				}
			
			}
		});
}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Exchange.signInProgressDialog.isShowing()){
			Exchange.signInProgressDialog.dismiss();
		}
		
	}
		
	public void parseAndNotifyAdapter(String response){
		
		Log.d("dhana", "response after distance"+response);
		JSONObject jsonObjectResponse;
		
		try {
			JSONObject jsonResponse= new JSONObject(response);
			
			JSONArray jArray=jsonResponse.getJSONArray("data");
			for (int i=0;i<jArray.length();i++){
				adapterdata adapterData=new adapterdata();
				jsonObjectResponse = jArray.getJSONObject(i);
				
				adapterData.bookName=jsonObjectResponse.getString("bookname");
				adapterData.imageurl=jsonObjectResponse.getString("imageurl");
				Log.d("dhana", "Distance="+jsonObjectResponse.getInt("distance"));
				if((jsonObjectResponse.getInt("distance")>0)){
					adapterData.distance=jsonObjectResponse.getInt("distance");
				}
				else{
					adapterData.distance=1;
				}
				adapterDataList.add(adapterData);
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(adapterDataList.isEmpty()){
			swipelayout.setVisibility(View.GONE);
		}else{
			mainListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("mainlistdata", adapterDataList);
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mainListAdapter = new MainListAdapter(getActivity());
		setListAdapter(mainListAdapter);
		getListView().setOnItemClickListener(this);
		radiusBar = (SeekBar) fragmentview.findViewById(R.id.seekBar1);
		radiusBar.setProgress(sharedpreferences.getInt("radius", 0));
		radiusTextView=(TextView) fragmentview.findViewById(R.id.seekbarradiustext);
		if(radiusBar.getProgress() == 0){
			radiusTextView.setText("Radius: (All)");
		}else{
			radiusTextView.setText("Radius: ("+radiusBar.getProgress()+")Km");
		}
		radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			int progresschanged=0;
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				editor.putInt("radius", progresschanged);
				editor.commit();
				localPage=1;
				if(Connectivity.isConnected(getActivity())){
				adapterDataList.clear();
				mainListAdapter= new MainListAdapter(getActivity());
				setListAdapter(mainListAdapter);
				loadNewData(ls);
				}
				else{
					Toast.makeText(getActivity(), "Could not connect to the Network", Toast.LENGTH_LONG).show();
				}
				
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(progress==0){
					radiusTextView.setText("Radius: (All)");
				}else{
					radiusTextView.setText("Radius: ("+progress+")Km");
				}
				progresschanged=progress;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		adapterdata onclickadapter = adapterDataList.get(position);
		Toast.makeText(getActivity(), "Clicked"+onclickadapter.bookName, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		localPage=1;
		if(Connectivity.isConnected(getActivity())){
		adapterDataList.clear();
		mainListAdapter= new MainListAdapter(getActivity());
		setListAdapter(mainListAdapter);
		loadNewData(ls);
		}
		else{
			Toast.makeText(getActivity(), "Could not connect to the Network", Toast.LENGTH_LONG).show();
		}
		swipelayout.setRefreshing(false);
		
	}
	public class MainListAdapter extends BaseAdapter {

		Context context;
		ImageLoader imageLoader;
		int loader;
		
		public MainListAdapter(Context c) {
			// TODO Auto-generated constructor stub
			context=c;
			imageLoader=new ImageLoader(c);
			loader=R.drawable.ic_launcher;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return adapterDataList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			adapterdata viewData= adapterDataList.get(position);
			View row = inflater.inflate(R.layout.mainlistviewelement, parent,false);
			
			TextView bookNameText= (TextView) row.findViewById(R.id.mainlistviewname);
			ImageView imageView = (ImageView) row.findViewById(R.id.mainlistimageView);
			TextView radiusText=(TextView) row.findViewById(R.id.mainlistviewradius);
			imageLoader.DisplayImage(viewData.imageurl, loader, imageView);
			bookNameText.setText(viewData.bookName);
			radiusText.setText("Within "+viewData.distance+" Km");
			return row;

		}

	}



	@Override
	public void returnUpdatedRecords(String response) {
		// TODO Auto-generated method stub

		parseAndNotifyAdapter(response);
	}


}
class adapterdata implements Parcelable{
	String bookName;
	String imageurl;
	int distance;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public adapterdata() {
		// TODO Auto-generated constructor stub
	}
	public adapterdata(Parcel in) {
		// TODO Auto-generated constructor stub
		bookName=in.readString();
		imageurl=in.readString();
		distance=in.readInt();
	}
	
	public static final Parcelable.Creator<adapterdata> CREATOR
    = new Parcelable.Creator<adapterdata>() {
public adapterdata createFromParcel(Parcel in) {
    return new adapterdata(in);
}

public adapterdata[] newArray(int size) {
    return new adapterdata[size];
}
};
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bookName);
		dest.writeString(imageurl);
		dest.writeInt(distance);
		// TODO Auto-generated method stub
		
	}
}
