package com.example.exchange;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class RegisterFragment extends Fragment {

	View fragmentView;
	private MenuItem menuitem;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragmentView=inflater.inflate(R.layout.register_screen, container,false);
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
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		menuitem.setEnabled(true);
	}
}
