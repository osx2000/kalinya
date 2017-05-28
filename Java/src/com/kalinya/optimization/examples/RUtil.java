package com.kalinya.optimization.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.github.rcaller.TempFileService;
import com.github.rcaller.exception.ParseException;
import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.github.rcaller.rstuff.ROutputParser;
import com.kalinya.util.Assertions;

public class RUtil {

	/**
	 * Retrieves the content of a text file and returns it as a List<String>
	 * 
	 * @param filePath
	 *            The fully-qualified path to the file
	 * @return The file's content with each row represented as an element of the
	 *         List
	 */
	public static List<String> getImportedCode(String filePath) {
		Assertions.notNull(filePath, "ImportedCodeFilePath");
		List<String> fileContent = new ArrayList<>();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			String currentLine;
			br = new BufferedReader(new FileReader(filePath));
			while ((currentLine = br.readLine()) != null) {
				fileContent.add(currentLine);
			}
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Adds each element of the {@code linesOfCode} List to the {@code rCode}
	 * object
	 * 
	 * @param rCode
	 *            The RCaller.RCode object
	 * @param linesOfCode
	 * @see com.github.rcaller.rstuff.RCode#addRCode(String)
	 */
	public static void addRCode(RCode rCode, List<String> linesOfCode) {
		Assertions.notNullOrEmpty("ImportedCode", linesOfCode);
		Assertions.notNull("RCode", rCode);
		for(String lineOfCode: linesOfCode) {
			rCode.addRCode(lineOfCode);
		}
	}

	/**
	 * Removes values from the String array that apply to day/month/year and
	 * other inapplicable values to return only InstrumentIds
	 * 
	 * @param instrumentIds
	 * @return
	 */
	public static String[] removeInapplicableInstrumentsFromDataFrame(String[] instrumentIds) {
		List<String> newInstrumentIds = new ArrayList<>();
		Set<String> valuesToRemove = getInapplicableDataFrameValues();
		for(String instrumentId: instrumentIds) {
			if(!valuesToRemove.contains(instrumentId)) {
				newInstrumentIds.add(instrumentId);
			}
		}
		return newInstrumentIds.toArray(new String[newInstrumentIds.size()]);
	}

	private static Set<String> getInapplicableDataFrameValues() {
		Set<String> inapplicableDataFrameValues = new HashSet<>();
		inapplicableDataFrameValues.add("day");
		inapplicableDataFrameValues.add("month");
		inapplicableDataFrameValues.add("year");
		inapplicableDataFrameValues.add("crsp");
		return inapplicableDataFrameValues;
	}

	public static ArrayList<String> getRCallerTempFileNames(RCaller caller) {
		try {
			TempFileService tempFileService = (TempFileService) FieldUtils.readField(caller, "tempFileService", true);
			@SuppressWarnings("unchecked")
			ArrayList<String> tempFileNames = (ArrayList<String>) FieldUtils.readField(tempFileService, "tempFiles", true);
			return tempFileNames;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

	/**
	 * Returns the first entry in the array as an int.
	 * <p>
	 * Throws an IllegalArgumentException if the array is null or empty
	 * 
	 * @param caller
	 *            The RCaller object
	 * @param name
	 *            The name of the int field to retrieve from the RCaller Parser
	 * @return An int from the array
	 */
	public static int getAsInt(RCaller caller, String name) {
		int[] intArray = caller.getParser().getAsIntArray(name);
		Assertions.notNullOrEmpty("intArray", intArray);
		return intArray[0];
	}

	public static int getIndexFromArrayFindByValue(String[] strings, String lookupValue) {
		Assertions.notNullOrEmpty("strings", strings);
		for(int i = 0; i < strings.length; i++) {
			if(strings[i].equalsIgnoreCase(lookupValue)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns a multi-dimensional array of {@code double}s
	 * 
	 * <p>
	 * <b>Note</b> the RCaller output xml file registers the matrix values and
	 * there was an occasion when the Parser did not build the equivalent java
	 * double[][] correctly. The Parser unpacked the records from the xml file
	 * by filling down from the first column then across. The correct order of
	 * populating the POJO double[][] was to fill accross and then down.
	 * 
	 * @param parser
	 *            The RCaller Parser object
	 * @param name
	 *            The name of the R matrix field to retrieve from the RCaller Parser
	 * @param extractColumnValuesFirst
	 *            {@code true} Reverses the order in which the Parser unpacks
	 *            the multi-dimensional array of {@code double}s {@code false}
	 *            Uses the native RCaller.Parser
	 *            {@link com.github.rcaller.rstuff.ROutputParser#getAsDoubleMatrix(String)}
	 * @return
	 * @throws ParseException
	 */
	public static double[][] getAsDoubleMatrix(ROutputParser parser, String name, boolean extractColumnValuesFirst) throws ParseException {
		if(extractColumnValuesFirst) {
			int[] dims = parser.getDimensions(name);
			int n = dims[0];
			int m = dims[1];
			double[][] result = new double[n][m];
			double[] arr = parser.getAsDoubleArray(name);
			int c = 0;
			/*
			 * Note that this part loops first on the inner-most array dimension
			 * and then the outer-most array dimension
			 */
			for (int j = 0; j < m; j++) {
				for (int i = 0; i < n; i++) {
					result[i][j] = arr[c];
					c++;
				}
			}
			return (result);
		} else {
			return parser.getAsDoubleMatrix(name);
        }
    }
}
