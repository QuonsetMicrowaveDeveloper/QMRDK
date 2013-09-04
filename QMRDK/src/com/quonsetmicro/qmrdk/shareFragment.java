package com.quonsetmicro.qmrdk;

import java.io.File;
import java.util.ArrayList;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class shareFragment extends Fragment {

	  String baseDir = Environment.getExternalStorageDirectory().toString();
	  final String rdkFolder = "QMRDK/Data";
	  final String sep = "/";
	  private String fileName;
	  public String[] fileArray;
	  public int[] positionChecked;
	  File folderFile;
	  int pos;
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState) {
	    // Inflate the layout for this fragment   	    
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.share_layout, container, false);
	}
	 
	@Override
	public void onResume(){
		super.onResume();

	//Get pathway to folder to upload from
	folderFile = new File(baseDir+sep+rdkFolder);
	
	//Create File array with a list of files inside save folder
	 File [] fileList = folderFile.listFiles();
		if(fileList != null){
			 fileArray = new String[fileList.length];
			 //Convert File array to String array	
			 for(int i=0; i<fileList.length; ++i){
					fileArray[i]=fileList[i].getName();
					}
				//Create adapter to pull files from array and put into list items on screen
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.activity_list, 
						fileArray);
				ListView listView = (ListView) getView().findViewById(R.id.filelistView);
				listView.setItemsCanFocus(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setAdapter(adapter);
				
				//Create an array to keep track of which files are chosen by user, initialize to all 0
				positionChecked = new int[fileArray.length];
				for(int count=0; count<fileArray.length; ++count){
					positionChecked[count]=0;
				}
				
				//Listen for user choosing files, update array that keeps track if chosen/unchosen
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View viewClicked, int position,
							long id) {
						
						TextView textView = (TextView)viewClicked;
						 if ((textView.getText() != null) && (positionChecked[position]==1)){
				             positionChecked[position]=0;
				             viewClicked.setBackgroundColor(0x00000000);
				         }
						 else if ((textView.getText() != null) && (positionChecked[position]==0)){	 
							 positionChecked[position]=1;
							 viewClicked.setBackgroundColor(0xff9bddff);
						 }
						 }
					}
				);				
	}	
		//Listen for "UPLOAD" button to be pressed, signaling user has finished choosing files to upload
		//and now needs to actually upload these files
		
		//Populate file list and send files to user-chosen application to be uploaded
		Button uploadbutton = (Button) getView().findViewById(R.id.uploadbutton);
		uploadbutton.setOnClickListener(new OnClickListener(){
		public void onClick (View v){	
			ArrayList<Uri> mainFileUri = new ArrayList<Uri>();
			for(int n=0; n<positionChecked.length; ++n){
				if(positionChecked[n] == 1){
					fileName = fileArray[n];
					String path = (baseDir+sep+rdkFolder+sep+fileName);
					mainFileUri.add(Uri.fromFile(new java.io.File(path)));	
				}	
			}
			if (mainFileUri.size()==1) {
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("text/plain");
				emailIntent.putExtra(Intent.EXTRA_STREAM, mainFileUri.get(0));
				startActivity(Intent.createChooser(emailIntent, "Choose How to Send File")); 	
			}
			else{
			Intent email_Intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
			email_Intent.setType("text/plain");
			email_Intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mainFileUri);
			startActivity(Intent.createChooser(email_Intent, "Choose How to Send File")); 
			}
		}
		});
	}
	
	//This needs to be here as a declaration from MainActivity, will not compile correctly otherwise
	public void parse(byte[] readBuf) {
		// TODO Auto-generated method stub
		
	}	
}	