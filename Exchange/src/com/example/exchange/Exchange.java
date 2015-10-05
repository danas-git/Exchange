package com.example.exchange;

import javax.sql.CommonDataSource;

import com.example.exchange.CommonDialogFragment.CommonDialogListener;
import com.example.utilities.Connectivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Email;
//import android.service.carrier.CarrierMessagingService.ResultCallback;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Exchange extends Activity implements ConnectionCallbacks,
OnConnectionFailedListener,CommonDialogListener {
	private String[] msamples;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private CharSequence mDrawTitle;
	public static Menu actionBar;
	private MenuItem menuItem;
	private MenuItem gItem;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private Fragment fragment;
	private static boolean showmenuitems=true;
	static GoogleApiClient googleApiClient;
	private MenuItem signinItem;
	private SharedPreferences sharedpreferences;
	private Editor editor;
	static ProgressDialog signInProgressDialog;

	private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    static int mSignInProgress;
    private PendingIntent mSignInIntent;
    private int mSignInError;
	//private GoogleApiClient googleApiClient;
	static final int RC_SIGN_IN = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("dhana", "ACTIVITY oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedpreferences=PreferenceManager.getDefaultSharedPreferences(this);
		editor=sharedpreferences.edit();
		
		signInProgressDialog= new ProgressDialog(this);
		signInProgressDialog.setMessage("Signing in ...");
		
		msamples=getResources().getStringArray(R.array.drawer_names);
		mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
		
		/*List view for the navigation drawer*/
		mDrawerListView=(ListView) findViewById(R.id.left_drawer);
		mTitle=mDrawTitle=getTitle();
		if(actionBar!=null){
		menuItem=actionBar.findItem(R.id.action_sign_in);
		}
		
		
		/*Navigation Drawer*/
		mDrawerListView.setAdapter(new CustomListAdapter(this));
		NavigationListListener listener = new NavigationListListener();
		mDrawerListView.setOnItemClickListener(listener);
		mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_closed){
			@Override
			public void onDrawerOpened(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mTitle);
				showmenuitems=false;
				/*recreate action bar and set items invisible*/
				invalidateOptionsMenu();
				
			}
			
			@Override
			public void onDrawerClosed(View drawerView) {
				// TODO Auto-generated method stub
				super.onDrawerClosed(drawerView);
				getActionBar().setTitle(mDrawTitle);
				showmenuitems=true;
				/*recreate action bar and set items visible*/
				invalidateOptionsMenu();
			}
			
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		
		/*Call for Main page list fragment on first time loading*/
		if(savedInstanceState==null){
			fragmentManager=getFragmentManager();
			fragmentTransaction=fragmentManager.beginTransaction();
			fragment = new MainPageListFragment();
			fragmentTransaction.replace(R.id.content_frame, fragment);
			fragmentTransaction.commit();
		}
		

		googleApiClient = buildGoogleApiClient();
	}
	
	private GoogleApiClient buildGoogleApiClient() {
		// TODO Auto-generated method stub
		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN);
		return builder.build();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(Connectivity.isConnected(this)){
			googleApiClient.connect();
		}else{
			Toast.makeText(this, "Could not connect to the Network", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(Connectivity.isConnected(this)){
			if(googleApiClient.isConnected()){
				googleApiClient.disconnect();
			}
		}
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
		{
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}
		else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		actionBar=menu;
		
		/*Make the items visible on Action Bar*/
		for (int i=0; i< menu.size();i++){
			menu.getItem(i).setVisible(showmenuitems);
		}
		signinItem = actionBar.findItem(R.id.action_sign_in);

		/*set the signin item in status bar based on preference value */
		String loggedstatus = sharedpreferences.getString("loggedin", "false");
		Log.d("dhana", "activity on create logged in status= " +loggedstatus +sharedpreferences.getString("loggedin", "default"));
		if(loggedstatus.equals("true")){
			Log.d("dhana", "oncreateoptions true");
			signinItem.setTitle(R.string.sign_out);
		}else if(loggedstatus.equals("false")){
			Log.d("dhana", "oncreateoptions false");
			signinItem.setTitle(R.string.sign_in);
		}
		
		return true;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		
		gItem=item;
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == R.id.action_sign_in){
			/*check if the string at the item is Sign in / sign out */
			if (item.getTitle().equals(getResources().getString(R.string.sign_out))){
				signInProgressDialog.setMessage("Signing out");
				signInProgressDialog.show();
				if(Connectivity.isConnected(this)){
					if(googleApiClient.isConnected()){
						Plus.AccountApi.clearDefaultAccount(googleApiClient);
						googleApiClient.disconnect();
						
					}
					Log.d("dhana", "sharedoptionsvalueduring disconnect = " +sharedpreferences.getString("loggedin", "default"));
					
					editor.putString("loggedin", "false");
					editor.commit();
					signinItem.setTitle(R.string.sign_in);

					fragmentManager=getFragmentManager();
					fragmentTransaction=fragmentManager.beginTransaction();
					fragment = new MainPageListFragment();
					fragmentTransaction.replace(R.id.content_frame, fragment,"main_page_fragment");
					fragmentTransaction.commit();
				}else{
					signInProgressDialog.dismiss();
					Toast.makeText(this, "Could not connect to the Network", Toast.LENGTH_LONG).show();
				}
				
			}
			else{
				LoginFragment loginFragment= new LoginFragment();
				fragmentTransaction=getFragmentManager().beginTransaction();
				fragmentTransaction.replace(R.id.content_frame, loginFragment,"login_fragment");
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
				
			}
		}
		else if(id == R.id.action_new){
			CommonDialogFragment dialogue = new CommonDialogFragment();
			dialogue.setDialoginputs("Add Offer", "Book", "Other Items");
			dialogue.show(getFragmentManager(), "addofferdialogue");
			
		}
		return super.onOptionsItemSelected(item);
	}
	public class NavigationListListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Toast.makeText(getBaseContext(), "Clicked at position" + position,100).show();
			if(position==0){
				fragmentManager=getFragmentManager();
				fragmentTransaction=fragmentManager.beginTransaction();
				fragment = new MainPageListFragment();
				fragmentTransaction.replace(R.id.content_frame, fragment,"main_page_fragment");
				fragmentTransaction.commit();
			
			}
			mDrawerListView.setItemChecked(position, true);
			mTitle=msamples[position];
			getActionBar().setTitle(mTitle);
			if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
			{
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
			
		}

	}
	@Override
	public void onDialogueNegativeClick(DialogFragment dialogue) {
		// TODO Auto-generated method stub

		AddBookOfferFragment addBookOffer= new AddBookOfferFragment();
		fragmentTransaction=getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.content_frame,addBookOffer );
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	public void onDialoguePositiveClick(DialogFragment dialogue) {
		// TODO Auto-generated method stub

		AddItemOfferFragment addItemOffer= new AddItemOfferFragment();
		fragmentTransaction=getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.content_frame,addItemOffer );
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("dhana","came to activity onresult" + requestCode);

		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case 5:
			if(resultCode == RESULT_OK){
				Log.d("dhana","forcamera");
				break;
			}
        case RC_SIGN_IN:
            if (resultCode == RESULT_OK) {
                // If the error resolution was successful we should continue
                // processing errors.
                mSignInProgress = STATE_SIGN_IN;
            } else {
                // If the error resolution was not successful or the user canceled,
                // we should stop processing errors.
                mSignInProgress = STATE_DEFAULT;
            }

            if (!googleApiClient.isConnecting()) {
                // If Google Play services resolved the issue with a dialog then
                // onStart is not called so we need to re-attempt connection here.
            	if(Connectivity.isConnected(this)){
            		googleApiClient.connect();
            	}else{
            		Toast.makeText(this, "Could not connect to the Network", Toast.LENGTH_LONG).show();
            	}
            }
            break;
    }
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
		Log.d("dhana", "on connectionfailed");
		if(result.getErrorCode() == ConnectionResult.API_UNAVAILABLE){
			Log.d("dhana", "API Unavailable");
			
		}else if(mSignInProgress!=STATE_IN_PROGRESS){
			mSignInIntent = result.getResolution();
			mSignInError = result.getErrorCode();
			
			Log.d("dhana", "else part of api");
			
			if(mSignInProgress == STATE_SIGN_IN){
				Log.d("dhana", "if part of api");
				resolveSignInError();
			}
		}
		
	}
	
	private void resolveSignInError() {
		// TODO Auto-generated method stub
		if(mSignInIntent!=null){
			try{
				mSignInProgress= STATE_IN_PROGRESS;
				startIntentSenderForResult(mSignInIntent.getIntentSender(),RC_SIGN_IN,null,0,0,0);
			}catch (SendIntentException e){
				Log.d("dhana", "sign in intent could not be sent");
				mSignInProgress=STATE_SIGN_IN;
				if(Connectivity.isConnected(this)){
					googleApiClient.connect();
				}else{
					Toast.makeText(this, "Could not connect to the Network", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
	
	

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
		Log.d("dhana", "onconnected:");
		
		editor.putString("loggedin", "true");
		editor.commit();

		Log.d("dhana", "onconnected2:");
		signinItem.setEnabled(true);
		signinItem.setTitle(R.string.sign_out);

		Log.d("dhana", "onconnected3:");
		Person currentUser = Plus.PeopleApi.getCurrentPerson(googleApiClient);

		Log.d("dhana", "onconnected4:");
		String displayname= String.format("Signed in as", currentUser.getDisplayName());

		Log.d("dhana", "onconnected5:");
		
		Log.d("dhana", currentUser.getDisplayName() + Plus.AccountApi.getAccountName(googleApiClient));
		
		mSignInProgress=STATE_DEFAULT;

		/*if the onconnected is called from login fragment then load the mainpagelistfragment
		 * Else onconnected is called due to orientation change and hence current fragment 
		 * is retained*/

		Log.d("dhana", "onconnected6:");
		if(signInProgressDialog.isShowing()){
			signInProgressDialog.dismiss();

			Log.d("dhana", "onconnected7:");

			android.app.FragmentManager fragmentManager=getFragmentManager();
			FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
			Fragment fragment = new MainPageListFragment();
			fragmentTransaction.replace(R.id.content_frame, fragment);
			fragmentTransaction.commit();
		}

		Log.d("dhana", "onconnected8:");
		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		if(Connectivity.isConnected(this)){
			googleApiClient.connect();
		}else{
			Toast.makeText(this, "Could not connect to the Network", Toast.LENGTH_LONG).show();
		}
		
	}
}
