package com.kalinya.results;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.performance.Instrument;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.DateUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

public class InstrumentResult implements Comparable<InstrumentResult> {
	Date date;
	Instrument instrument;
	InstrumentResultEnum instrumentResultEnum;
	BigDecimal value;
	
	
	private InstrumentResult(Date date, Instrument instrument, InstrumentResultEnum instrumentResultEnum, BigDecimal value) {
		this.date = DateUtil.getDate(date);
		this.instrument = instrument;
		this.instrumentResultEnum = instrumentResultEnum;
		this.value = value;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
				.append("Date", StringUtil.formatDate(date))
				.append("Instrument", (instrument != null ? instrument.getInstrumentId() : null))
				.append("ResultEnum", instrumentResultEnum)
				.append("Value", StringUtil.formatDouble(value))
				.build();
	}
	
	public Date getDate() {
		return date;
	}
	
	public Instrument getInstrument() {
		return instrument;
	}
	
	public InstrumentResultEnum getInstrumentResultEnum() {
		return instrumentResultEnum;
	}
	
	public BigDecimal getValue() {
		return value;
	}
	
	public static InstrumentResult create(Date date, Instrument instrument, InstrumentResultEnum instrumentResultEnum, BigDecimal value) {
		return new InstrumentResult(date, instrument, instrumentResultEnum, value);
	}

	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<InstrumentResult>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(date)
				.append(instrument)
				.append(instrumentResultEnum)
				.build();
	}
	
	@Override
	public int compareTo(InstrumentResult that) {
		return new CompareToBuilder()
				.append(date, that.date)
				.append(instrument, that.instrument)
				.append(instrumentResultEnum, that.instrumentResultEnum)
				.build();
	}
}
