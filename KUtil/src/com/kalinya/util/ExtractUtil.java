package com.kalinya.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;

public class ExtractUtil {
	/*
	 * CSV configuration
	 */
	private static final String NEW_LINE_SEPARATOR = "\n";
	public static final CSVFormat CSV_FILE_FORMAT = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
	
	public static void extractToCsv(String[] headers, double[][] matrix, String filePath) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		try {
			fileWriter = new FileWriter(filePath);
			csvFilePrinter = new CSVPrinter(fileWriter, CSV_FILE_FORMAT);
			csvFilePrinter.printRecord(Arrays.asList(headers));
			for(int i = 0; i < matrix.length; i++) {
				Double[] entryArray = ArrayUtils.toObject(matrix[i]);
				List<Double> entryList = Arrays.asList(entryArray);
				csvFilePrinter.printRecord(entryList);
			}
			System.out.println(String.format("Extracted data to [%s]", filePath));
		} catch (IOException e) {
			if(e.getMessage().contains("The process cannot access the file because it is being used by another process")) {
				//TODO: handle this
				//throw new FileLockedException(e);
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			PluginUtil.close(fileWriter);
			PluginUtil.close(csvFilePrinter);
		}
	}
	
	public static void extractToCsv(String[] headers, double[] vector, String filePath) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		try {
			fileWriter = new FileWriter(filePath);
			csvFilePrinter = new CSVPrinter(fileWriter, CSV_FILE_FORMAT);
			csvFilePrinter.printRecord(Arrays.asList(headers));
			Double[] entryArray = ArrayUtils.toObject(vector);
			List<Double> entryList = Arrays.asList(entryArray);
			csvFilePrinter.printRecord(entryList);
			System.out.println(String.format("Extracted data to [%s]", filePath));
		} catch (IOException e) {
			if(e.getMessage().contains("The process cannot access the file because it is being used by another process")) {
				//TODO: handle this
				//throw new FileLockedException(e);
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			PluginUtil.close(fileWriter);
			PluginUtil.close(csvFilePrinter);
		}
	}
}