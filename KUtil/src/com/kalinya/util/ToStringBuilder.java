package com.kalinya.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToStringBuilder {
	private Object object;
	private String lineBreakCharacter = "";
	private List<String> stringList;
	private String separator = " ";

	public ToStringBuilder(Object object) {
		Assertions.notNull("Object", object);
		stringList = new ArrayList<>();
		this.object = object;
	}

	public ToStringBuilder append(final String fieldName, final int i) {
		append(String.format("%s [%s]", fieldName, ((Integer) i).toString()));
		return this;
	}

	public ToStringBuilder append(final String fieldName, final BigDecimal bd) {
		if(bd != null) {
			append(String.format("%s [%s]", fieldName, StringUtil.formatDouble(bd)));
		} else {
			appendNull(fieldName);
		}
		return this;
	}
	
	public ToStringBuilder append(final String fieldName, final Date date) {
		if(date != null) {
			append(String.format("%s [%s]", fieldName, StringUtil.formatDate(date)));
		} else {
			appendNull(fieldName);
		}
		return this;
	}

	public ToStringBuilder append(final String fieldName, final Object o) {
		if(o != null) {
			append(String.format("%s [%s]", fieldName, o.toString()));
		}
		return this;
	}
	
	private ToStringBuilder appendNull(String fieldName) {
		append(String.format("%s [null]", fieldName));
		return this;
	}
	
	public ToStringBuilder withClassName() {
		append(String.format("%s", object.getClass().getSimpleName()));
		return this;
	}
	
	public ToStringBuilder withLineBreaks() {
		return withLineBreaks(true);
	}
	
	public ToStringBuilder withLineBreaks(boolean withLineBreaks) {
		if(withLineBreaks) {
			lineBreakCharacter = "\n";
		} else {
			lineBreakCharacter = ",";
		}
		return this;
	}
	
	public ToStringBuilder withSeparator(String separator) {
		this.separator = separator;
		return this;
	}

	private void append(String string) {
		stringList.add(string);
	}
	
	@Override
	public String toString() {
		return build();
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		String separator = "";
		for(String string: stringList) {
			sb.append(separator + lineBreakCharacter + string);
			separator = this.separator;
		}
		return sb.toString();
	}
}
