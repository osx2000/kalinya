package com.kalinya.enums;

public enum ReportBuilderMode {
	COLLECT_METADATA(0, "Collect Metadata"), RUN_REPORT(1, "Run Report");
	
	private final int ordinal;
	private final String name;

	ReportBuilderMode(int ordinal, String name) {
		this.ordinal = ordinal;
		this.name = name;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String getName() {
		return name;
	}

	public static ReportBuilderMode fromString(String name) {
		if (name == null || name.length() < 1) {
			return null;
		}
		for (ReportBuilderMode instance : values()) {
			if (instance.getName().equals(name)) {
				return instance;
			}
		}
		throw new UnsupportedOperationException("Unsupported name [" + name + "] ");
	}

	public static ReportBuilderMode fromInt(int ordinal) {
		for (ReportBuilderMode instance : values()) {
			if (instance.getName().equals(ordinal)) {
				return instance;
			}
		}
		throw new UnsupportedOperationException("Unsupported ordinal [" + ordinal + "]");
	}
}
