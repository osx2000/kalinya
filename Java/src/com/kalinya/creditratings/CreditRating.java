package com.kalinya.creditratings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"partyName","agencyName","rating"})
@XmlRootElement
public class CreditRating implements Comparable<CreditRating>{
	private String partyName;
	private String agencyName;
	private String rating;

	@SuppressWarnings("unused")
	private CreditRating() {
	}
	
	public CreditRating(String party, String agencyName, String rating) {
		setPartyName(party);
		setAgencyName(agencyName);
		setRating(rating);
	}
	
	@Override
	public String toString() {
		return "Party [" + getPartyName() 
				+ "] Agency [" + getAgencyName() 
				+ "] Rating [" + getRating() + "]";
	}

	/**
	 * @return the agencyName
	 */
	public String getAgencyName() {
		return agencyName;
	}

	/**
	 * @param agencyName the agencyName to set
	 */
	@XmlElement
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	@XmlElement
	public void setRating(String rating) {
		this.rating = rating;
	}

	/**
	 * @return the party
	 */
	public String getPartyName() {
		return partyName;
	}

	/**
	 * @param party the party to set
	 */
	@XmlElement
	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	
	@Override
	public int compareTo(CreditRating that) {
		if (this == that) {
			return 0;
		}
		int i = getPartyName().compareTo(that.getPartyName());
		if(i!=0) return i;
		
		i = getAgencyName().compareTo(that.getAgencyName());
		if(i!=0) return i;
		
		return getRating().compareTo(that.getRating());
	}
}
