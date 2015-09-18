package com.example.exchange;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class SingleRow {
	int image;
	String text;
	
	public SingleRow (int image, String text){
		this.image=image;
		this.text=text;
	}
}

class CustomListAdapter extends BaseAdapter {
	
	ArrayList<SingleRow> list;
	Context context;
	
	 CustomListAdapter(Context c) {
		// TODO Auto-generated constructor stub
		 context=c;
		 list=new ArrayList<SingleRow>();
		 String[] navtext=c.getResources().getStringArray(R.array.drawer_names);
		 int[] navimage={R.drawable.ic_drawer,R.drawable.ic_drawer,R.drawable.ic_drawer,R.drawable.ic_drawer,R.drawable.ic_drawer,R.drawable.ic_drawer,R.drawable.ic_drawer};
		 
		 for(int i=0;i<navimage.length;i++){
			 list.add(new SingleRow(navimage[i],navtext[i]));
		 }
	}
	 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.list_element, parent,false);
		ImageView image = (ImageView) row.findViewById(R.id.navimage);
		TextView text = (TextView) row.findViewById(R.id.navtext);
		
		SingleRow res = list.get(position);

		image.setImageResource(res.image);
		text.setText(res.text);
		return row;
	}

}
