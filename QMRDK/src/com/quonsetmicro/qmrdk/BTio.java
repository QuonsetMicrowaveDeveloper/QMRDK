package com.quonsetmicro.qmrdk;

import java.io.UnsupportedEncodingException;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BTio extends Fragment implements OnClickListener {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		setRetainInstance(true);
		
		View v = inflater.inflate(R.layout.btio_layout, container, false);

		Button b = (Button) v.findViewById(R.id.send_btn);
		b.setOnClickListener(this);

		// Inflate the layout for this fragment
		return v;
	}

	public void writeMsg(String msg) {
		
    	if (((MainActivity) getActivity()).connected == false){
    		return;
    	}
    	
		((MainActivity) getActivity()).sendMessage(msg);
		Log.d("btio", "msg sent");
	}

	public void writeReadMsg(String msg) {
		((MainActivity) getActivity()).sendMessage(msg);
		Log.d("btio", "msg sent");

	}

	public void parse(byte[] msg) {

		String tmp = null;
		try {
			tmp = new String(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Log.d("LOG", "received: " + tmp);
		
		if(getView() == null)
		{
			Log.d("btio","view null");
			return;
		}
		
		TextView rcvHistTxt = (TextView) getView().findViewById(R.id.rcv_txt);
		rcvHistTxt.append(tmp);
				
	}

	public void sendMsg() {
		EditText cmdTxt = (EditText) getView().findViewById(R.id.cmd_txt);

		if (cmdTxt.getText().toString().contains("?")) {
			writeReadMsg(cmdTxt.getText().toString());
		} else {
			writeMsg(cmdTxt.getText().toString());
		}

		Log.d("LOG", cmdTxt.getText().toString());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_btn:
			sendMsg();
			break;
		}

	}

}