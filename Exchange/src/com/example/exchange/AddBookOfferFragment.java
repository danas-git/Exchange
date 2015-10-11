package com.example.exchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.dataobjects.Offer;
import com.example.utilities.BitmapUtilities;
import com.example.utilities.Connectivity;
import com.example.utilities.UploadOfferToServer;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddBookOfferFragment extends Fragment {
	
	private View fragmentView;
	private MenuItem menuitem;
	private ImageButton cameraImageButton;
	private ImageView imageView;
	private TextView bookNameText;
	private Button addBookButton;
	Uri outputFileUri;
	String bitmapPath;
	Bitmap rotationUpdatedBitmap;
	Offer bookOffer = new Offer();
	private static final String urlAddOffer = "http://www.danas.comeze.com/androidexchange/addoffer.php";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d("dhana", "oncreateaddbook");
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
		Log.d("dhana", "oncreateviewaddbook");
		return fragmentView;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("dhana", "insideonresume of fragment");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize=8;
		if(bitmapPath !=null){

			Log.d("dhana","came to onresume uri not null");


			Log.d("dhana","came to fragment camera result 3" +bitmapPath);
			final Bitmap bitmap =BitmapFactory.decodeFile(bitmapPath,options);
			rotationUpdatedBitmap = rotateBitmap(bitmap);
			Log.d("dhana","came to fragment camera result 4");
			imageView.setImageBitmap(rotationUpdatedBitmap);
		}
		
	} 
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState!=null){
			Log.d("dhana", "retainsavedinstance");
			String imageuri = savedInstanceState.getString("imageuri");
			bitmapPath=savedInstanceState.getString("bitmapPath");
			if(imageuri!=null){
				Log.d("dhana", "uriafter saveinstance" + imageuri);
				outputFileUri = Uri.parse(imageuri);
			}
		}
		bookNameText = (TextView) fragmentView.findViewById(R.id.addbook_book_name);
		imageView = (ImageView) fragmentView.findViewById(R.id.addbook_imageViewUpload);
		cameraImageButton = (ImageButton) fragmentView.findViewById(R.id.addbook_imageButtonUpload);
		cameraImageButton.setOnClickListener(new View.OnClickListener() {								@Override
			public void onClick(View v) {
			openImageIntent();
			}
		});
		addBookButton=(Button) fragmentView.findViewById(R.id.addbook_button_add);
		addBookButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Connectivity.isConnected(getActivity())){
					Bitmap uploadBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
					String uploadbookName = bookNameText.getText().toString();
					bookOffer.setName(uploadbookName);
					bookOffer.setBitmap(uploadBitmap);
					Log.d("dhana", "upload book name");
					UploadOfferToServer upload = new UploadOfferToServer(bookOffer, getActivity());
					upload.execute(urlAddOffer);

					String tempcity2 = Exchange.getAddress(getActivity());
					Toast.makeText(getActivity(), "City:"+tempcity2, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getActivity(), "Could not connect to the Network", Toast.LENGTH_LONG).show();
				}
			

			}
		});
			
		super.onActivityCreated(savedInstanceState);
		Log.d("dhana", "onactivitycreatedaddbook");
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if(outputFileUri !=null){
			Log.d("dhana", "onsaveinstance" +bitmapPath);
		outState.putString("imageuri", outputFileUri.toString());
		}
		if(bitmapPath!=null){
			outState.putString("bitmapPath", bitmapPath);
		
		}
	}
	
	private void openImageIntent(){

	    // Determine Uri of camera image to save.
	    final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "amfb" + File.separator);
	    root.mkdir();
	    final String fname = "img_" + System.currentTimeMillis() + ".jpg";
	    final File sdImageMainDirectory = new File(root, fname);
	    outputFileUri = Uri.fromFile(sdImageMainDirectory);

	    // Camera.
	    final  List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    final PackageManager packageManager = getActivity().getPackageManager();
	    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
	    for (ResolveInfo res : listCam){
	        final String packageName = res.activityInfo.packageName;
	        final Intent intent = new Intent(captureIntent);
	        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        intent.setPackage(packageName);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        cameraIntents.add(intent);
	    }
	     
	    if(outputFileUri == null){
	    	Log.d("dhana", "outfile uri null");
	    }else{
	    	Log.d("dhana","uri" + outputFileUri.toString());
	    }
	    //FileSystem
	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/");
	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

	    // Chooser of filesystem options.
	    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
	    // Add the camera options.
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
	    startActivityForResult(chooserIntent, 5);

	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("dhana","came to fragment camera result" + requestCode);
		if (data !=null){
			Log.d("dhana", "inside activityresult fragment data null");
			String action = data.getAction();
			outputFileUri = data.getData();
			Log.d("dhana", "gallery uri" + outputFileUri);
			try {
				String [] filePathColumn={MediaStore.Images.Media.DATA};
				Cursor cursor = getActivity().getContentResolver().query(outputFileUri, filePathColumn, null,null,null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				bitmapPath = cursor.getString(columnIndex);
				cursor.close();
				Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
				rotationUpdatedBitmap = rotateBitmap(bitmap);
				Log.d("dhana", "setting rotated image gallery");
				imageView.setImageBitmap(rotationUpdatedBitmap);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("dhana","came to fragment camera result not null" + action);
			
		}else{
			Log.d("dhana", "inside activityresult fragment null");
			Uri selectedImageURI;
			selectedImageURI = outputFileUri;
			Log.d("dhana", "before problem");
			bitmapPath = outputFileUri.getPath();
			Log.d("dhana", "after problem");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize=8;
			if(selectedImageURI ==null){

				Log.d("dhana","came to fragment camera result uri null");
			}

			
			final Bitmap bitmap =BitmapFactory.decodeFile(selectedImageURI.getPath(),options);

			Log.d("dhana","came to fragment camera result 4");
			rotationUpdatedBitmap = rotateBitmap(bitmap);
			Log.d("dhana", "setting rotated image");
			imageView.setImageBitmap(rotationUpdatedBitmap);
			
			
		}
		Log.d("dhana","came to fragment camera result end");
	}
	
	public Bitmap rotateBitmap(Bitmap bitmap){
		ExifInterface exif = null;
		try{
			exif=new ExifInterface(outputFileUri.getPath());
			Log.d("dhana", "inside if of exif"+ outputFileUri.getPath());
		}catch (IOException e){
			e.printStackTrace();
		}
		
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
		Log.d("dhana", "orientation = "+ orientation);
		Matrix matrix = new Matrix();
		switch (orientation){
			case ExifInterface.ORIENTATION_NORMAL:
				Log.d("dhana", "orientation case1");
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	            matrix.setScale(-1, 1);
	            Log.d("dhana", "orientation case2");
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            matrix.setRotate(180);
	            Log.d("dhana", "orientation case3");
	            break;
	        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	            matrix.setRotate(180);
	            Log.d("dhana", "orientation case4");
	            matrix.postScale(-1, 1);
	            break;
	        case ExifInterface.ORIENTATION_TRANSPOSE:
	            matrix.setRotate(90);
	            Log.d("dhana", "orientation case5");
	            matrix.postScale(-1, 1);
	            break;
	       case ExifInterface.ORIENTATION_ROTATE_90:
	           matrix.setRotate(90);
	           Log.d("dhana", "orientation case6");
	           break;
	       case ExifInterface.ORIENTATION_TRANSVERSE:
	           matrix.setRotate(-90);
	           Log.d("dhana", "orientation case7");
	           matrix.postScale(-1, 1);
	           break;
	       case ExifInterface.ORIENTATION_ROTATE_270:
	           matrix.setRotate(-90);
	           break;
	       default:
	           return bitmap;
		}
		try{
			Bitmap bmRotated = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
			bitmap.recycle();
			Log.d("dhana", "return rotated image");
			return bmRotated;
		}catch(OutOfMemoryError e){
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//menuitem.setVisible(true);
	}
}

