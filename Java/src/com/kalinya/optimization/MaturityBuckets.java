package com.kalinya.optimization;

import java.util.Date;
import com.kalinya.performance.Instrument;
import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;

public class MaturityBuckets extends BaseSet<MaturityBucket> {

	private static final long serialVersionUID = -172756958705720734L;

	public MaturityBuckets() {
		super();
	}

	public static MaturityBuckets create() {
		return new MaturityBuckets();
	}

	public MaturityBucket getMaturityBucketForInstrument(Instrument instrument) {
		Date maturityDate = instrument.getMaturityDate();
		return getMaturityBucket(maturityDate);
	}
	
	public static MaturityBuckets create(String[] bucketNames) {
		Assertions.notNullOrEmpty("BucketNames", bucketNames);
		MaturityBuckets maturityBuckets = MaturityBuckets.create();
		for(int i = 0; i < bucketNames.length; i++) {
			String bucketName = bucketNames[i];
			maturityBuckets.add(MaturityBucket.create(bucketName));
		}
		return maturityBuckets;
	}
	
	/**
	 * Returns the first MaturityBucket that has an endDate greater than the
	 * parameter maturityDate
	 * 
	 * @param maturityDate
	 * @return
	 */
	public MaturityBucket getMaturityBucket(Date maturityDate) {
		for(MaturityBucket maturityBucket: getSet()) {
			Date endDate = maturityBucket.getDate();
			if(endDate.compareTo(maturityDate) > 0) {
				return maturityBucket;
			}
		}
		return null;
	}
}

