package com.kalinya.results;

import java.util.Date;

import com.kalinya.performance.Portfolio;

public class PortfolioResults  implements Results {

	private Date date;
	private Portfolio portfolio;
	
	//TODO: constructor
	
	@Override
	public Date getDate() {
		return date;
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}
}
