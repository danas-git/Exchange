package com.example.exchange;

import com.example.utilities.Connectivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment  {
	
	TextView registerText;
	TextView forgotText;
	View fragmentView;
	private FragmentTransaction fragmentTransation;
	private MenuItem menuitem;
	private SignInButton signInButton;
	

	private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragmentView= inflater.inflate(R.layout.login_screen, container,false);

		return fragmentView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		menuitem = (MenuItem) Exchange.actionBar.findItem(R.id.action_sign_in);
		menuitem.setEnabled(false);
		
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		registerText=(TextView) fragmentView.findViewById(R.id.user_register_new);
		forgotText=(TextView) fragmentView.findViewById(R.id.user_forgot_pwd);
		registerText.setText(Html.fromHtml(getString(R.string.register)));
		forgotText.setText(Html.fromHtml(getString(R.string.forgot)));
		signInButton=(SignInButton) fragmentView.findViewById(R.id.google_sign_in);
		
		signInButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Connectivity.isConnected(getActivity())){
					Exchange.signInProgressDialog.setMessage("Signing in ...");
					Exchange.signInProgressDialog.show();
					Exchange.mSignInProgress=STATE_SIGN_IN;
					Exchange.googleApiClient.connect();
					Log.d("dhana", "onclickof google sign in");
				}else{
					Toast.makeText(getActivity(), "Could not connect to the Network", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		registerText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterFragment registerFragment= new RegisterFragment();
				fragmentTransation=getActivity().getFragmentManager().beginTransaction();
				fragmentTransation.replace(R.id.content_frame, registerFragment);
				fragmentTransation.addToBackStack(null);
				fragmentTransation.commit();
				
			}
		});
		
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		menuitem.setEnabled(true);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	
}
