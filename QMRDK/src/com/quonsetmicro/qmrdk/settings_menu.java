package com.quonsetmicro.qmrdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class settings_menu extends PreferenceActivity implements OnSharedPreferenceChangeListener,
		listFragment.OnPreferenceAttachedListener{
	public static final String KEY_LIST_PREFERENCE = "sweeper";
	public static final String KEY_LIST_PREFERENCE1="defaults";
	public static final String KEY_EDITTEXT_PREFERENCE = "rampTimeEntered";
    public static final String KEY_EDITTEXT_PREFERENCE1 = "refDividerEntered";
    public static final String KEY_EDITTEXT_PREFERENCE2 = "startFreqEntered";
    public static final String KEY_EDITTEXT_PREFERENCE3 = "stopFreqEntered";
    private EditTextPreference mEditTextPreference;
    private EditTextPreference mEditTextPreference1;
    private EditTextPreference mEditTextPreference2;
    private EditTextPreference mEditTextPreference3;
    private ListPreference mListPreference;
    private ListPreference mListPreference1;
    public static final String SHARED_PREFS_NAME = "settings";

    String defaultModeSelected;
    float rampTimeEnt;
	int refDivEnt;
	float startFreqEnt;
	float stopFreqEnt;
	
	String rampTime;
	String refDiv;
	String startFreq;
	String stopFreq;
	String rampTimeCheck;
	String refDivCheck;
	String startFreqCheck;
	String stopFreqCheck;
	
	
	
@SuppressWarnings("deprecation")
@Override
public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		PreferenceManager preferenceManager = getPreferenceManager();
//        preferenceManager.setSharedPreferencesName(SHARED_PREFS_NAME);
//        preferenceManager.getSharedPreferences()
//                .registerOnSharedPreferenceChangeListener(this);
		
		addPreferencesFromResource(R.xml.preference);
		 //Create variables to reference individual preferences
		 mEditTextPreference = (EditTextPreference) getPreferenceScreen()
	                .findPreference(KEY_EDITTEXT_PREFERENCE);
		 mEditTextPreference1 = (EditTextPreference) getPreferenceScreen()
	                .findPreference(KEY_EDITTEXT_PREFERENCE1);
		 mEditTextPreference2 = (EditTextPreference) getPreferenceScreen()
	                .findPreference(KEY_EDITTEXT_PREFERENCE2);
		 mEditTextPreference3 = (EditTextPreference) getPreferenceScreen()
	                .findPreference(KEY_EDITTEXT_PREFERENCE3);
	     mListPreference = (ListPreference) getPreferenceScreen()
	                .findPreference(KEY_LIST_PREFERENCE);
	     mListPreference1 = (ListPreference) getPreferenceScreen()
	                .findPreference(KEY_LIST_PREFERENCE1);
	     
	     rampTimeCheck = mEditTextPreference.getText();
	     refDivCheck = mEditTextPreference1.getText();
	     startFreqCheck = mEditTextPreference2.getText();
	     stopFreqCheck = mEditTextPreference3.getText();
	     getActionBar().setDisplayHomeAsUpEnabled(true);
	     setResult(Activity.RESULT_CANCELED);
//	     setRetainInstance(true);
	}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
		// This ID represents the Home or Up button. In the case of this
		// activity, the Up button is shown. Use NavUtils to allow users
		// to navigate up one level in the application structure. For
		// more details, see the Navigation pattern on Android Design:
		//
		// http://developer.android.com/design/patterns/navigation.html#up-vs-back
		//
		// TODO: If Settings has multiple levels, Up should navigate up
		// that hierarchy.
		//			NavUtils.navigateUpFromSameTask(this);
		//			return true;
		finish();
		
	}
	return super.onOptionsItemSelected(item);
} 

