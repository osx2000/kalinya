package com.kalinya.harness;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.kalinya.creditratings.CreditRating;
import com.kalinya.creditratings.CreditRatings;
import com.kalinya.performance.Configurator;

public class CreditRatingServiceSandbox {

	public static void main(String[] args) {
		CreditRatings creditRatings = new CreditRatings();
		creditRatings.add(new CreditRating("Barclays", "S&P", "A+"));
		creditRatings.add(new CreditRating("Barclays", "Moodys", "A1"));
		System.out.println("\nPublished Credit Ratings...\n" + creditRatings.toString());
		publishRatingsToXml(creditRatings);
		
		CreditRatings newCreditRatings = getCreditRatings(Configurator.CREDIT_RATINGS_FILE_PATH);
		System.out.println("\nRetrieved Credit Ratings...\n" + newCreditRatings.toString());
	}
	
	public static void publishRatingsToXml(CreditRatings creditRatings) {
		File file = new File(Configurator.CREDIT_RATINGS_FILE_PATH);
		try {
			JAXBContext context = JAXBContext.newInstance(CreditRatings.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(creditRatings, file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static CreditRatings getCreditRatings(String path) {
		File file = new File(path);
		try {
			JAXBContext context = JAXBContext.newInstance(CreditRatings.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			CreditRatings creditRatings = (CreditRatings) unmarshaller.unmarshal(file);
			return creditRatings;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * <h2 style="color:red;">TODO<h5>
	 * <ul>
	 * <li>TODO: Create Moodys</li>
	 * <li>TODO: Create Fitch</li>
	 * <li>TODO: Create S&P</li>
	 * <li>TODO: Short-term/long-term</li>
	 * <li>TODO: Support rating aggregation</li>
	 * <li>TODO: Rating aggregation supports max/min/average</li>
	 * <li>TODO: Ratings on parties and instruments</li>
	 * </ul>
	 * <h2 style="color:green;">SUPPORTED</h3>
	 * <ul>
	 * <li></li>
	 * <li></li>
	 * <li></li>
	 * </ul>
	 */
	public static void todo() {
	}
}