package com.quonsetmicro.qmrdk;

import org.achartengine.model.XYSeries;

import android.util.Log;

/**
 * Algorithm to calculate the FFT of an input data array of type double[].
 * Precondition: Accepts only one input of type double[] that holds the values of
 * 					data to be processed.
 * Postcondition: Returns a type double[] array of calculated FFT data that has a
 * 					length that is equal to a power of two (if it didn't before).
 */
public class CalcFFT {

//	/**
//	 * The array of FFT data
//	 */
//	protected double[] fftData;


	public double[] fft(short[] shortInput) {
		try {
			double[] array = fft(shortInput);
			return array;
		}
		catch (Exception e) {
			Log.e("CalcFFT.java", "I don't know how to function properly, grrrr...");
			return null;
		}
	}
	
	
	/**
	 * sampling frequency in Hz
	 */
	private final int fs = 22100;	// put in settings so user can change

	/**
	 * last x-value, (starting x-value = 0)
	 */
	private final int endValue = fs/2;
	
	public double[] formatFFT(double[] fftData) {
		
		double[] rangeData = new double[fftData.length];
		// Linearly-spaced x-axis values for data points, increment must be constant 
		
		double increment = (endValue)/(fftData.length/2);	// half of sampling freq/length of data
		double range_feet = 0;
		double rangeComp = 0;
		
		for(int i =0; i < (fftData.length/2); i++) 
		{
			range_feet = ((3e8)*(i * increment)/(2*(196e6/16e-3)))*3.28084;
			rangeComp = range_feet * .16;
			rangeData[i] = fftData[i] + rangeComp; // i*increment gives the freq. 
		}

		// for the buildDataset method call:
		
		return rangeData;
	}
	
	
	/**
	 * Calculates the FFT of an array
	 * @param inputArray	Input array
	 * @return fftData	Array of FFT values
	 */
	public double[] fft(double[] inputArray) {
		// size of input array
		int n = inputArray.length;
		int upSampleNum = 4;
		
		// If n is a power of 2, then ld is an integer
		double ld = Math.log(n) / Math.log(2.0);

		// declare arrays to prevent overwriting input data
		double[] realData, imagData;	

		// Checks if n is a power of 2. If not: adds zero place holders on to end of input arrays 
		if (((int) ld) - ld != 0) {
			double powerOfTwo = Math.pow(2, (int)ld +1);
			n = (int) powerOfTwo * upSampleNum;		// set n to equal a power of 2, since it wasn't before
			realData = new double[n];	// initializes array to power of 2 length
			ld = Math.log(n) / Math.log(2.0);
		}
		else
		{
			n = n * upSampleNum;
			realData = new double[n];	// initializes array
			ld = Math.log(n) / Math.log(2.0);
		}
			

		// may cause lag in code if there are a lot of elements to copy from the input array to the new array
		for(int i = 0; i < inputArray.length; i++)
			realData[i] = inputArray[i];
		
		

		imagData = new double[n];	// initializes array for imaginary fft values -> all elements = 0.0


		// Declaration and initialization of the variables
		// ld should be an integer, actually, so I don't lose any information in
		// the cast
		int nu = (int) ld;
		int n2 = (n / 2);
		int nu1 = nu - 1;
		double tReal, tImag, p, arg, c, s;
		
		// Here I check if I'm going to do the direct transform or the inverse
		// transform.
		double constant = -2 * Math.PI;

		// First phase - calculation:
		int k = 0;
		for (int l = 1; l <= nu; l++) {
			while (k < n) {
				for (int i = 1; i <= n2; i++) {
					p = bitreverseReference(k >> nu1, nu);
					// direct FFT or inverse FFT
					arg = constant * p / n;
					c = Math.cos(arg);
					s = Math.sin(arg);
					tReal = realData[k + n2] * c + imagData[k + n2] * s;
					tImag = imagData[k + n2] * c - realData[k + n2] * s;
					realData[k + n2] = realData[k] - tReal;
					imagData[k + n2] = imagData[k] - tImag;
					realData[k] += tReal;
					imagData[k] += tImag;
					k++;
				}
				k += n2;
			}
			k = 0;
			nu1--;
			n2 /= 2;
		}

		// Second phase - recombination:
		k = 0;
		int r;
		while (k < n) {
			r = bitreverseReference(k, nu);
			if (r > k) {
				tReal = realData[k];
				tImag = imagData[k];
				realData[k] = realData[r];
				imagData[k] = imagData[r];
				realData[r] = tReal;
				imagData[r] = tImag;
			}
			k++;
		}

		double[] fftData = new double[n];	// initializes array that will soon store FFT data

//		double dfdt = 196e6/16e-3;
		
		// Magnitude of real & imaginary parts of the fftData
		for(int i = 0; i < fftData.length; i++) {
			fftData[i] = Math.sqrt(Math.pow(realData[i], 2) + Math.pow(imagData[i], 2));
			fftData[i] = fftData[i]/fftData.length;
			if (fftData[i] != 0)
				fftData[i] = 20*Math.log10(fftData[i]);		// Power in dB of |fftData|
			
		}
		return fftData;		// output of FFT calculation
	}

	/**
	 * The reference bitreverse function.
	 */
	private static int bitreverseReference(int j, int nu) {
		int j2;
		int j1 = j;
		int k = 0;
		for (int i = 1; i <= nu; i++) {
			j2 = j1 / 2;
			k = 2 * k + j1 - 2 * j2;
			j1 = j2;
		}
		return k;
	}
}
