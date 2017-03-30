package com.kalinya.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kalinya.javafx.util.RowData;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.table.EnumColType;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.utility.EnumRefBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CollectionUtil {

	/**
	 * Assigns the specified value to each element of the specified range of the
	 * specified array of EnumRefBase. The range to be filled extends from index
	 * <tt>fromIndex</tt>, inclusive, to index <tt>toIndex</tt>, exclusive. (If
	 * <tt>fromIndex==toIndex</tt>, the range to be filled is empty.)
	 *
	 * @param a
	 *            the array to be filled
	 * @param fromIndex
	 *            the index of the first element (inclusive) to be filled with
	 *            the specified value
	 * @param toIndex
	 *            the index of the last element (exclusive) to be filled with
	 *            the specified value
	 * @param val
	 *            the value to be stored in all elements of the array
	 * @throws IllegalArgumentException
	 *             if <tt>fromIndex &gt; toIndex</tt>
	 * @throws ArrayIndexOutOfBoundsException
	 *             if <tt>fromIndex &lt; 0</tt> or
	 *             <tt>toIndex &gt; a.length</tt>
	 * @see java.util.Arrays#fill(int[], int, int, int)
	 */
	public static <T extends EnumRefBase> T[] fill(T[] a, int fromIndex, int toIndex, T val) {
		rangeCheck(a.length, fromIndex, toIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			a[i] = val;
		}
		return a;
	}

	/**
	 * Checks that {@code fromIndex} and {@code toIndex} are in
	 * the range and throws an appropriate exception, if they aren't.
	 */
	private static void rangeCheck(int length, int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException(
					"fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
		if (fromIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		}
		if (toIndex > length) {
			throw new ArrayIndexOutOfBoundsException(toIndex);
		}
	}

	/**
	 * Accepts list data and returns a table. If the first record in the list
	 * does not contain headers, the column name will be col_0, col_1, ... col_n-1
	 * 
	 * @param session
	 * @param tableAsList
	 * @return
	 */
	public static Table getListAsTable(Session session, List<List<String>> tableAsList, boolean hasHeaderRow) {
		if(tableAsList != null && tableAsList.size() > 0) {
			//Get the header row
			List<String> columnNames = null;
			if(hasHeaderRow) {
				columnNames = tableAsList.iterator().next();
			} else {
				columnNames = getStandardizedColumnNames(tableAsList.size());
			}
			int headerCount = columnNames.size();
			//Instantiate table with the same number of columns.
			Table table = session.getTableFactory().createTable();
			//Get the column names and add them to the table as String columns
			String[] names = columnNames.toArray(new String[columnNames.size()]);
			EnumColType[] types = new EnumColType[columnNames.size()]; 
			types = CollectionUtil.fill(types, 0, types.length, EnumColType.String);
			table.addColumns(names, types);

			boolean headerRecord = true;
			int record = 0;
			for(List<String> rowData: tableAsList) {
				if(headerRecord) {
					//skip header record
					headerRecord = false;
					continue;
				}
				if(rowData.size() != headerCount) {
					throw new IllegalStateException(String.format("Unexpected number of columns: record [%s], actual [%s], expected [%s]",record, rowData.size(),headerCount));
				}
				int rowId = table.addRows(1);
				if(rowId >= 0) {
					int colId = 0;
					for(String extractCellData: rowData) {
						table.setValue(colId, rowId, extractCellData);
						colId++;
					}
				}
				record++;
			}
			return table;
		} else {
			return null;
		}
	}

	private static List<String> getStandardizedColumnNames(int size) {
		List<String> columnNames = new ArrayList<>();
		int i = 0;
		while (i < size) {
			columnNames.add("col_" + i);
		}
		return columnNames;
	}

	public static ObservableList<RowData> getListAsObservableList(List<List<String>> tableAsList, boolean includesHeader, final int columnsToExtract) {
		if(tableAsList != null && tableAsList.size() > 0) {
			List<RowData> listOfRowData = getListOfRowData(tableAsList, includesHeader, columnsToExtract);
			ObservableList<RowData> list = FXCollections.observableArrayList(listOfRowData);
			int colId = 0;
			while(colId < columnsToExtract) {
				colId++;
			}
			return list;
		}
		return null;
	}

	private static List<RowData> getListOfRowData(List<List<String>> tableAsList, final boolean includesHeader, final int columnsToExtract) {
		List<RowData> listOfRowData = new ArrayList<>();
		boolean isHeader = includesHeader;
		for(List<String> rowDataAsList: tableAsList) {
			if(isHeader) {
				isHeader = false;
				continue;
			}
			int size = Math.min(columnsToExtract, rowDataAsList.size());
			String[] rowDataAsArray = rowDataAsList.toArray(new String[size]);
			String columnValue1 = rowDataAsArray[0];
			String columnValue2 = rowDataAsArray[1];
			RowData rowData = new RowData(columnValue1, columnValue2);
			listOfRowData.add(rowData);
		}
		return listOfRowData;
	}
	
	/**
	 * Returns the parameter Map sorted by the Map's Values
	 * 
	 * @param map
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for(Map.Entry<K, V> entry: list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
