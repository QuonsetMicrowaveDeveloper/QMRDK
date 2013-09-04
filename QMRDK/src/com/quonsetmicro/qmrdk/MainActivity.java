package com.quonsetmicro.qmrdk;

//import org.achartengine.ChartFactory;
import java.io.File;
import java.io.UnsupportedEncodingException;

import com.quonsetmicro.qmrdk.listFragment.OnPreferenceAttachedListener;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnPreferenceAttachedListener {
	Intent serverIntent = null;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] DrawerItemNames;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	String mBackTitle;
	Bundle newBundy=new Bundle();
	String baseDir = Environment.getExternalStorageDirectory().toString();
	final String rdkFolder = "QMRDK";
	final String sep = "/";
	File folderFile = new File(baseDir+sep+rdkFolder+sep+"Data");
	
//	private plotFragment plotFrag;
//	private BTio btIoFrag;
//	private quickFragment quickFrag;
//	private processFragment processFrag;
//	private shareFragment shareFrag;
	int here0 = 0;
	int here1 = 0;
	int here2 = 0;
	int here3 = 0;
	int here4 = 0;
	int defaultMode;
	
	String rampTime;
	String refDiv;
	String startFreq;
	String stopFreq;
	String rampTimeCheck;
	String refDivCheck;
	String startFreqCheck;
	String stopFreqCheck;
	String rampTi;
	String refDivi;
	String startTi;
	String stopTi;
	String wave;
	String def;
	
	SharedPreferences preferences;
	public static final String KEY_LIST_PREFERENCE = "sweeper";
	public static final String KEY_LIST_PREFERENCE1="defaults";
	public static final String KEY_EDITTEXT_PREFERENCE = "rampTimeEntered";
    public static final String KEY_EDITTEXT_PREFERENCE1 = "refDividerEntered";
    public static final String KEY_EDITTEXT_PREFERENCE2 = "startFreqEntered";
    public static final String KEY_EDITTEXT_PREFERENCE3 = "stopFreqEntered";

	// Bluetooth
    String ramptime_val;
    String start_freq;
    String stop_freq;
    String sweep_type;
    String div_val;
    boolean connected = false;
    
	private String currentFragTag = null;
	byte[] rcvData = null;
	
	private int activeFrag = 0;
	
	// Message types sent from the BluetoothChatService
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_DISCONNECTED = 6;
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	public static final String DEVICE_NAME = null;
	public static final String TOAST = null;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	// private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter	
	private BluetoothAdapter mBluetoothAdapter = null;	
	// Member object for the chat services
	private btcommFragment mBtCommFragment = null;
	int ramp;

	// END OF BT INITIALIZERS

	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("OnCreate", "Creating");
		super.onCreate(savedInstanceState);
		//Make sure there is a folder for the app to save and store captured data files
		//Create folder if none exists
		if (folderFile.isDirectory()==false){   
	        	folderFile.mkdirs();
		}
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
//	    Intent intent = getIntent();
//	    String defaultModeSelected = intent.getExtras().getString("defModVal");
//	    int foo = Integer.parseInt(defaultModeSelected);
//	    Log.v("MAIN", ""+foo);
		
	    //SharedPreferences preference = getSharedPreferences(KEY_EDITTEXT_PREFERENCE, MODE_PRIVATE);
	    //Log.d("MAIN_ramptime", ""+preferences.getInt(KEY_EDITTEXT_PREFERENCE, 0));
	    
	    
