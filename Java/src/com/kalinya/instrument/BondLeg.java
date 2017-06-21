package com.kalinya.instrument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.kalinya.oc.util.MessageLog;
import com.kalinya.util.StringUtil;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.enums.TRANF_FIELD;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.trading.EnumInstrumentFieldId;
import com.olf.openrisk.trading.EnumLegFieldId;
import com.olf.openrisk.trading.EnumResetDefinitionFieldId;
import com.olf.openrisk.trading.Instrument;
import com.olf.openrisk.trading.Leg;

@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlRootElement
public class BondLeg implements Comparable<BondLeg> {
	private MessageLog messageLog;
	private Session session;
	private Mapper mapper;
	private List<String> updates;
	private Instrument instrument;
	private String reference;
	private Leg leg;

	private String legId;

	private String fixFloat;
	private String paymentCurrency;
	private String projectionIndex;
	private String discountingIndex;
	private String startDate;
	private String endDate;
	private String notional;
	private String notionalCurrency;
	private String fixedRate;
	private String baseCpi; //Base CPI on IDX-BOND
	private String yieldBasis;
	private String indexLag;
	private String indexationType;
	private String interpolateMethod;
	private String indexTenor;
	private String dailyRefIndexDay;
	private String floatSpread;
	private String interestCapitalization;
	private String resetPeriod;
	private String paymentPeriod;
	private String compoundingPeriod;
	private String frontRollDate;
	private String backRollDate;
	private String holidaySchedules;
	private String paymentConvention;
	private String paymentType;
	private String paymentDateOffset;
	private String annuity;
	private String rollConvention;
	private String rounding;
	private String averagingType;
	private String resetPaymentPeriod;
	private String resetYieldBasis;
	private String resetHolidaySchedule;
	private String resetHolidayOption;
	private String resetRollDateOption;
	private String rateCutoff;
	private String referenceSource;
	private String profileCashflowType;
	private String compoundingType;
	private String profileEndDateAdjust;
	private String finalPrincipalPaymentConvention;
	
	//Secondary tab
	private String minSettlementSize;
	private String totalIssueSize;
	private String maxSettlementParcelSize;
	private String cashTranType;
	private String priceInputFormat;
	private String priceType;
	private String repoPriceType;
	private String buySellbackPriceType;
	private String repoFwdPriceRounding;
	private String priceRoundingType;
	private String yieldToPriceRounding;
	private String finalCouponYieldToPriceRounding;
	private String finalPrincipalRounding;
	private String marketPriceLookupType;
	private String marketPriceIndex;
	private String priceConversion;
	private String marketPriceGridpointInputEffective;
	private String creditClassification;
	private String guarantor;
	private String guarantorLegalEntity;
	private String creditRating;
	private String firstTradeDate;
	private String lastTradeDate;
	private String whenIssued;
	private String whenIssuedDate;
	private String onTheRun;
	private String exCouponDays;
	private String exCouponTreatment;
	private String yieldCalculationBasis;
	private String yieldCalculationMethod;
	private String yieldCompoundingFrequency;
	private String yieldToMethod;
	private String redemptionPrice;
	private String accrualCalculationBasis;
	private String accrualCalculationMethod;
	private String accrualDayAdjustment;
	private String accrualRounding;
	private String accrualRoundingMethod;
	private String repoIndex;
	private String finalCouponYieldMethod;
	private String sicCode;
	private String settleDiscountFlag;
	private String resetConvention;
	private String resetRateMethod;
	private String resetShift;
	private String rfiShift;
	private Map<EnumLegFieldId, String> fieldValueOverrides;

	public BondLeg() {
		updates = new ArrayList<String>();
	}

