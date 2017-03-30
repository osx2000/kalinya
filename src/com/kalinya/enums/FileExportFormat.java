package com.kalinya.enums;

public enum FileExportFormat {
	EXCEL("MS Excel", ".xlsx"), CSV("CSV", ".csv");

	private String name;
	private String fileExtension;

	FileExportFormat(String name, String fileExtension) {
		this.name = name;
		this.fileExtension = fileExtension;
	}

	public String getName() {
		return name;
	}

	public String getFileExtension() {
		return fileExtension;
	}

}