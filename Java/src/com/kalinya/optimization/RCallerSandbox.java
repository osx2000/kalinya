package com.kalinya.optimization;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;

public class RCallerSandbox {

	public static void main(String[] args) {
		RCaller caller = RCaller.create();
		RCode code = RCode.create();
		double[] xvector = new double[]{1,3,5,3,2,4}; 
		double[] yvector = new double[]{6,7,5,6,5,6}; 

		//caller.setRscriptExecutable("/usr/bin/Rscript"); 

		code.addDoubleArray("X", xvector); 
		code.addDoubleArray("Y", yvector); 
		code.addRCode("ols <- lm ( Y ~ X )"); 

		caller.setRCode(code); 

		caller.runAndReturnResult("ols"); 

		double[] residuals = 
				caller.getParser(). 
				getAsDoubleArray("residuals");  
	}

}