	public BondLeg(MessageLog messageLog, Mapper mapper, Instrument instrument, int legId) {
		this();
		this.messageLog = messageLog;
		this.session = messageLog.getSession();
		try {
			setFieldValueOverrides(mapper.getFieldValueOverrides());
			setLegId(String.valueOf(legId));
			setLeg(instrument.getLeg(legId));

			//Instrument fields
			setReference(instrument.getValueAsString(EnumInstrumentFieldId.ReferenceString));

			//Leg fields (Mostly.  Some are on the ResetDefinition)
			setFixFloat(getLegValueAsString(EnumLegFieldId.FixFloat));
			setPaymentCurrency(getLegValueAsString(EnumLegFieldId.Currency));
			setProjectionIndex(getLegValueAsString(EnumLegFieldId.ProjectionIndex));
			setDiscountingIndex(getLegValueAsString(EnumLegFieldId.DiscountIndex));
			setStartDate(getLegValueAsDate(EnumLegFieldId.StartDate));
			setEndDate(getLegValueAsDate(EnumLegFieldId.MaturityDate));
			setNotionalCurrency(getLegValueAsString(EnumLegFieldId.NotionalCurrency));
			setNotional(getLegValueAsString(EnumLegFieldId.Notional));
			setFixedRate(getLegValueAsString(EnumLegFieldId.Rate));
			//TODO: test this is the right value
			setRefValue(getLegValueAsString(EnumLegFieldId.IndexPercentage));
			setYieldBasis(getLegValueAsString(EnumLegFieldId.YieldBasis));
			
			setIndexLag(getLegValueAsString(EnumLegFieldId.IndexLag));
			setIndexationType(getLegValueAsString(TRANF_FIELD.TRANF_INDEXATION_TYPE));
			setInterpolateMethod(getLegValueAsString(EnumLegFieldId.InterpolateMethod));
			
			setIndexTenor(getLegValueAsString(EnumLegFieldId.ProjectionIndexTenor));
			setDailyRefIndexDay(getLegValueAsString(EnumLegFieldId.DailyRefIndexDay));
			setFloatSpread(getLegValueAsString(EnumLegFieldId.FloatSpread));
			setInterestCapitalization(getLegValueAsString(EnumLegFieldId.InterestCapitalization));
			setResetPeriod(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.Period));
			setPaymentPeriod(getLegValueAsString(EnumLegFieldId.PaymentPeriod));
			setResetShift(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.Shift));
			setRfiShift(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.RefIndexShift));
			setCompoundingPeriod(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.CompoundingPeriod));
			setFrontRollDate(getLegValueAsDate(EnumLegFieldId.ForwardRollDate));
			setBackRollDate(getLegValueAsDate(EnumLegFieldId.BackRollDate));
			//TODO: test this for multiple holiday schedules
			setHolidaySchedules(getLegValueAsString(EnumLegFieldId.HolidaySchedule));
			setPaymentConvention(getLegValueAsString(EnumLegFieldId.PaymentConvention));
			setPaymentType(getLegValueAsString(EnumLegFieldId.PaymentType));
			setPaymentDateOffset(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.PaymentDateOffset));
			setAnnuity(getLegValueAsString(EnumLegFieldId.Annuity));
			setRollConvention(getLegValueAsString(EnumLegFieldId.RollConvention));
			setResetConvention(getLegValueAsString(EnumLegFieldId.ResetConvention));
			setResetRateMethod(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.RateMethod));
			setAveragingType(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.AverageType));
			setResetPaymentPeriod(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.PaymentPeriod));
			setResetYieldBasis(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.YieldBasis));
			setResetHolidaySchedule(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.HolidayList));
			setResetHolidayOption(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.HolidayOption));
			setResetRollDateOption(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.RollDateOption));
			setRateCutoff(getLegValueAsString(EnumLegFieldId.RateCutOff));
			setProfileCashflowType(getLegValueAsString(EnumLegFieldId.CashflowType));
			setReferenceSource(getResetDefinitionValueAsString(EnumResetDefinitionFieldId.ReferenceSource));
			setRounding(getLegValueAsString(EnumLegFieldId.Rounding));
			setCompoundingType(getLegValueAsString(EnumLegFieldId.CompoundingType));
			
			setProfileEndDateAdjust(getLegValueAsString(EnumLegFieldId.ProfileEndDateAdjusted));
			setFinalPrincipalPaymentConvention(getLegValueAsString(EnumLegFieldId.FinalPrincipalPaymentConvention));
			setMinSettlementSize(getLegValueAsString(EnumLegFieldId.TickSize));
			setTotalIssueSize(getLegValueAsString(EnumLegFieldId.TotalIssueSize));
			setMaxSettlementParcelSize(getLegValueAsString(EnumLegFieldId.MaxSettlementParcelSize));
			setCashTranType(getLegValueAsString(EnumLegFieldId.CashTransactionType));
			setPriceInputFormat(getLegValueAsString(EnumLegFieldId.PriceInputFormat));
			setPriceType(getLegValueAsString(EnumLegFieldId.PriceType));
			setRepoPriceType(getLegValueAsString(EnumLegFieldId.RepoPriceType));
			setBuySellbackPriceType(getLegValueAsString(EnumLegFieldId.BuySellbackPriceType));
			setRepoFwdPriceRounding(getLegValueAsString(EnumLegFieldId.RepoForwardPriceRounding));
			setPriceRoundingType(getLegValueAsString(EnumLegFieldId.PriceRoundingType));
			setYieldToPriceRounding(getLegValueAsString(EnumLegFieldId.YieldtoPriceRounding));
			setFinalCouponYieldToPriceRounding(getLegValueAsString(EnumLegFieldId.FinalCouponYieldtoPriceRnding));
			setFinalPrincipalRounding(getLegValueAsString(EnumLegFieldId.FinalPrincipalRounding));
			setMarketPriceLookupType(getLegValueAsString(EnumLegFieldId.MarketPriceLookupType));
			setMarketPriceIndex(getLegValueAsString(EnumLegFieldId.MarketPriceIndex));
			setMarketPriceGridpointInputEffective(getLegValueAsString(EnumLegFieldId.MarketPriceGptInputEff));
			setPriceConversion(getLegValueAsString(EnumLegFieldId.UnderlyingPXMethod));
			setCreditClassification(getLegValueAsString(EnumLegFieldId.CreditClassification));
			setGuarantor(getLegValueAsString(EnumLegFieldId.Guarantor));
			setGuarantorLegalEntity(getLegValueAsString(EnumLegFieldId.GuarantorLEntity));
			setCreditRating(getLegValueAsString(EnumLegFieldId.CreditRating));
			setFirstTradeDate(getLegValueAsDate(EnumLegFieldId.FirstTradeDate));
			setLastTradeDate(getLegValueAsDate(EnumLegFieldId.LastTradeDate));
			setWhenIssued(getLegValueAsString(EnumLegFieldId.WhenIssued));
			setWhenIssuedDate(getLegValueAsDate(EnumLegFieldId.WhenIssuedDate));
			setOnTheRun(getLegValueAsString(EnumLegFieldId.OnTheRun));
			setExCouponDays(getLegValueAsString(EnumLegFieldId.ExCouponDays));
			setExCouponTreatment(getLegValueAsString(EnumLegFieldId.ExCouponTreatment));
			setYieldCalculationBasis(getLegValueAsString(EnumLegFieldId.YieldCalculationBasis));
			setYieldCalculationMethod(getLegValueAsString(EnumLegFieldId.YieldCalculationMethod));
			setYieldCompoundingFrequency(getLegValueAsString(EnumLegFieldId.YieldCompoundingFrequency));
			setYieldToMethod(getLegValueAsString(EnumLegFieldId.YieldToMethod));
			setRedemptionPrice(getLegValueAsString(EnumLegFieldId.RedemptionPrice));
			setAccrualCalculationBasis(getLegValueAsString(EnumLegFieldId.AccrualCalculationBasis));
			setAccrualCalculationMethod(getLegValueAsString(EnumLegFieldId.AccrualCalculationMethod));
			setAccrualDayAdjustment(getLegValueAsString(EnumLegFieldId.AccrualDayAdjust));
			setAccrualRounding(getLegValueAsString(EnumLegFieldId.AccrualRounding));
			setAccrualRoundingMethod(getLegValueAsString(EnumLegFieldId.AccrualRoundingMethod));
			setRepoIndex(getLegValueAsString(EnumLegFieldId.TransactionIndex));
			setFinalCouponYieldMethod(getLegValueAsString(EnumLegFieldId.FinalCouponYieldMethod));
			setSicCode(getLegValueAsString(EnumLegFieldId.SICCode));
			setSettleDiscountFlag(getLegValueAsString(EnumLegFieldId.SettlementDiscountFlag));
		} catch (Exception e) {
			messageLog.logException(e);
		} finally {
		}
	}

	@XmlTransient
	public void setFieldValueOverrides(Map<EnumLegFieldId, String> fieldValueOverrides) {
		this.fieldValueOverrides = fieldValueOverrides;
	}

	private Leg getLeg() {
		return leg;
	}
	
	@XmlTransient
	private void setLeg(Leg leg) {
		this.leg = leg;
	}
	
	private String getLegValueAsString(EnumLegFieldId fieldId) {
		if(leg.isApplicable(fieldId)) {
			if(getFieldValueOverrides().containsKey(fieldId)) {
				return getFieldValueOverrides().get(fieldId);
			}
			return leg.getValueAsString(fieldId);
		}
		return null;
	}
	
	private Map<EnumLegFieldId, String> getFieldValueOverrides() {
		return fieldValueOverrides;
	}

	private String getLegValueAsString(TRANF_FIELD jvsTranField) {
		try {
			switch(jvsTranField) {
			case TRANF_INDEXATION_TYPE:
				if(Integer.valueOf(getLegId()) == 1) {
					com.olf.openjvs.Transaction jvsTransaction = getJvsTransaction();
					return jvsTransaction.getField(jvsTranField.toInt(), Integer.valueOf(getLegId()));
				}
			default:
				break;
			}
		} catch (NumberFormatException | OException e) {
			throw new OpenRiskException(e);
		}
		return null;
	}
	
	private com.olf.openjvs.Transaction getJvsTransaction() {
		if(instrument == null) {
			instrument = (Instrument) leg.getParent();
		}
		return getSession().getTradingFactory().toOpenJvs(instrument.getTransaction());
	}

	private String getLegValueAsDate(EnumLegFieldId fieldId) {
		if(leg.isApplicable(fieldId)) {
			return StringUtil.formatInternationalDate(leg.getValueAsDate(fieldId));
		}
		return null;
	}

	private String getResetDefinitionValueAsString(EnumResetDefinitionFieldId fieldId) {
		if(leg.getResetDefinition().isApplicable(fieldId)) {
			return leg.getResetDefinition().getValueAsString(fieldId);
		}
		return null;
	}

	/*public Object push(MessageLog messageLog, Mapper mapper) {
		setMapper(mapper);
		return push(messageLog);
	}*/

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	private Map<String,String> getIndexMap() {
		return mapper.getIndexMap();
	}

	private Map<String, String> getHolidayScheduleMap() {
		return mapper.getHolidayScheduleMap();
	}

	private String getMappedValue(EnumLegFieldId fieldId, String value) {
		//Override index fields
		switch(fieldId) {
		case DiscountIndex:
		case MarketPriceIndex:
		case TransactionIndex:
			if(getIndexMap() != null) {
				String newFieldValue = getIndexMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		case HolidaySchedule:
			if(getHolidayScheduleMap() != null) {
				String newFieldValue = getHolidayScheduleMap().get(value);
				if(newFieldValue != null) {
					value = newFieldValue;
				}
			}
			break;
		default:
			break;
		}
		return value;
	}

	private void setMessageLog(MessageLog messageLog) {
		if(this.messageLog == null) {
			this.messageLog = messageLog;
		}
	}

	private void setSession(Session session) {
		if(this.session == null) {
			this.session = session;
		}
	}

	public final MessageLog getMessageLog() {
		return messageLog;
	}

	public final Session getSession() {
		return session;
	}

	public String getPaymentCurrency() {
		return paymentCurrency;
	}

	@XmlElement
	public void setPaymentCurrency(String paymentCurrency) {
		this.paymentCurrency = paymentCurrency;
	}


	public String getFixedRate() {
		return fixedRate;
	}

	@XmlElement
	public void setFixedRate(String fixedRate) {
		this.fixedRate = fixedRate;
	}
	
	/**
	 * Inflation linked bond base CPI, uses OC enum EnumLegFieldId.IndexPercentage
	 * @return
	 */
	public String getRefValue() {
		return baseCpi;
	}
	
	public void setRefValue(String baseCpi) {
		this.baseCpi = baseCpi;
	}

	public String toString() {
		return "LegId [" + getLegId() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BondLeg)) {
			return false;
		}
		return this.compareTo((BondLeg) obj) == 0;
	}

	@Override
	public int compareTo(BondLeg that) {
		if (this == that) {
			return 0;
		}

		int i = getReference().compareTo(that.getReference());
		if(i != 0) return i;

		i = getLegId().compareTo(that.getLegId());
		if(i != 0) return i;

		return 0;
	}

	public String getReference() {
		return reference;
	}

	private void setReference(String reference) {
		this.reference = reference;
	}

	public String getFixFloat() {
		return fixFloat;
	}

	@XmlElement
	public void setFixFloat(String fixFloat) {
		this.fixFloat = fixFloat;
	}

	public String getProjectionIndex() {
		return projectionIndex;
	}
	
	@XmlElement
	public void setProjectionIndex(String projectionIndex) {
		this.projectionIndex = projectionIndex;
	}
	
	public String getDiscountingIndex() {
		return discountingIndex;
	}

	@XmlElement
	public void setDiscountingIndex(String discountingIndex) {
		this.discountingIndex = discountingIndex;
	}

	public String getStartDate() {
		return startDate;
	}

	@XmlElement
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	@XmlElement
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getNotional() {
		return notional;
	}

	@XmlElement
	public void setNotional(String notional) {
		this.notional = notional;
	}

	public String getNotionalCurrency() {
		return notionalCurrency;
	}

	@XmlElement
	public void setNotionalCurrency(String notionalCurrency) {
		this.notionalCurrency = notionalCurrency;
	}

	public String getYieldBasis() {
		return yieldBasis;
	}

	@XmlElement
	public void setYieldBasis(String yieldBasis) {
		this.yieldBasis = yieldBasis;
	}

	public String getIndexLag() {
		return indexLag;
	}

	@XmlElement
	public void setIndexLag(String indexLag) {
		this.indexLag = indexLag;
	}

	public String getIndexationType() {
		return indexationType;
	}

	@XmlElement
	public void setIndexationType(String indexationType) {
		this.indexationType = indexationType;
	}

	public String getInterpolateMethod() {
		return interpolateMethod;
	}

	@XmlElement
	public void setInterpolateMethod(String interpolateMethod) {
		this.interpolateMethod = interpolateMethod;
	}
	
	/**
	 * The tenor of the projection index reference rate.  Uses EnumLegFieldId.ProjectionIndexTenor
	 * 
	 * @return
	 */
	public String getIndexTenor() {
		return indexTenor;
	}
	
	@XmlElement
	public void setIndexTenor(String indexTenor) {
		this.indexTenor = indexTenor;
	}
	
	public String getDailyRefIndexDay() {
		return dailyRefIndexDay;
	}
	
	@XmlElement
	public void setDailyRefIndexDay(String dailyRefIndexDay) {
		this.dailyRefIndexDay = dailyRefIndexDay;
	}
	
	public String getFloatSpread() {
		return floatSpread;
	}
	
	@XmlElement
	public void setFloatSpread(String floatSpread) {
		this.floatSpread = floatSpread;		
	}
	
	public String getInterestCapitalization() {
		return interestCapitalization;
	}
	
	@XmlElement
	public void setInterestCapitalization(String interestCapitalization) {
		this.interestCapitalization = interestCapitalization;
	}
	
	public String getResetPeriod() {
		return resetPeriod;
	}
	
	@XmlElement
	public void setResetPeriod(String resetPeriod) {
		this.resetPeriod = resetPeriod;
	}

	public String getPaymentPeriod() {
		return paymentPeriod;
	}

	@XmlElement
	public void setPaymentPeriod(String paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}
	
	public String getResetShift() {
		return resetShift;
	}
	
	@XmlElement
	public void setResetShift(String resetShift) {
		this.resetShift = resetShift;
	}

	public String getRfiShift() {
		return rfiShift;
	}
	
	@XmlElement
	public void setRfiShift(String rfiShift) {
		this.rfiShift = rfiShift;
	}

	public String getCompoundingPeriod() {
		return compoundingPeriod;
	}

	@XmlElement
	public void setCompoundingPeriod(String compoundingPeriod) {
		this.compoundingPeriod = compoundingPeriod;
	}

	public String getFrontRollDate() {
		return frontRollDate;
	}

	@XmlElement
	public void setFrontRollDate(String frontRollDate) {
		this.frontRollDate = frontRollDate;
	}

	public String getBackRollDate() {
		return backRollDate;
	}

	@XmlElement
	public void setBackRollDate(String backRollDate) {
		this.backRollDate = backRollDate;
	}

	public String getHolidaySchedules() {
		return holidaySchedules;
	}

	@XmlElement
	public void setHolidaySchedules(String holidaySchedules) {
		this.holidaySchedules = holidaySchedules;
	}

	public String getPaymentConvention() {
		return paymentConvention;
	}

	@XmlElement
	public void setPaymentConvention(String paymentConvention) {
		this.paymentConvention = paymentConvention;
	}

	public String getPaymentType() {
		return paymentType;
	}

	@XmlElement
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentDateOffset() {
		return paymentDateOffset;
	}

	@XmlElement
	public void setPaymentDateOffset(String paymentDateOffset) {
		this.paymentDateOffset = paymentDateOffset;
	}

	public String getAnnuity() {
		return annuity;
	}

	@XmlElement
	public void setAnnuity(String annuity) {
		this.annuity = annuity;
	}

	public String getRollConvention() {
		return rollConvention;
	}

	@XmlElement
	public void setRollConvention(String rollConvention) {
		this.rollConvention = rollConvention;
	}

	public String getRounding() {
		return rounding;
	}

	@XmlElement
	public void setRounding(String rounding) {
		this.rounding = rounding;
	}

	public String getResetConvention() {
		return resetConvention;
	}
	
	@XmlElement
	public void setResetConvention(String resetConvention) {
		this.resetConvention =  resetConvention;		
	}
	
	public String getResetRateMethod() {
		return resetRateMethod;
	}

	@XmlElement
	public void setResetRateMethod(String resetRateMethod) {
		this.resetRateMethod = resetRateMethod;
	}

	public String getAveragingType() {
		return averagingType;
	}
	
	@XmlElement
	public void setAveragingType(String averagingType) {
		this.averagingType = averagingType;
	}

	public String getResetPaymentPeriod() {
		return resetPaymentPeriod;
	}
	
	@XmlElement
	public void setResetPaymentPeriod(String resetPaymentPeriod) {
		this.resetPaymentPeriod = resetPaymentPeriod;
	}

	public String getResetYieldBasis() {
		return resetYieldBasis;
	}
	
	@XmlElement
	public void setResetYieldBasis(String resetYieldBasis) {
		this.resetYieldBasis = resetYieldBasis;
	}

	public String getResetHolidaySchedule() {
		return resetHolidaySchedule;
	}
	
	@XmlElement
	public void setResetHolidaySchedule(String resetHolidaySchedule) {
		this.resetHolidaySchedule = resetHolidaySchedule;
	}

	public String getResetHolidayOption() {
		return resetHolidayOption;
	}
	
	@XmlElement
	public void setResetHolidayOption(String resetHolidayOption) {
		this.resetHolidayOption = resetHolidayOption;
	}

	public String getResetRollDateOption() {
		return resetRollDateOption;
	}
	
	@XmlElement
	public void setResetRollDateOption(String resetRollDateOption) {
		this.resetRollDateOption = resetRollDateOption;
	}

	public String getRateCutoff() {
		return rateCutoff;
	}
	
	@XmlElement
	public void setRateCutoff(String rateCutoff) {
		this.rateCutoff = rateCutoff;
	}

	public String getReferenceSource() {
		return referenceSource;
	}
	
	@XmlElement
	public void setReferenceSource(String referenceSource) {
		this.referenceSource = referenceSource;
	}
	
	public String getProfileCashflowType() {
		return profileCashflowType;
	}

	@XmlElement
	public void setProfileCashflowType(String profileCashflowType) {
		this.profileCashflowType = profileCashflowType;
	}

	public String getCompoundingType() {
		return compoundingType;
	}
	
	@XmlElement
	public void setCompoundingType(String compoundingType) {
		this.compoundingType = compoundingType;
	}
	
	public String getProfileEndDateAdjust() {
		return profileEndDateAdjust;
	}
	
	@XmlElement
	public void setProfileEndDateAdjust(String profileEndDateAdjust) {
		this.profileEndDateAdjust = profileEndDateAdjust;
	}

	public String getFinalPrincipalPaymentConvention() {
		return finalPrincipalPaymentConvention;
	}

	@XmlElement
	public void setFinalPrincipalPaymentConvention(
			String finalPrincipalPaymentConvention) {
		this.finalPrincipalPaymentConvention = finalPrincipalPaymentConvention;
	}

	public String getMinSettlementSize() {
		return minSettlementSize;
	}

	@XmlElement
	public void setMinSettlementSize(String minSettlementSize) {
		this.minSettlementSize = minSettlementSize;
	}

	public final String getTotalIssueSize() {
		return totalIssueSize;
	}

	@XmlElement
	public final void setTotalIssueSize(String totalIssueSize) {
		this.totalIssueSize = totalIssueSize;
	}

	public final String getMaxSettlementParcelSize() {
		return maxSettlementParcelSize;
	}

	@XmlElement
	public final void setMaxSettlementParcelSize(String maxSettlementParcelSize) {
		this.maxSettlementParcelSize = maxSettlementParcelSize;
	}

	public final String getCashTranType() {
		return cashTranType;
	}

	@XmlElement
	public final void setCashTranType(String cashTranType) {
		this.cashTranType = cashTranType;
	}

	public final String getPriceInputFormat() {
		return priceInputFormat;
	}

	@XmlElement
	public final void setPriceInputFormat(String priceInputFormat) {
		this.priceInputFormat = priceInputFormat;
	}

	public final String getPriceType() {
		return priceType;
	}

	@XmlElement
	public final void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public final String getRepoPriceType() {
		return repoPriceType;
	}

	@XmlElement
	public final void setRepoPriceType(String repoPriceType) {
		this.repoPriceType = repoPriceType;
	}

	public final String getBuySellbackPriceType() {
		return buySellbackPriceType;
	}

	@XmlElement
	public final void setBuySellbackPriceType(String buySellbackPriceType) {
		this.buySellbackPriceType = buySellbackPriceType;
	}

	public final String getRepoFwdPriceRounding() {
		return repoFwdPriceRounding;
	}

	@XmlElement
	public final void setRepoFwdPriceRounding(String repoFwdPriceRounding) {
		this.repoFwdPriceRounding = repoFwdPriceRounding;
	}

	public final String getPriceRoundingType() {
		return priceRoundingType;
	}

	@XmlElement
	public final void setPriceRoundingType(String priceRoundingType) {
		this.priceRoundingType = priceRoundingType;
	}

	public final String getYieldToPriceRounding() {
		return yieldToPriceRounding;
	}

	@XmlElement
	public final void setYieldToPriceRounding(String yieldToPriceRounding) {
		this.yieldToPriceRounding = yieldToPriceRounding;
	}

	public final String getFinalCouponYieldToPriceRounding() {
		return finalCouponYieldToPriceRounding;
	}

	@XmlElement
	public final void setFinalCouponYieldToPriceRounding(
			String finalCouponYieldToPriceRounding) {
		this.finalCouponYieldToPriceRounding = finalCouponYieldToPriceRounding;
	}

	public final String getFinalPrincipalRounding() {
		return finalPrincipalRounding;
	}

	@XmlElement
	public final void setFinalPrincipalRounding(String finalPrincipalRounding) {
		this.finalPrincipalRounding = finalPrincipalRounding;
	}

	public final String getMarketPriceLookupType() {
		return marketPriceLookupType;
	}

	@XmlElement
	public final void setMarketPriceLookupType(String marketPriceLookupType) {
		this.marketPriceLookupType = marketPriceLookupType;
	}

	public final String getMarketPriceIndex() {
		return marketPriceIndex;
	}

	@XmlElement
	public final void setMarketPriceIndex(String marketPriceIndex) {
		this.marketPriceIndex = marketPriceIndex;
	}

	/**
	 * GUI = PX Conversion, OC enum = EnumLegFieldId.UnderlyingPXMethod
	 * 
	 * @return
	 */
	public final String getPriceConversion() {
		return priceConversion;
	}

	/**
	 * GUI = PX Conversion, OC enum = EnumLegFieldId.UnderlyingPXMethod
	 * 
	 * @param priceConversion
	 */
	@XmlElement
	public final void setPriceConversion(String priceConversion) {
		this.priceConversion = priceConversion;
	}

	public final String getMarketPriceGridpointInputEffective() {
		return marketPriceGridpointInputEffective;
	}

	@XmlElement
	public final void setMarketPriceGridpointInputEffective(
			String marketPriceGridpointInputEffective) {
		this.marketPriceGridpointInputEffective = marketPriceGridpointInputEffective;
	}

	public final String getCreditClassification() {
		return creditClassification;
	}

	@XmlElement
	public final void setCreditClassification(String creditClassification) {
		this.creditClassification = creditClassification;
	}

	public final String getGuarantor() {
		return guarantor;
	}

	@XmlElement
	public final void setGuarantor(String guarantor) {
		this.guarantor = guarantor;
	}

	public final String getGuarantorLegalEntity() {
		return guarantorLegalEntity;
	}

	@XmlElement
	public final void setGuarantorLegalEntity(String guarantorLegalEntity) {
		this.guarantorLegalEntity = guarantorLegalEntity;
	}

	public final String getCreditRating() {
		return creditRating;
	}

	@XmlElement
	public final void setCreditRating(String creditRating) {
		this.creditRating = creditRating;
	}

	public final String getFirstTradeDate() {
		return firstTradeDate;
	}

	@XmlElement
	public final void setFirstTradeDate(String firstTradeDate) {
		this.firstTradeDate = firstTradeDate;
	}

	public final String getLastTradeDate() {
		return lastTradeDate;
	}

	@XmlElement
	public final void setLastTradeDate(String lastTradeDate) {
		this.lastTradeDate = lastTradeDate;
	}

	public final String getWhenIssued() {
		return whenIssued;
	}

	@XmlElement
	public final void setWhenIssued(String whenIssued) {
		this.whenIssued = whenIssued;
	}

	public final String getWhenIssuedDate() {
		return whenIssuedDate;
	}

	@XmlElement
	public final void setWhenIssuedDate(String whenIssuedDate) {
		this.whenIssuedDate = whenIssuedDate;
	}

	public final String getOnTheRun() {
		return onTheRun;
	}

	@XmlElement
	public final void setOnTheRun(String onTheRun) {
		this.onTheRun = onTheRun;
	}

	public final String getExCouponDays() {
		return exCouponDays;
	}

	@XmlElement
	public final void setExCouponDays(String exCouponDays) {
		this.exCouponDays = exCouponDays;
	}

	public final String getExCouponTreatment() {
		return exCouponTreatment;
	}

	@XmlElement
	public final void setExCouponTreatment(String exCouponTreatment) {
		this.exCouponTreatment = exCouponTreatment;
	}

	public final String getYieldCalculationBasis() {
		return yieldCalculationBasis;
	}

	@XmlElement
	public final void setYieldCalculationBasis(String yieldCalculationBasis) {
		this.yieldCalculationBasis = yieldCalculationBasis;
	}

	public final String getYieldCalculationMethod() {
		return yieldCalculationMethod;
	}

	@XmlElement
	public final void setYieldCalculationMethod(String yieldCalculationMethod) {
		this.yieldCalculationMethod = yieldCalculationMethod;
	}

	public final String getYieldCompoundingFrequency() {
		return yieldCompoundingFrequency;
	}

	@XmlElement
	public final void setYieldCompoundingFrequency(String yieldCompoundingFrequency) {
		this.yieldCompoundingFrequency = yieldCompoundingFrequency;
	}

	public final String getYieldToMethod() {
		return yieldToMethod;
	}

	@XmlElement
	public final void setYieldToMethod(String yieldToMethod) {
		this.yieldToMethod = yieldToMethod;
	}

	public final String getRedemptionPrice() {
		return redemptionPrice;
	}

	@XmlElement
	public final void setRedemptionPrice(String redemptionPrice) {
		this.redemptionPrice = redemptionPrice;
	}

	public final String getAccrualCalculationBasis() {
		return accrualCalculationBasis;
	}

	@XmlElement
	public final void setAccrualCalculationBasis(String accrualCalculationBasis) {
		this.accrualCalculationBasis = accrualCalculationBasis;
	}

	public final String getAccrualCalculationMethod() {
		return accrualCalculationMethod;
	}

	@XmlElement
	public final void setAccrualCalculationMethod(String accrualCalculationMethod) {
		this.accrualCalculationMethod = accrualCalculationMethod;
	}

	public final String getAccrualDayAdjustment() {
		return accrualDayAdjustment;
	}

	@XmlElement
	public final void setAccrualDayAdjustment(String accrualDayAdjustment) {
		this.accrualDayAdjustment = accrualDayAdjustment;
	}

	public final String getAccrualRounding() {
		return accrualRounding;
	}

	@XmlElement
	public final void setAccrualRounding(String accrualRounding) {
		this.accrualRounding = accrualRounding;
	}

	public final String getAccrualRoundingMethod() {
		return accrualRoundingMethod;
	}

	@XmlElement
	public final void setAccrualRoundingMethod(String accrualRoundingMethod) {
		this.accrualRoundingMethod = accrualRoundingMethod;
	}

	public final String getRepoIndex() {
		return repoIndex;
	}

	@XmlElement
	public final void setRepoIndex(String repoIndex) {
		this.repoIndex = repoIndex;
	}

	public final String getFinalCouponYieldMethod() {
		return finalCouponYieldMethod;
	}

	@XmlElement
	public final void setFinalCouponYieldMethod(String finalCouponYieldMethod) {
		this.finalCouponYieldMethod = finalCouponYieldMethod;
	}

	public final String getSicCode() {
		return sicCode;
	}

	@XmlElement
	public final void setSicCode(String sicCode) {
		this.sicCode = sicCode;
	}

	public final String getSettleDiscountFlag() {
		return settleDiscountFlag;
	}

	@XmlElement
	public final void setSettleDiscountFlag(String settleDiscountFlag) {
		this.settleDiscountFlag = settleDiscountFlag;
	}

	public String getLegId() {
		return legId;
	}

	@XmlAttribute
	public void setLegId(String legId) {
		this.legId = legId;
	}

	public List<String> getUpdates() {
		return updates;
	}
	
	/**
	 * Set fields on the instrument leg
	 * 
	 * @param leg
	 * @param mapper 
	 * @return
	 */
	public List<String> pushInstrumentLegUpdates(MessageLog messageLog, Mapper mapper, Leg leg) {
		setMessageLog(messageLog);
		setSession(getMessageLog().getSession());
		setMapper(mapper);
		setLeg(leg);
		getUpdates().clear();
		update(EnumLegFieldId.FixFloat, getFixFloat());
		update(EnumLegFieldId.Currency, getPaymentCurrency());
		update(EnumLegFieldId.ProjectionIndex, getProjectionIndex());
		update(EnumLegFieldId.DiscountIndex, getDiscountingIndex());
		updateDate(EnumLegFieldId.StartDate, getStartDate());
		updateDate(EnumLegFieldId.MaturityDate, getEndDate());
		update(EnumLegFieldId.NotionalCurrency, getNotionalCurrency());
		update(EnumLegFieldId.Notional, getNotional());
		update(EnumLegFieldId.Rate, getFixedRate());
		update(EnumLegFieldId.IndexPercentage, getRefValue());
		update(EnumLegFieldId.YieldBasis, getYieldBasis());
		update(EnumLegFieldId.IndexLag, getIndexLag());
		update(TRANF_FIELD.TRANF_INDEXATION_TYPE, getIndexationType());
		update(EnumLegFieldId.InterpolateMethod, getInterpolateMethod());
		update(EnumLegFieldId.ProjectionIndexTenor, getIndexTenor());
		update(EnumLegFieldId.DailyRefIndexDay, getDailyRefIndexDay());
		update(EnumLegFieldId.FloatSpread, getFloatSpread());
		update(EnumLegFieldId.InterestCapitalization, getInterestCapitalization());
		update(EnumResetDefinitionFieldId.Period, getResetPeriod());
		update(EnumLegFieldId.PaymentPeriod, getPaymentPeriod());
		update(EnumResetDefinitionFieldId.Shift, getResetShift());
		update(EnumResetDefinitionFieldId.RefIndexShift, getRfiShift());
		update(EnumResetDefinitionFieldId.CompoundingPeriod, getCompoundingPeriod());
		updateDate(EnumLegFieldId.ForwardRollDate, getFrontRollDate());
		updateDate(EnumLegFieldId.BackRollDate, getBackRollDate());
		update(EnumLegFieldId.HolidaySchedule, getHolidaySchedules());
		update(EnumLegFieldId.PaymentConvention, getPaymentConvention());
		update(EnumLegFieldId.PaymentType, getPaymentType());
		update(EnumResetDefinitionFieldId.PaymentDateOffset, getPaymentDateOffset());
		update(EnumLegFieldId.Annuity, getAnnuity());
		update(EnumLegFieldId.RollConvention, getRollConvention());
		update(EnumLegFieldId.ResetConvention, getResetConvention());
		update(EnumResetDefinitionFieldId.RateMethod, getResetRateMethod());
		update(EnumResetDefinitionFieldId.AverageType, getAveragingType());
		update(EnumResetDefinitionFieldId.PaymentPeriod, getResetPaymentPeriod());
		update(EnumResetDefinitionFieldId.YieldBasis, getResetYieldBasis());
		update(EnumResetDefinitionFieldId.HolidayList, getResetHolidaySchedule());
		update(EnumResetDefinitionFieldId.HolidayOption, getResetHolidayOption());
		update(EnumResetDefinitionFieldId.RollDateOption, getResetRollDateOption());
		update(EnumLegFieldId.RateCutOff, getRateCutoff());
		update(EnumLegFieldId.CashflowType, getProfileCashflowType());
		update(EnumResetDefinitionFieldId.ReferenceSource, getReferenceSource());
		update(EnumLegFieldId.Rounding, getRounding());
		update(EnumLegFieldId.CompoundingType, getCompoundingType());
		update(EnumLegFieldId.ProfileEndDateAdjusted, getProfileEndDateAdjust());
		update(EnumLegFieldId.FinalPrincipalPaymentConvention, getFinalPrincipalPaymentConvention());
		update(EnumLegFieldId.TickSize, getMinSettlementSize());
		update(EnumLegFieldId.TotalIssueSize, getTotalIssueSize());
		update(EnumLegFieldId.MaxSettlementParcelSize, getMaxSettlementParcelSize());
		update(EnumLegFieldId.CashTransactionType, getCashTranType());
		update(EnumLegFieldId.PriceType, getPriceType());
		update(EnumLegFieldId.RepoPriceType, getRepoPriceType());
		update(EnumLegFieldId.BuySellbackPriceType, getBuySellbackPriceType());
		update(EnumLegFieldId.RepoForwardPriceRounding, getRepoFwdPriceRounding());
		update(EnumLegFieldId.PriceRoundingType, getPriceRoundingType());
		update(EnumLegFieldId.YieldtoPriceRounding, getYieldToPriceRounding());
		update(EnumLegFieldId.FinalCouponYieldtoPriceRnding, getFinalCouponYieldToPriceRounding());
		update(EnumLegFieldId.FinalPrincipalRounding, getFinalPrincipalRounding());
		update(EnumLegFieldId.MarketPriceLookupType, getMarketPriceLookupType());
		update(EnumLegFieldId.MarketPriceIndex, getMarketPriceIndex());
		update(EnumLegFieldId.MarketPriceGptInputEff, getMarketPriceGridpointInputEffective());
		update(EnumLegFieldId.UnderlyingPXMethod, getPriceConversion());
		update(EnumLegFieldId.Guarantor, getGuarantor());
		update(EnumLegFieldId.GuarantorLEntity, getGuarantorLegalEntity());
		//TODO: support CreditRating 
		//update(EnumLegFieldId.CreditClassification, getCreditClassification());
		//update(EnumLegFieldId.CreditRating, getCreditRating());
		updateDate(EnumLegFieldId.FirstTradeDate, getFirstTradeDate());
		updateDate(EnumLegFieldId.LastTradeDate, getLastTradeDate());
		update(EnumLegFieldId.WhenIssued, getWhenIssued());
		updateDate(EnumLegFieldId.WhenIssuedDate, getWhenIssuedDate());
		update(EnumLegFieldId.OnTheRun, getOnTheRun());
		update(EnumLegFieldId.ExCouponDays, getExCouponDays());
		update(EnumLegFieldId.ExCouponTreatment, getExCouponTreatment());
		update(EnumLegFieldId.YieldCalculationBasis, getYieldCalculationBasis());
		update(EnumLegFieldId.YieldCalculationMethod, getYieldCalculationMethod());
		update(EnumLegFieldId.YieldCompoundingFrequency, getYieldCompoundingFrequency());
		update(EnumLegFieldId.YieldToMethod, getYieldToMethod());
		update(EnumLegFieldId.RedemptionPrice, getRedemptionPrice());
		update(EnumLegFieldId.AccrualCalculationBasis, getAccrualCalculationBasis());
		update(EnumLegFieldId.AccrualCalculationMethod, getAccrualCalculationMethod());
		update(EnumLegFieldId.AccrualDayAdjust, getAccrualDayAdjustment());
		update(EnumLegFieldId.AccrualRounding, getAccrualRounding());
		update(EnumLegFieldId.AccrualRoundingMethod, getAccrualRoundingMethod());
		update(EnumLegFieldId.TransactionIndex, getRepoIndex());
		update(EnumLegFieldId.FinalCouponYieldMethod, getFinalCouponYieldMethod());
		update(EnumLegFieldId.SICCode, getSicCode());
		update(EnumLegFieldId.SettlementDiscountFlag, getSettleDiscountFlag());
		
		return getUpdates();
	}
	
	private void update(EnumResetDefinitionFieldId fieldId, String value) {
		if(value != null && getLeg().getResetDefinition().isApplicable(fieldId)) {
			if(!getLeg().getResetDefinition().getValueAsString(fieldId).equalsIgnoreCase(value)) {
				getLeg().getResetDefinition().setValue(fieldId, value);
				getUpdates().add("Leg" + getLegId() + "EnumResetDefinitionFieldId." + fieldId.toString());
			}
		}
	}
	
	private void update(EnumLegFieldId fieldId, String value) {
		if(value != null && getLeg().isApplicable(fieldId)) {
				value = getMappedValue(fieldId, value);
				if(!getLeg().getValueAsString(fieldId).equalsIgnoreCase(value)) {
					getLeg().setValue(fieldId, value);
					getUpdates().add("Leg" + getLegId() + ".EnumLegFieldId." + fieldId.toString());
				}
		}
	}
	
	private void update(TRANF_FIELD fieldId, String value) {
		try {
			if(value != null) {
				int legId = Integer.valueOf(getLegId());
				switch(fieldId) {
				case TRANF_INDEXATION_TYPE:
					if(legId == 1) {
						com.olf.openjvs.Transaction jvsTransaction = getJvsTransaction();
						if(!jvsTransaction.getField(fieldId.toInt(), legId).equalsIgnoreCase(value)) {
							jvsTransaction.setField(fieldId.toInt(), legId, "", value);
							getUpdates().add("Leg" + getLegId() + "TRANF_FIELD." + fieldId);
						}
					}
					break;
				default:
					throw new UnsupportedOperationException("Unsupported JVS TRANF_FIELD [" + fieldId.toString() + "]");
				}
			}
		} catch (NumberFormatException | OException e) {
			throw new OpenRiskException(e);
		}
	}
	
	private void updateDate(EnumLegFieldId fieldId, String value) {
		try {
			if(value != null && getLeg().isApplicable(fieldId)) {
				value = dateParser(value);
				Date existingFieldValue = getLeg().getValueAsDate(fieldId);
				if(value == null && existingFieldValue == null) {
					return;
				}
				int existingFieldJd = getSession().getCalendarFactory().getJulianDate(existingFieldValue);
				int parameterFieldJd = OCalendar.parseString(value);
				if(existingFieldJd != parameterFieldJd) {
					getLeg().setValue(fieldId, value);
					getUpdates().add("Leg" + getLegId() + ".EnumLegFieldId." + fieldId.toString());
				}
			}
		} catch (OException e) {
			throw new OpenRiskException(e);
		}
	}

	private String dateParser(String value) {
		if(value.isEmpty() || value.trim().equals("") || value.equalsIgnoreCase("None")) {
			return null;
		}
		return value;
	}
}

