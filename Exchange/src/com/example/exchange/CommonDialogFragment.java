package com.example.exchange;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CommonDialogFragment extends DialogFragment implements View.OnClickListener{
	
	private String message;
	private String positive;
	private String negative;
	
	Button buttonPositive;
	Button buttonNegative;
	
	public interface CommonDialogListener{
		public void onDialogueNegativeClick(DialogFragment dialogue);
		public void onDialoguePositiveClick(DialogFragment dialogue);
	}
	public void setDialoginputs(String message, String negative, String positive) {
		// TODO Auto-generated constructor stub
		this.message=message;
		this.positive=positive;
		this.negative=negative;
		
	}
	
	CommonDialogListener mListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mListener=(CommonDialogListener) activity;
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog=super.onCreateDialog(savedInstanceState);
		dialog.setTitle(message);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.commondialoglayout, null);
		buttonNegative=(Button) view.findViewById(R.id.dialog_negative);
		buttonPositive=(Button) view.findViewById(R.id.dialog_positive);
		
		buttonNegative.setOnClickListener(this);
		buttonPositive.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		buttonNegative.setText(negative);
		buttonPositive.setText(positive);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.dialog_negative){
			dismiss();
			mListener.onDialogueNegativeClick(CommonDialogFragment.this);
		}else if(v.getId()==R.id.dialog_positive){
			dismiss();
			mListener.onDialoguePositiveClick(CommonDialogFragment.this);
		}
		
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		if(getDialog()!=null && getRetainInstance()){
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

}