//	    rampTime = preferences.getString(KEY_EDITTEXT_PREFERENCE, "0");
//		rampTimeEnt = Integer.valueOf(rampTime);
//		refDiv = preferences.getString(("refDividerEntered"), "0");
//		refDivEnt = Integer.valueOf(refDiv);
//		startFreq = preferences.getString(("startFreqEntered"), "0");
//		startFreqEnt = Float.valueOf(startFreq);
//		stopFreq = preferences.getString(("stopFreqEntered"), "0");
//		stopFreqEnt = Float.valueOf(stopFreq);
//	    
	    
	    
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
		}
		
		// set the content view
		setContentView(R.layout.main_menu);


		// Setting up Drawer Navigation // strongly based on android developer web site
		mTitle = mDrawerTitle = getTitle();
		DrawerItemNames = getResources().getStringArray(R.array.drawerList);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.DrawerOpen, R.string.DraweClosed) {

			/** Called when a drawer has settled in a completely closed state. */
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// only for ICS and newer versions
			getActionBar().setHomeButtonEnabled(true);
		}

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, DrawerItemNames));
		
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create an instance of the main fragment, This is what appears
			// when the app is started
			mainFragment mainFrag = new mainFragment();

			// In case this activity was started with special instructions from
			// an Intent,
			// pass the Intent's extras to the fragment as arguments
			mainFrag.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, mainFrag).commit();
		}
	}

	public void onStart() {	
		super.onStart();
		
		Log.v("onstart", "starting");
		rampTi = preferences.getString(KEY_EDITTEXT_PREFERENCE, "16");
		refDivi = preferences.getString(KEY_EDITTEXT_PREFERENCE1, "1"); 
		stopTi = preferences.getString(KEY_EDITTEXT_PREFERENCE3, "2.5"); 
		startTi = preferences.getString(KEY_EDITTEXT_PREFERENCE2, "2.4"); 
		wave = preferences.getString(KEY_LIST_PREFERENCE, "2");
		def = preferences.getString(KEY_LIST_PREFERENCE1, "5"); 
//		Log.v("RampTime",""+rampTi);
//		Log.v("Refdiv",""+refDivi);
//		Log.v("stop",""+stopTi);
//		Log.v("start",""+startTi);
//		Log.v("Wave type",""+wave);
//		Log.v("Default",""+def);
		defaultMode = Integer.valueOf(def);

		if((mBtCommFragment!=null)&&(connected == true)){
		sendMessage("sweep:ramptime "+rampTi+"$");
		sendMessage("sweep:freqstar "+startTi+"$");
		sendMessage("sweep:freqstop "+stopTi+"$");
		sendMessage("sweep:type "+wave+"$");
		sendMessage("freq:ref:div "+refDivi+"$");
		sendMessage("sweep:start$");
		
		sendMessage("sweep:ramptime?$");
		sendMessage("sweep:freqstar?$");
		sendMessage("sweep:freqstop?$");
		sendMessage("sweep:type?$");
		sendMessage("freq:ref:div?$");
//		Log.v("BT","Got Here");
		}
		
	switch (defaultMode)	{
	case 5: // None
		break;
	case 0: // plotting
		getActionBar().setTitle("Collect and Plot");
//			If Statement, insures that the same fragment does not appear twice in the Backstack.
			here1=0;here2=0;here3=0;here4=0;
			if(here0==0){
			
			plotFragment plotFrag = new plotFragment();
			
			FragmentTransaction plotTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back

			plotTransaction.replace(R.id.fragment_container, plotFrag,
					"plotFrag");
			plotTransaction.addToBackStack("Collect and Plot");

			// Commit the transaction
			plotTransaction.commit();
			here0++;
			break;
			}
			else break;
		case 1: // sharing
			getActionBar().setTitle("Share");
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here2=0;here3=0;here4=0;
			if(here1==0){
			shareFragment shareFrag = new shareFragment();
			
			FragmentTransaction shareTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			shareTransaction.replace(R.id.fragment_container, shareFrag,
					"shareFrag");
			shareTransaction.addToBackStack("Share");

			// Commit the transaction
			shareTransaction.commit();
//			currentFragId = position;
			here1++;
			break;
			}
			else break;

		case 2: // Doppler (working title)
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here1=0;here3=0;here4=0;
			if(here2==0){
			dopplerDemoFrag dopplerFrag = new dopplerDemoFrag();
			
			FragmentTransaction dopplerDemoTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			dopplerDemoTransaction.replace(R.id.fragment_container, dopplerFrag,
					"dopplerFrag");
			dopplerDemoTransaction.addToBackStack("Doppler");

			// Commit the transaction
			dopplerDemoTransaction.commit();
//			currentFragTag = position;
			here2++;
			break;
			}
			else break;
		case 3: // processing
			getActionBar().setTitle("Process");
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here1=0;here2=0;here4=0;
			if(here3==0){
			processFragment processFrag = new processFragment();
			
			FragmentTransaction processTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			processTransaction.replace(R.id.fragment_container, processFrag,
					"processFrag");
			processTransaction.addToBackStack("Process");

			// Commit the transaction
			processTransaction.commit();
//			currentFragTag = position;
			here3++;
			break;
			}
			else break;

		case 4:
			getActionBar().setTitle("Bluetooth I/O");
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here1=0;here2=0;here3=0;
			if(here4==0){
			BTio btIoFrag = new BTio();
			
			FragmentTransaction btioTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			btioTransaction.replace(R.id.fragment_container, btIoFrag,
					"btIoFrag");
			
			btioTransaction.addToBackStack("Bluetooth I/O");

			// Commit the transaction
			btioTransaction.commit();
//			currentFragTag = position;
			here4++;
			break;}
			else break;
			
	}
	
		
		
//		// If BT is not on, request that it be enabled.
//		// setupChat() will then be called during onActivityResult
//		if (!mBluetoothAdapter.isEnabled()) {
//			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//			// Otherwise, setup the chat session
//		} else {
//			if (mBtCommFragment == null)
//				setupChat();
//		}
	}

	@Override
	public synchronized void onResume() {	
		super.onResume();
//		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		Toast.makeText(this, ""+display.getRotation(), Toast.LENGTH_LONG).show();
		Log.d("LOG", "onResume");
		
		if (mBtCommFragment != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mBtCommFragment.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mBtCommFragment.start();
			}
		}
			
	}
	
	 @Override
	    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
	    }
	
	
	// When the fragment is changed this method is called. 
	// The main function of this method is to notify the BT comm thread of the new fragment (mBtCommFragment.setFragReceiver(activeFrag);)
	// All fragments that use BT must be listed here
	@Override
	public void onAttachFragment(Fragment fragment) {
	    // TODO Auto-generated method stub
	    super.onAttachFragment(fragment);

	    currentFragTag = fragment.getTag();
	    
	    // check to make sure the current fragment has a tag
	    if(currentFragTag == null)
	    	return;
	    
	    // set activeFrag based on the fragment's tag
	    if(currentFragTag.contains("plotFrag"))
	    	activeFrag = 0;
	    else if(currentFragTag.contains("shareFrag"))
	    	activeFrag = 1;
    	else if(currentFragTag.contains("dopplerFrag"))
	    	activeFrag = 2;
    	else if(currentFragTag.contains("processFrag"))
	    	activeFrag = 3;
//    	else if(currentFragTag.contains("radarSettingFrag"))
//	    	activeFrag = 4;
    	else if(currentFragTag.contains("btIoFrag"))
    		activeFrag = 4;
//	    	activeFrag = 5;
	    
	    if(mBtCommFragment == null)
	    {
	    	return;
	    }
	    mBtCommFragment.setFragReceiver(activeFrag);
//	    Log.d("main",String.valueOf(activeFrag));
	    
//    	Toast.makeText(getApplicationContext(), String.valueOf(fragment.getTag()), Toast.LENGTH_SHORT).show();

	}

	// The Handler that gets information back from the BluetoothChatService
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
//				Log.i("BT", "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));
					connected = true;
					// mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
