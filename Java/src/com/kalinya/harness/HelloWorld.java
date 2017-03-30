package com.kalinya.harness;

import com.kalinya.performance.enums.AssetClass;


public class HelloWorld {

	public static void main(String[] args) {
		String s = "Hello world";
		System.out.println(s);
		
		AssetClass assetClass = AssetClass.UNKNOWN;
		System.out.println(String.format("AssetClass [%s]", assetClass.getName()));
	}
}