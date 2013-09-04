package com.quonsetmicro.qmrdk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class quickFragment extends Fragment {
	static String ARG_POSITION = "position";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.quick_layout, container, false);
    }

	public void parse(byte[] msg) {
		// TODO Auto-generated method stub
		
	}
}