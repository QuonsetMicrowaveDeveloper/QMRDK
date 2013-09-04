package com.quonsetmicro.qmrdk;


import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.ZoomListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class dopplerDemoFrag extends Fragment {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private short[] raw; // the last set of data collected	
	private int adcOffset = 32768;
	private int numSamples2Collect = 4096;
	private double startFreq = 2.4; // in GHz
	boolean stopCollecting = true;
//	double samplingFreq = 22471; // in Hz
	double samplingFreq = 22197; // in Hz
	ScheduledFuture<?> schedTask;

	int rcvStatBit = 0;
	
	// plotting 
	private PlotSettings plot = new PlotSettings();
	private GraphicalView mChartView;
	ZoomListener myZoomListener;
	private XYMultipleSeriesDataset mDataset; // = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
    	
//    	Log.d("LOG","creating doppler Demo View");
    	
    	View v = inflater.inflate(R.layout.doppler_demo_layout, container, false);	
    	v.findViewById(R.id.startDoppler_btn).setOnClickListener(start_OnClickListener); 
    	v.findViewById(R.id.stopDoppler_btn).setOnClickListener(stop_OnClickListener); 
    	raw = new short[128];
    	
    	writeMsg("SWEEP:TYPE 3$");
    	writeMsg("SWEEP:START$");
    	writeMsg("sweep:freqstar?$");
    	
		mRenderer = plot.getMyDefaultRenderer();
		mDataset = plot.getMyDefaultData();   
    	
        // Inflate the layout for this fragment   	
        return v; 
    }
    
    public void onStop()
    {
    	super.onStop();
//    	Log.d("doppler","onStop");
    	if(schedTask==null){
    		return;
    	}
    	schedTask.cancel(false);
    }

	public void parse(byte[] msg) {

		
//		if(rcvStatBit == 1)
//		{
//			rcvStatBit = 0;
//        	ImageView statusImg = (ImageView)getActivity().findViewById(R.id.radarStatus);
//        	statusImg.setImageResource(R.drawable.antenna_and_radio_waves_2);
//		}else{
//			rcvStatBit = 1;
//        	ImageView statusImg = (ImageView)getActivity().findViewById(R.id.radarStatus);
//        	statusImg.setImageResource(R.drawable.antenna_and_radio_waves_0);
//		}
        	
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
//   			Log.d("doppler",lockStatus);
   			
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
//   			Log.d("doppler",String.valueOf(msg.length));
//   			Log.d("doppler",String.valueOf(raw.length));
   			
   			// This return is different from the others, the data is binary (16bits) 
   			// the first 7 bytes are the header information (DATA:\n\r) and the end 'END:\n\r' is 6 (Hence i starts at 7, and raw is (msg.length-13)/2)
   			int n = 0;
   			for(int i = 7; i < ((msg.length-6)); i+=2) {
   				raw[n] = (short) ((short)((msg[i] << 8) + (msg[i+1] & 0xff)) - adcOffset);
   				n++;
    		}


   			updateSpeedLbl();
//   			scheduler.schedule(dopplerCollect, 10, TimeUnit.MILLISECONDS);
   			
   		}else if(rcvHeader.contains("RAMP"))
   		{
   			String ramptime_val = tmp.substring(0, endIndex);
//   			Log.d("doppler",ramptime_val);
   			
   		}else if(rcvHeader.contains("STARTF"))
   		{
   			String start_freq = tmp.substring(0, endIndex);
   			startFreq = Double.parseDouble(start_freq);
   			
   		}else if(rcvHeader.contains("STOPF"))
   		{
   			String stop_freq = tmp.substring(0, endIndex);
//   			Log.d("doppler",stop_freq);
   
   		}else if(rcvHeader.contains("TYPE"))
   		{
   			String sweep_type = tmp.substring(0, endIndex);
//   			Log.d("doppler",sweep_type);
   			
   		}else if(rcvHeader.contains("PLLM"))
   		{
   			String pllm_val = tmp.substring(0, endIndex);
//   			Log.d("doppler",pllm_val);
   			
   		}else if(rcvHeader.contains("SET"))
   		{
   			// no idea what this return is...
   			
   		}else if(rcvHeader.contains("RETACT"))
   		{
   			String actFreq_val = tmp.substring(0, endIndex);
//   			Log.d("doppler",actFreq_val);
   			
   		}else if(rcvHeader.contains("DIV"))
   		{
   			String div_val = tmp.substring(0, endIndex);
//   			Log.d("doppler",div_val);
   			
   		}else if(rcvHeader.contains("REFEXT"))
   		{
   			String refExt_val = tmp.substring(0, endIndex);
//   			Log.d("doppler",refExt_val);
   			
   		}else{
//   			Log.d("LOG","No match in parser");
   		}
	}
	
	public void updateSpeedLbl(){
		
		CalcFFT calculate = new CalcFFT();
		// fftData = calculate.fft(dataToPlot); // to plot data that is an array
		// of type double[]

		double[] rawDoub = new double[raw.length];
		double[] fftData;
		for (int i = 0; i < raw.length; i++) {
			// rawDoub[i] = raw_diff[i];
			rawDoub[i] = ((double)raw[i])/32768 *5; // convert to Volts (5V swing
		}

		// remove DC
		double sum = 0;
		for(int n = 0; n < rawDoub.length; n++)
		{
			sum = sum + rawDoub[n];
		}
		
		double mean  = sum/rawDoub.length;
		for(int n = 0; n < rawDoub.length; n++)
		{
			rawDoub[n] = (rawDoub[n] - mean);
		}
			
		
		// fftData = calculate.formatFFT(calculate.fft(rawDoub));
		fftData = calculate.fft(rawDoub);
//		Log.d("doppler",String.valueOf(fftData.length));
		
		double maxVal = -1*Math.pow(2, 20);
		int maxIndex = 0;
		
		for(int n = 1; n < fftData.length/2; n++) // start at 1 to skip first sample
		{
			if(fftData[n] > maxVal)
			{
				maxVal = fftData[n];
				maxIndex = n-1;
			}
		}
		
//		plotDopData(fftData);
		
		double tmpVal;
		double freq; 
		
		freq = (samplingFreq/2)/(fftData.length/2) *maxIndex;
//		Log.d("doppler",String.valueOf(freq));
//		Log.d("doppler",String.valueOf(maxIndex));
//		Log.d("doppler",String.valueOf(fftData.length/2));
		
		tmpVal = ((freq)* 3e8)/(startFreq*1000000000); //freq at max;
				
		TextView speedLbl = (TextView)getView().findViewById(R.id.speed_lbl);
		
		
		if(maxVal > -60) // power threshold for doppler display
		{
//			if(tmpVal > 7)
//			{
				speedLbl.setText(String.valueOf(tmpVal*2.23694).substring(0, 4) + " mph\n");
//			}
		}else{
			speedLbl.setText("0.00 mph");
		}
		
//		Log.d("doppler",String.valueOf(stopCollecting));
		
//		if(!stopCollecting)
//		{
//			Log.d("doppler","scheduling");
//			scheduler.schedule(collect, 500, TimeUnit.MILLISECONDS);
//		}else{
//			stopCollecting = true;
//		}
		
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		writeMsg("capt:stre " + String.valueOf(numSamples2Collect) +"$");
		
//    	ImageView statusImg = (ImageView)getActivity().findViewById(R.id.radarStatus);
//    	statusImg.setImageResource(R.drawable.antenna_and_radio_waves_0);
	}
	
	public void writeMsg(String msg) {
		if (((MainActivity) getActivity()).connected == false){
    		return;
    	}
	   	 ((MainActivity) getActivity()).sendMessage(msg);
	   }
	
	//On click listener for button1
    final OnClickListener start_OnClickListener = new OnClickListener() {
        public void onClick(final View v) {
        
        	schedTask = scheduler.scheduleAtFixedRate(collectDoppler, 0, 800, TimeUnit.MILLISECONDS);
        }
    };
    
    final OnClickListener stop_OnClickListener = new OnClickListener() {
        public void onClick(final View v) {

        	schedTask.cancel(false);
        }
    };
        
    final Runnable collectDoppler = new Runnable() {
        public void run() 
        {        
        	Log.d("doppler","collecting");
        	writeMsg("capt:stre " + String.valueOf(numSamples2Collect) +"$");
    	}
    };
    
    final Runnable rcvStatusOn = new Runnable() {
        public void run() 
        {        
        	ImageView statusImg = (ImageView)getActivity().findViewById(R.id.radarStatus);
        	statusImg.setImageResource(R.drawable.antenna_and_radio_waves_2);
        	scheduler.schedule(rcvStatusOff, 500, TimeUnit.MILLISECONDS);
    	}
    };
    
    final Runnable rcvStatusOff = new Runnable() {
        public void run() 
        {        
        	ImageView statusImg = (ImageView)getActivity().findViewById(R.id.radarStatus);
        	statusImg.setImageResource(R.drawable.antenna_and_radio_waves_0);
    	}
    };
    
    public void plotDopData(double[] data){
		
		XYSeries dataSeries = new XYSeries("Tablet Data");
		
		// add data to the XYSeries
		for (int i=0; i<data.length; i++)
			dataSeries.add(i, data[i]);
		
		mDataset.removeSeries(0);
		mDataset.addSeries(0,dataSeries);
		mRenderer = plot.getMyDefaultRenderer();
		mRenderer.setYAxisMax(-20);
		mRenderer.setYAxisMin(-90);
		
		if (mChartView == null){
			RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.DopplerPlotLayout);
			mChartView = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);
			// remove the layout before adding a new one
			layout.removeAllViews();
			mChartView.addZoomListener(myZoomListener, false, true);
			layout.addView(mChartView);
			
		}else{
			mChartView.repaint();
		}
	}

}