package com.kalinya.creditratings;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.kalinya.util.BaseSet;
import com.olf.openrisk.utility.Disposable;

@XmlType(propOrder={"xmlVersion","creditRatingSet"})
@XmlRootElement
public class CreditRatings extends BaseSet<CreditRating> implements Disposable {
	private static final long serialVersionUID = 3875503093150094084L;
	private Set<CreditRating> creditRatingSet;
	private String xmlVersion;
	
	public CreditRatings() {
		super();
		setXmlVersion("2017.02.01");
		setCreditRatingSet(getSet());
	}

	@Override
	public void dispose() {
	}
	
	public Set<CreditRating> getCreditRatingSet() {
		return creditRatingSet;
	}
	
	@XmlElement(name="creditRatings")
	public void setCreditRatingSet(Set<CreditRating> set) {
		creditRatingSet = set;
		if(creditRatingSet != null && creditRatingSet.size() > 0 && getSet().size() == 0) {
			addAll(creditRatingSet);
		}
	}
	
	@XmlElement
	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}
	
	public String getXmlVersion() {
		return xmlVersion;
	}
}

