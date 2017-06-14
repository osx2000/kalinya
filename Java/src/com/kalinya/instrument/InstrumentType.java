package com.kalinya.instrument;

public final class InstrumentType extends InstrumentData<InstrumentType> {
	
	private static final long serialVersionUID = 3807544273371905557L;
	public static final InstrumentType UNKNOWN = InstrumentType.create("UNKNOWN");
	
	public InstrumentType(String name) {
		super(name);
	}

	public static InstrumentType create(String name) {
		return new InstrumentType(name);
	}
}