//					Log.v("Main", "not connected toast");
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
//				Log.i("BT", "Tablet:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
//				Log.i("MESSAGE_READ", "Received stuff main");
				
				String rcvString = null;
				try {
					rcvString = new String(readBuf,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		   		
//		   		Log.d("LOG",rcvString);
		   		
		   		// separate parts of receive string of format: <TYPE>:<DATA>:END
//		   		 "END" signifies the end of the message
		   		
		   		int headerIndex = rcvString.indexOf(":");
		   		String rcvHeader = rcvString.substring(0, headerIndex);
		   		String tmp = rcvString.substring(headerIndex+1);
		   		int endIndex = tmp.indexOf(":END");
		   		
//		   		String rcvData = rcvString.substring(headerIndex, endIndex+5);
		   		
		   		// Parse
		   		if(rcvHeader.contains("LOCK"))
		   		{
		   			String lockStatus = tmp.substring(0, endIndex);
//		   			Log.d("Main",lockStatus);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else if(rcvHeader.contains("RAMP"))
		   		{
		   			ramptime_val = tmp.substring(0, endIndex-3);
//		   			Log.d("Main",ramptime_val);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else if(rcvHeader.contains("STARTF"))
		   		{
		   			start_freq = tmp.substring(0, endIndex);
//		   			Log.d("Main",start_freq);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else if(rcvHeader.contains("STOPF"))
		   		{
		   			stop_freq = tmp.substring(0, endIndex);
//		   			Log.d("Main",stop_freq);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else if(rcvHeader.contains("TYPE"))
		   		{
		   			sweep_type = tmp.substring(0, endIndex);
//		   			Log.d("Main",sweep_type);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else if(rcvHeader.contains("PLLM"))
		   		{
		   			String pllm_val = tmp.substring(0, endIndex);
//		   			Log.d("Main",pllm_val);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else if(rcvHeader.contains("DIV"))
		   		{
		   			div_val = tmp.substring(0, endIndex);
//		   			Log.d("Main",div_val);
		   			try {Thread.sleep(100);
		   	        } catch (InterruptedException e) {
		   	            // TODO Auto-generated catch block
		   	            e.printStackTrace();
		   	        }
		   		}else{
//		   			Log.d("Main","No match in parser");
		   		}

				
				// Send the received message to the current fragment
				switch (msg.arg2) { // msg.arg2 should be equal to this.activeFrag
				case 0:// plotting
					
//					FragmentManager fm = getFragmentManager();
//									
//					if(fm == null)
//					{
//						Log.d("mHandler", "fm Null");
//						break;
//					}
										
					plotFragment frag = (plotFragment) getSupportFragmentManager()
					.findFragmentByTag("plotFrag");
					
//					Log.d("Main",String.valueOf(msg.arg2));
					
					if (frag != null)
						frag.parse(readBuf);
					break;

				case 1:// sharing
//					Log.d("Main",String.valueOf(msg.arg2));
					
					shareFragment frag1 = (shareFragment) getSupportFragmentManager()
						.findFragmentByTag("sharFrag");
					
					if (frag1 != null)
						frag1.parse(readBuf);
					break;

				case 2:// quick
					
					dopplerDemoFrag frag2 = (dopplerDemoFrag) getSupportFragmentManager()
						.findFragmentByTag("dopplerFrag");
					
					if (frag2 != null)
						frag2.parse(readBuf);
					break;

				case 3:// process
//					Log.d("Main",String.valueOf(msg.arg2));
//					
//					if (processFrag != null)
//						processFrag.parse(readBuf);
//					break;

//				case 4:// Radar Settings
//					Log.d("Main",String.valueOf(msg.arg2));
//					break;

				case 4:
//				case 5:// BTio
//					FragmentManager fm1 = getFragmentManager();
//					if(fm1 == null)
//					{
//						Log.d("mHandler", "fm Null");
//						break;
//					}
										
//					Log.d("Main",String.valueOf(msg.arg2));
										
					BTio f5 = (BTio) getSupportFragmentManager().findFragmentByTag("btIoFrag");
					
					if (f5 != null)
						f5.parse(readBuf);
					break;
				}

				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				//Query RDK about settings
				MainActivity.this.sendMessage("sweep:ramptime?$");
				MainActivity.this.sendMessage("sweep:freqstar?$");
				MainActivity.this.sendMessage("sweep:freqstop?$");
				MainActivity.this.sendMessage("sweep:type?$");
				MainActivity.this.sendMessage("freq:ref:div?$");
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			case MESSAGE_DISCONNECTED:
				connected = false;
				break;
			}
		}
	};

		
	
	// Status of Bluetooth connection is set in ActionBar
	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	// Status of Bluetooth connection is set in ActionBar
	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.d("Activity Result", "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
				serverIntent = new Intent(this, DeviceListActivty.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			} else {
				// User did not enable Bluetooth or an error occurred
//				Log.d("BT Enable", "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled,
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivty.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mBtCommFragment.connect(device, false);
	}

	private void setupChat() {
		
		// Initialize the BluetoothChatService to perform bluetooth connections
		
		FragmentManager fm = getSupportFragmentManager();

        // Check to see if we have retained the worker fragment.	
		mBtCommFragment = (btcommFragment)fm.findFragmentByTag("btFrag");

        // If not retained (or first time running), we need to create it.
        if (mBtCommFragment == null) {
        	mBtCommFragment = new btcommFragment(mHandler);
            // Tell it who it is working with.
        	//mBtCommFragment.setTargetFragment(this, 0);
            fm.beginTransaction().add(mBtCommFragment, "btFrag").commitAllowingStateLoss();
            }
		
		//mChatService = new btcommFragment(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Nav drawer item click Listener //
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}


	@Override
	public synchronized void onPause() {
		super.onPause();
		Log.e("App", "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e("App", "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.d("LOG", "onDestroy");
		
//		// Stop the Bluetooth chat services
		if (mBtCommFragment != null)
			mBtCommFragment.stop();
//		Log.e("App", "--- ON DESTROY ---");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		// Action Bar MenuItem onClick events --> what to do next

		//Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.action_settings:
//			Log.d("menu", "setting clicked");
			
			//Read data from RDK and setup the settings
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE, ramptime_val).commit();
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE1, div_val).commit();
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE2, start_freq).commit();
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE3, stop_freq).commit();
			preferences.edit().putString(KEY_LIST_PREFERENCE, sweep_type).commit();
			
			Intent settingintent =new Intent (this,settings_menu.class);
			startActivity(settingintent);
			return true;
		case R.id.bt_connect:
			// Check/enable Bluetooth, launch the DeviceListActivity to see devices and do scan
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				// Otherwise, setup the chat session
			} else {
				if (mBtCommFragment == null)
					setupChat();
					serverIntent = new Intent(this, DeviceListActivty.class);
					startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
					return true;
				}
			}

		return super.onOptionsItemSelected(item);
	}

private SharedPreferences getSharedPreferences(MainActivity mainActivity) {
		// TODO Auto-generated method stub
		return null;
	}

//	public void parseBTmessage(int id, String message, byte[] data){
//		
//		switch (id) {
//		case MESSAGE_STATE_CHANGE:
//			Log.i("BT", "MESSAGE_STATE_CHANGE: " + message);
//			
//			if (message.contains("3")){ // connected
//				this.setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//				// mConversationArrayAdapter.clear();
//			}else if( message == "1") { // Listen
//				
//			}else if( message.contains("2")) { // connecting
//				this.setStatus(R.string.title_connecting);
//			}
////			case BluetoothChatService.STATE_CONNECTED:
////
////				break;
////			case BluetoothChatService.STATE_CONNECTING:
////				setStatus(R.string.title_connecting);
////				break;
////			case BluetoothChatService.STATE_LISTEN:
////			case BluetoothChatService.STATE_NONE:
////				setStatus(R.string.title_not_connected);
////				break;
////			}
//			break;
//		case MESSAGE_WRITE:
////			byte[] writeBuf = (byte[]) msg.obj;
////			// construct a string from the buffer
////			String writeMessage = new String(writeBuf);
////			Log.i("BT", "Tablet:  " + writeMessage);
////			break;
//		case MESSAGE_READ:
//			byte[] readBuf = data;
//			Log.i("MESSAGE_READ", "Received stuff main");
//			//handleMsg(readBuf); //
//
//			switch (currentFragId) {
//			case 0:// plotting
//				plotFragment frag = (plotFragment) getFragmentManager()
//						.findFragmentByTag("plotFrag");
//
//				if (frag != null)
//					frag.parse(readBuf);
////				break;
//
//			case 1:// sharing
//				shareFragment frag1 = (shareFragment) getFragmentManager()
//						.findFragmentByTag("sharFrag");
//
//				if (frag1 != null)
//					frag1.parse();
////				break;
//
//			case 2:// quick
//				quickFragment frag2 = (quickFragment) getFragmentManager()
//						.findFragmentByTag("quickFrag");
//
//				if (frag2 != null)
//					frag2.parse();
////				break;
//
//			case 3:// process
//				processFragment frag3 = (processFragment) getFragmentManager()
//						.findFragmentByTag("processFrag");
//
//				if (frag3 != null)
//					frag3.parse();
////				break;
//
//			case 4:
//				break;
//
//			case 5:
//				BTio frag4 = (BTio) getFragmentManager().findFragmentByTag(
//						"btioFrag");
//
//				if (frag4 != null)
//					frag4.parse(readBuf);
////				break;
//
//			}
//
//			break;
//		case MESSAGE_DEVICE_NAME:
//			// save the connected device's name
//			mConnectedDeviceName = message;
////			Toast.makeText(getApplicationContext(),
////					"Connected to " + mConnectedDeviceName,
////					Toast.LENGTH_SHORT).show();
//			break;
//		case MESSAGE_TOAST:
////			Toast.makeText(getApplicationContext(),
////					message, Toast.LENGTH_SHORT)
////					.show();
//			break;
//		}
//	}
		
	
	/**
	 * F Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 * @return
	 */
	public void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mBtCommFragment.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mBtCommFragment.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			// mOutEditText.setText(mOutStringBuffer);
		}
		
		try {Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return;
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
	
		switch (position) {
		case 0: // plotting
			
//			If Statement, insures that the same fragment does not appear twice in the Backstack.
			here1=0;here2=0;here3=0;here4=0;
			if(here0==0){
			
			plotFragment plotFrag = new plotFragment();
			
			FragmentTransaction plotTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back

			plotTransaction.replace(R.id.fragment_container, plotFrag,
					"plotFrag");
			plotTransaction.addToBackStack("Collect and Plot");

			// Commit the transaction
			plotTransaction.commit();
			here0++;
			break;
			}
			else break;
		case 1: // sharing
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here2=0;here3=0;here4=0;
			
			//Check to see if media device is mounted
			Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

			if(here1==0){
				
			shareFragment shareFrag = new shareFragment();
			
			FragmentTransaction shareTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			shareTransaction.replace(R.id.fragment_container, shareFrag,
					"shareFrag");
			shareTransaction.addToBackStack("Share");

			// Commit the transaction
			shareTransaction.commit();
//			currentFragId = position;
			here1++;
			break;
			}
			else break;

		case 2: // Doppler (working title)
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here1=0;here3=0;here4=0;
			if(here2==0){
			dopplerDemoFrag dopplerFrag = new dopplerDemoFrag();
			
			FragmentTransaction dopplerDemoTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			dopplerDemoTransaction.replace(R.id.fragment_container, dopplerFrag,
					"dopplerFrag");
			dopplerDemoTransaction.addToBackStack("Doppler");

			// Commit the transaction
			dopplerDemoTransaction.commit();
//			currentFragTag = position;
			here2++;
			break;
			}
			else break;
//		case 3: // processing
//			// Create fragment and give it an argument specifying the article it
//			// should show
//			here0=0;here1=0;here2=0;here4=0;
//			if(here3==0){
//			processFragment processFrag = new processFragment();
//			
//			FragmentTransaction processTransaction = getSupportFragmentManager()
//					.beginTransaction();
//
//			// Replace whatever is in the fragment_container view with this
//			// fragment,
//			// and add the transaction to the back stack so the user can
//			// navigate back
//			processTransaction.replace(R.id.fragment_container, processFrag,
//					"processFrag");
//			processTransaction.addToBackStack("Process");
//
//			// Commit the transaction
//			processTransaction.commit();
////			currentFragTag = position;
//			here3++;
//			break;
//			}
//			else break;
	
		case 3:
			// Create fragment and give it an argument specifying the article it
			// should show
			here0=0;here1=0;here2=0;here3=0;
			if(here4==0){
			BTio btIoFrag = new BTio();
			
			FragmentTransaction btioTransaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			btioTransaction.replace(R.id.fragment_container, btIoFrag,
					"btIoFrag");
			
			btioTransaction.addToBackStack("Bluetooth I/O");

			// Commit the transaction
			btioTransaction.commit();
//			currentFragTag = position;
			here4++;
			break;}
			else break;
		case 4: // Radar Settings
			//Read data from RDK and setup the settings
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE, ramptime_val).commit();
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE1, div_val).commit();
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE2, start_freq).commit();
			preferences.edit().putString(KEY_EDITTEXT_PREFERENCE3, stop_freq).commit();
			preferences.edit().putString(KEY_LIST_PREFERENCE, sweep_type).commit();
			
			Intent settingintent = new Intent (this,settings_menu.class);
			startActivity(settingintent);
			break;
		}

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(DrawerItemNames[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		// TODO - need to add version check here. this will probably crash on
		// APIs < 11
		getActionBar().setTitle(mTitle);
	}
	
//Changes the title to that of the appropriate fragment
	@SuppressWarnings("deprecation")
	public void onBackPressed(){
		if(getSupportFragmentManager().getBackStackEntryCount()>1){
		FragmentManager.BackStackEntry backTitleEntry=getSupportFragmentManager().
					getBackStackEntryAt(this.getSupportFragmentManager().getBackStackEntryCount()-2);

			mBackTitle=backTitleEntry.getName();
			getActionBar().setTitle(mBackTitle);
			getSupportFragmentManager().popBackStack();}

				else if (getSupportFragmentManager().getBackStackEntryCount()<=1){
					final AlertDialog exitDialog = new AlertDialog.Builder(this,1).create();
					exitDialog.setTitle("You are now leaving the QMRDK Application. Are you sure?");
					exitDialog.setButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						finish();
						}
					});
					exitDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
//						Back to home screen on cancel
//					    getFragmentManager().popBackStackImmediate();
//					    getActionBar().setTitle("QMRDK");
						exitDialog.dismiss();
						}
					});
					exitDialog.show();
			}
		}	  
	}