@Override
public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
    if (root == null)
        return;
}
	@Override
	public void onResume() {
	        super.onResume();
  
		    // Setup the initial values for the preference summaries on
	        // the main preference screen
//	        Log.d("RDKSettings","Got to onResume");
	        mEditTextPreference1.setSummary(mEditTextPreference1.getText());
	        mEditTextPreference2.setSummary(mEditTextPreference2.getText()+" GHz");
	        mEditTextPreference3.setSummary(mEditTextPreference3.getText()+" GHz");
	        mListPreference.setSummary(mListPreference.getEntry());
	        mListPreference1.setSummary(mListPreference1.getEntry());	 
	        mEditTextPreference.setSummary(mEditTextPreference.getText()+" ms");
	       
	        // Set up a listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
	    }

	@Override
	public void onPause() {
	        super.onPause();
//	        Log.d("RDKSettings","Got to onPause");
	        // Unregister the listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences()
	                .unregisterOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onStop(){
		super.onStop();
//        Log.d("RDKSettings","Got to onStop");
	}
	
	//When a preference changes, check to make sure it is a valid input for each preference
	//and alert the user to change if not a valid value
	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	            String key) {	
		   // Get values entered into each EditTextPreference box, convert from String to int
			try{
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				rampTime = preferences.getString(KEY_EDITTEXT_PREFERENCE, "0");
				rampTimeEnt = Float.valueOf(rampTime);
				refDiv = preferences.getString(("refDividerEntered"), "0");
				refDivEnt = Integer.valueOf(refDiv);
				startFreq = preferences.getString(("startFreqEntered"), "0");
				startFreqEnt = Float.valueOf(startFreq);
				stopFreq = preferences.getString(("stopFreqEntered"), "0");
				stopFreqEnt = Float.valueOf(stopFreq);
			}
			catch(NumberFormatException e){
					//This catches the "NumberFormatException" if preference field(s) left 
					//blank, rest of code below handles the notification and updates summary 
			}
			finally{
			  //Initialize alertDialog to pop up when value is invalid to alert the user
			  AlertDialog alertDialog = new AlertDialog.Builder(this,3).create();
			  alertDialog.setTitle("Invalid Parameter Entered");
			  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		      final PreferenceScreen screen = (PreferenceScreen) findPreference("pref_screen");
		      
		      //Check Ramp Time value
			  if (key.equals(KEY_EDITTEXT_PREFERENCE)) {
	              if((rampTimeEnt>=1)&&(rampTimeEnt<=65536)&&(rampTime.isEmpty()==false)){
				  mEditTextPreference.setSummary(mEditTextPreference.getText()+ " ms");
				  rampTimeCheck = preferences.getString(KEY_EDITTEXT_PREFERENCE, "0");
				  }
	              else if((rampTimeEnt<1)||(rampTimeEnt>65536)||(rampTime.equals(""))){
	            	//Find out which preference is being edited and get the number of the order
	            	final int pos = findPreference(KEY_EDITTEXT_PREFERENCE).getOrder();
	            	
	            	//Set up alertDialog box to pop up
	            	alertDialog.setMessage("Invalid Ramp Time value, choose an integer between 1 and 65536");
					
	            	//Remove incorrect value from savedPreferences, replace with last correct value entered
	            	preferences.edit().remove(KEY_EDITTEXT_PREFERENCE).putString(KEY_EDITTEXT_PREFERENCE, rampTimeCheck).commit();
					mEditTextPreference.setText(rampTimeCheck);
					
	            	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	            	@Override
					public void onClick(DialogInterface dialog, int which) {
						//Once alert dialog is acknowledged, this simulates a click on the same preference to 
	            		//bring the EditTextPreference box back to the screen automatically to change value
	            		screen.onItemClick(null, null,pos,0);
						mEditTextPreference.setSummary(rampTimeCheck+" ms");
					}});
	            	alertDialog.show();
	              }
	          }
		      
			  //Check reference divider value
			  else if (key.equals(KEY_EDITTEXT_PREFERENCE1)) {
		        	if((refDivEnt>=1) && (refDivEnt<=256) && (refDiv.isEmpty()==false)){
			        mEditTextPreference1.setSummary(mEditTextPreference1.getText());
			        refDivCheck = preferences.getString(KEY_EDITTEXT_PREFERENCE1, "empty");
			        }
		        	else if((refDivEnt<1)||(refDivEnt>256)||(refDiv.equals(""))){
			        	final int pos = findPreference(KEY_EDITTEXT_PREFERENCE1).getOrder();
		            	alertDialog.setMessage("Invalid Reference Divider value, choose an integer between 1 and 256");
						preferences.edit().remove(KEY_EDITTEXT_PREFERENCE1).putString(KEY_EDITTEXT_PREFERENCE1, refDivCheck).commit();
						mEditTextPreference1.setText(refDivCheck);
		            	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		        		@Override
						public void onClick(DialogInterface dialog, int which) {
							screen.onItemClick(null, null,pos,0);							
							mEditTextPreference1.setSummary(refDivCheck);
						}});
		            	alertDialog.show();
			        }
		        }
			   
		        else if (key.equals(KEY_EDITTEXT_PREFERENCE2)){
		        	if((startFreqEnt >=2.4) && (startFreqEnt<=2.5) && (startFreq.isEmpty()==false)){
		            mEditTextPreference2.setSummary(mEditTextPreference2.getText()+" GHz");
			        startFreqCheck = preferences.getString(KEY_EDITTEXT_PREFERENCE2, "0");
		        	}
		        	else if((startFreqEnt<2.4)||(startFreqEnt>2.5)||(startFreq.equals(""))){
		            	final int pos = findPreference(KEY_EDITTEXT_PREFERENCE2).getOrder();
		            	alertDialog.setMessage("Invalid Start Frequency value, choose a value  between 2.400 GHz and 2.500 GHz");
						preferences.edit().remove(KEY_EDITTEXT_PREFERENCE2).putString(KEY_EDITTEXT_PREFERENCE2, startFreqCheck).commit();
						mEditTextPreference2.setText(startFreqCheck);
		            	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		        		@Override
						public void onClick(DialogInterface dialog, int which) {
							screen.onItemClick(null, null,pos,0);
							mEditTextPreference2.setSummary(startFreqCheck+" GHz");
						}});
		            	alertDialog.show();
		        	}
			    }
			  
			    else if (key.equals(KEY_EDITTEXT_PREFERENCE3)) {
			    	if((stopFreqEnt >=2.4) && (stopFreqEnt<=2.5) && (stopFreq.isEmpty()==false)){
		            mEditTextPreference3.setSummary(mEditTextPreference3.getText()+" GHz");
			        stopFreqCheck = preferences.getString(KEY_EDITTEXT_PREFERENCE3, "0");
			    	}
			    	else if((stopFreqEnt<2.4)||(stopFreqEnt>2.5)||(stopFreq.equals(""))){
		            	final int pos = findPreference(KEY_EDITTEXT_PREFERENCE3).getOrder();
		            	alertDialog.setMessage("Invalid Stop Frequency value, choose a value between 2.400 GHz and 2.500 GHz");
						preferences.edit().remove(KEY_EDITTEXT_PREFERENCE3).putString(KEY_EDITTEXT_PREFERENCE3, stopFreqCheck).commit();
						mEditTextPreference3.setText(stopFreqCheck);
		            	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		        		@Override
						public void onClick(DialogInterface dialog, int which) {
							screen.onItemClick(null, null,pos,0);
							mEditTextPreference3.setSummary(stopFreqCheck+" GHz");

						}});
		            	alertDialog.show();
		        	}
			    }
			  
		    else if (key.equals(KEY_LIST_PREFERENCE)) {
	            mListPreference.setSummary(mListPreference.getEntry());
		    }
		    else if (key.equals(KEY_LIST_PREFERENCE1)){
		    	mListPreference1.setSummary(mListPreference1.getEntry());
		    	defaultModeSelected=(mListPreference1.getValue());
		    	Log.v("Guess", ""+defaultModeSelected);
		    	Log.v("Choice",""+mListPreference1.getValue());
		    	
		    	
	        }
	      }
	  }
	
	}
