package com.quonsetmicro.qmrdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.ZoomListener;

import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.content.Context;

public class plotFragment extends Fragment {
	
	static String ARG_POSITION = "position";
	public int x = 0;
	private int adcOffset = 32768;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	// plotting 
	private PlotSettings plot = new PlotSettings();
	private GraphicalView mChartView;
	ZoomListener myZoomListener;
	private XYMultipleSeriesDataset mDataset; // = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer;
	private short[] raw; // the last set of data collected
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
//    	Log.d("LOG","creating Plot frag View");
    	
    	raw = new short[128];
//    	setRetainInstance(true);
    	
//    	Log.d("plot Log",String.valueOf(x));  	
//    	Log.d("PlotFrag","PlotFrag Created");

    	View v = inflater.inflate(R.layout.plotting_layout, container, false);	
    	v.findViewById(R.id.collect_btn).setOnClickListener(collect_OnClickListener); 
    	v.findViewById(R.id.save_btn).setOnClickListener(save_OnClickListener); 
    	
		mRenderer = plot.getMyDefaultRenderer();
		mDataset = plot.getMyDefaultData();    	
    	
        // Inflate the layout for this fragment   	
        return v; 
    }
    
    public void onStart(){
    	super.onStart();
    	
    	if (mChartView == null) {
			LinearLayout layout = (LinearLayout) getView().findViewById(R.id.DopplerPlotLayout);
			mChartView = ChartFactory.getLineChartView(getActivity(), mDataset,
					mRenderer);
			mChartView.addZoomListener(myZoomListener, false, true);
			
			/// test for HW accel
			layout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			/// end test
			
			layout.addView(mChartView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			
		} else {
			mChartView.repaint(); // use this whenever data has changed and you
			// want to redraw
		}
    }
       
    private void collectData() {
    	    	
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//    	Log.d("LOG","Plot config changed");
    }
    
    public void writeMsg(String msg) {
    	
    	if (((MainActivity) getActivity()).connected == false){
    		return;
    	}
    	
   	 ((MainActivity) getActivity()).sendMessage(msg);
   }

	public void parse(byte[] msg) {

   		String rcvString = null;
		try {
			rcvString = new String(msg,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
   		
//   		Log.d("LOG",rcvString);
   		
   		// separate parts of receive string of format: <TYPE>:<DATA>:END
//   		 "END" signifies the end of the message
   		
   		int headerIndex = rcvString.indexOf(":");
   		String rcvHeader = rcvString.substring(0, headerIndex);
   		String tmp = rcvString.substring(headerIndex+1);
   		int endIndex = tmp.indexOf(":END");
//   		
//   		String rcvData = rcvString.substring(headerIndex, endIndex+5);
   		
   		// Parse
   		if(rcvHeader.contains("LOCK"))
   		{
   			String lockStatus = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",lockStatus);
//   			
   		}else if(rcvHeader.contains("DATA"))
   		{
   			// check if even or odd
   			int evenOdd = (msg.length % 2); 
   			int comp = 0;
   			if(evenOdd == 0) // msg.length is even
   			{
   				comp = 1;
   			}
   			//Make sure (msg.length-13) is always even by adding 1 if msg.length is even
   			raw = new short[(msg.length-(13 - comp))/2];
//   			Log.d("plotFrag",String.valueOf(msg.length));
//   			Log.d("plotFrag",String.valueOf(raw.length));
   			
   			// This return is different from the others, the data is binary (16bits) 
   			// the first 7 bytes are the header information (DATA:\n\r) and the end 'END:\n\r' is 6 (Hence i starts at 7, and raw is (msg.length-13)/2)
   			int n = 0;
   			for(int i = 7; i < ((msg.length-6)); i+=2) {
   				raw[n] = (short) ((short)((msg[i] << 8) + (msg[i+1] & 0xff)) - adcOffset);
   				n++;
    		}
   			
   			plotData(raw);
//   			scheduler.schedule(dopplerCollect, 10, TimeUnit.MILLISECONDS);
   			
   		}else if(rcvHeader.contains("RAMP"))
   		{
   			String ramptime_val = tmp.substring(0, endIndex-3);
//   			Log.d("plotFrag",ramptime_val);
   			
   		}else if(rcvHeader.contains("STARTF"))
   		{
   			String start_freq = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",start_freq);
   			
   		}else if(rcvHeader.contains("STOPF"))
   		{
   			String stop_freq = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",stop_freq);
   
   		}else if(rcvHeader.contains("TYPE"))
   		{
   			String sweep_type = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",sweep_type);
   			
   		}else if(rcvHeader.contains("PLLM"))
   		{
   			String pllm_val = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",pllm_val);
   			
   		}else if(rcvHeader.contains("SET"))
   		{
   			// no idea what this return is...
   			
   		}else if(rcvHeader.contains("RETACT"))
   		{
   			String actFreq_val = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",actFreq_val);
   			
   		}else if(rcvHeader.contains("DIV"))
   		{
   			String div_val = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",div_val);
   			
   		}else if(rcvHeader.contains("REFEXT"))
   		{
   			String refExt_val = tmp.substring(0, endIndex);
//   			Log.d("plotFrag",refExt_val);
   			
   		}else{
//   			Log.d("LOG","No match in parser");
   		}
	}
	
	/**
	 * Plots data
	 * @param data short[] of samples to be plotted
	 */
	public void plotData(short[] data){
		
		XYSeries dataSeries = new XYSeries("Tablet Data");
		
		// add data to the XYSeries
		for (int i=0; i<data.length; i++)
			dataSeries.add(i, data[i]);
		
		mDataset.removeSeries(0);
		mDataset.addSeries(0,dataSeries);
		mRenderer = plot.getMyDefaultRenderer();
		
		if (mChartView == null){
			LinearLayout layout = (LinearLayout) getView().findViewById(R.id.DopplerPlotLayout);
			mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);
			// remove the layout before adding a new one
			layout.removeAllViews();
			mChartView.addZoomListener(myZoomListener, false, true);
			layout.addView(mChartView);
			
		}else{
			mChartView.repaint();
		}
	}
	
	public void requestData(){
//		Log.d("plotFrag","collecting");
//		String dataPoints = ()
	}
	
	public void saveData(short[] data){
		
//		Log.d("plotFrag","saving");
		boolean mExternalStorageAvailable, mExternalStorageWriteable = false;
		
		String state = Environment.getExternalStorageState();    
		if (Environment.MEDIA_MOUNTED.equals(state)) {        
			mExternalStorageAvailable = mExternalStorageWriteable = true;    
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {        
			mExternalStorageAvailable = true;       
			mExternalStorageWriteable = false;    
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;   
		}
		
		if(mExternalStorageWriteable != true)
		{
//			Log.d("plotFrag","Storage not accessible");
			return;
		}
			
		
		String root = Environment.getExternalStorageDirectory().toString(); 
		
		TextView collectionName_txt = (TextView)getView().findViewById(R.id.collectName_txt);
		String dir = root + "/QMRDK/Data/";
		File myDir = new File(dir);	// name of directory
		
		if(!myDir.mkdirs())
		{
//			Log.d("plotFrag","Dir does not exist or already exists, not creating");
		}
		
		SimpleDateFormat fileDate = new SimpleDateFormat("yyMMdd_mmHHss",Locale.US);
		Date myDate = new Date();
		String fileString = collectionName_txt.getText().toString() + "_" + fileDate.format(myDate) + ".txt";
		
		String stringToSave = new String(); // string to store data in
		
		for (int i = 0; i<raw.length; i++) 
			stringToSave += data[i] + "\n";
		
		File file = new File (myDir, fileString);
		
		// if it exists overwrite
		if (file.exists ())
		{
			file.delete (); 
		}
		
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(stringToSave.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		new SingleMediaScanner(getActivity(), dir + fileString); 
		
	}
	
	//On click listener for button1
    final OnClickListener collect_OnClickListener = new OnClickListener() {
        public void onClick(final View v) {


        	TextView sampNumTxt = (TextView)getView().findViewById(R.id.sampNum_txt);
        	writeMsg("capt:stre " + sampNumTxt.getText().toString() + "$");
        }
    };
    
    final OnClickListener save_OnClickListener = new OnClickListener() {
        public void onClick(final View v) {

        	saveData(raw);
        }
    };
    
    final Runnable sweepCollect = new Runnable() {
        public void run() 
        {         	
        	TextView sampNumTxt = (TextView)getView().findViewById(R.id.sampNum_txt);
        	writeMsg("capt:stre " + sampNumTxt.getText().toString() + "$");
    	}
    };
    
    private class SingleMediaScanner implements MediaScannerConnectionClient 
    { 
            private MediaScannerConnection mMs; 
            private String path; 
            SingleMediaScanner(Context context, String f) 
            { 
            	path = f; 
                mMs = new MediaScannerConnection(context, this); 
                mMs.connect(); 
            } 
            @Override 
            public void onMediaScannerConnected() 
            { 
                mMs.scanFile(path, null); 
            } 
            @Override 
            public void onScanCompleted(String path, Uri uri) 
            { 
                mMs.disconnect(); 
            } 
        }
    
}