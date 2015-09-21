package com.example.exchange;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddBookOfferFragment extends Fragment {
	
	private View fragmentView;
	private MenuItem menuitem;
	private ImageButton cameraImageButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		menuitem = (MenuItem) Exchange.actionBar.findItem(R.id.action_new);
		menuitem.setVisible(false);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragmentView= inflater.inflate(R.layout.add_offer_book_screen, container,false);
		return fragmentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		cameraImageButton = (ImageButton) fragmentView.findViewById(R.id.addbook_imageButtonUpload);
		cameraImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Camera intent will be added shortly", Toast.LENGTH_SHORT).show();
				
			}
		});
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		menuitem.setVisible(true);
	}
}
