package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.linear.SolutionCallback;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import com.kalinya.oc.util.MessageLog;
import com.kalinya.optimization.MaturityBucket;
import com.kalinya.optimization.MaturityBuckets;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.Instrument;
import com.kalinya.performance.Instruments;
import com.kalinya.performance.SecurityMasters;
import com.kalinya.performance.datasource.CSVDataSource;
import com.kalinya.results.InstrumentResultEnum;
import com.kalinya.results.InstrumentResults;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

/**
 * Fixed Income Optimization implemented using Apache Commons-Math Optim Package
 * 
 * @see <a href=
 *      "http://commons.apache.org/proper/commons-math/userguide/optimization.html">Commons-Math
 *      Optimization Documentation</a>
 */
public final class FixedIncomeOptimizationSandbox {
	private Timer timer = null;
	private MaturityBuckets maturityBuckets;
	private MessageLog messageLog;
	
	/**
	 * <h1 style="color:blue;">FOMOBO 1.2.10 Reversed/Targeted Optimization</h1>
	 * <p>
	 * Scenario analysis is able to perform reversed/ targeted optimization to
	 * achieve certain portfolio Profile which need any manual input in variable
	 * such as: yield curve, currency, absolute or excess/ relative return,
	 * duration, etc
	 * </p>
	 * 
	 * <h2 style="color:red;">TODO<h5>
	 * <ul>
	 * <li>TODO: Currency weight constraints</li>
	 * <li>TODO: Credit rating constraints</li>
	 * <li>TODO: Target yield relative to benchmark</li>
	 * <li>TODO: Minimum issue size</li>
	 * <li>TODO: Minimum issue size by currency (defined in local currency)</li>
	 * <li>TODO: Minimum issue size exemption on specified instrument types e.g. commercial paper</li>
	 * <li>TODO: Minimum issue date</li>
	 * <li>TODO: Create Optimization object</li>
	 * <li>TODO: Minimize turnover?</li>
	 * <li>TODO: The portfolio optimization has the ability to incorporate real
	 * life constraints e.g. liquidity capital requirements</li>
	 * <li>TODO: i. The system is able to recommend the most appropriate hedging
	 * structures with regards to minimizing risks with several constraints such
	 * as max/min currency exposure, max/min asset class exposure, etc at least
	 * covering interest rate hedging and currency exchange rate hedging through
	 * the use of forward contracts</li>
	 * </ul>
	 * <h2 style="color:green;">SUPPORTED</h3>
	 * <li>Target yield</li>
	 * <li>Target duration</li>
	 * <li>Maturity bucket constraints</li>
	 * <li>Term to maturity constraints</li>
	 * <li>Portfolio summary statistics to support demos</li>
	 * </ul>
	 * <h1><p>FOMOBO 2.5.8 Methods in Optimizing Portfolio</p></h1>
	 * <p>
	 * i. System is able to calculate portfolio optimization using certain
	 * method, including but not limited to: - Historical; - Mean-Variance; -
	 * Bayesian (Black-Litterman); - Monte Carlo; - Resampling; and - User
	 * defined projection.
	 * 
	 * <h2 style="color:red;">TODO<h5>
	 * <ul>
	 * <li>TODO: Black–Litterman model</li>
	 * <p>
	 * There are also quite a few different ways of deriving TAA allocations,
	 * Black–Litterman model probably being most widely known and almost
	 * synonymous with TAA-driven portfolio allocations (<i>Bernhard
	 * Pfaff-Financial Risk Modelling and Portfolio Optimization with R-Wiley
	 * (2013), Ch 13</i>)
	 * </p>
	 * </ul>
	 * </ul>
	 */
	public static void todo() {
	}

	public FixedIncomeOptimizationSandbox() {
		messageLog = new MessageLog(null, this.getClass());
	}
	
	private void fixedIncomePortfolioMinimizeDuration() {
		String methodName = "Fixed Income Portfolio Minimize Duration (s.t. Yield >= 2%, Concentration <= 15%, BucketPct <= 30%, long-only, 1% Cash)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		Instruments instruments = null;
		InstrumentResults instrumentResults = null;
		Date today = DateUtil.today();
		try {
			//Retrieve instruments and required results (duration, convexity, yield)
			CSVDataSource csvDataSource = new CSVDataSource.Builder()
					.withScenarioResultsFilePath(Configurator.SCENARIO_RESULTS_FILE_PATH)
					.withSecurityMasterFilePath(Configurator.SECURITY_MASTER_FILE_PATH)
					.build();
			instruments = TestHarnessHelper.getPerformanceInstruments();
			SecurityMasters securityMasters = SecurityMasters.retrieve(csvDataSource);
			instruments.addSecurityMasterData(securityMasters);
			instrumentResults = InstrumentResults.retrieve(csvDataSource, today);
			instrumentResults.addDefaultCashResults();
			
			InstrumentResults durationResults = instrumentResults.getResults(today, instruments, InstrumentResultEnum.DURATION, true, true);
			double[] durations = durationResults.asArray();
			LinearObjectiveFunction f = new LinearObjectiveFunction(durations, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (1% in cash)
			double[] unitCoefficients = getUnitCoefficients(instruments.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			List<LinearConstraint> cashConstraints = getCashConstraints(instruments, Relationship.EQ, new BigDecimal(0.01)); 
			constraints.addAll(cashConstraints);
			
			//Yield constraint
			//Greater than 2 percent
			InstrumentResults yieldResults = instrumentResults.getResults(today, instruments, InstrumentResultEnum.MARKET_YIELD, true, true);
			double[] yields = yieldResults.asArray();
			constraints.add(new LinearConstraint(yields, Relationship.GEQ, 2.0));
			
			//Concentration limits
			//Less than 15% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(instruments, Relationship.LEQ, new BigDecimal(0.15)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Bucket limits
			//Less than 30% of the portfolio in any maturity bucket ({"1y", "2y", "5y", "10y", "30y"})
			instruments.setAndGetInstrumentsByBucket(maturityBuckets);
			List<LinearConstraint> bucketConcentrationConstraints = new ArrayList<LinearConstraint>(); 
			double[][] bucketMatrix = instruments.getInstrumentBucketMatrix(maturityBuckets);
			for(double[] bucketVector: bucketMatrix) {
				bucketConcentrationConstraints.add(new LinearConstraint(bucketVector, Relationship.LEQ, 0.30));
			}
			constraints.addAll(bucketConcentrationConstraints);
			
			//Term to maturity constraint
			//Less than 16 years to maturity
			List<LinearConstraint> termToMaturityConstraints = getTermToMaturityConstraints(instruments, Relationship.LEQ, new BigDecimal(16.0), today); 
			constraints.addAll(termToMaturityConstraints);
			
			//Non-negative constraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(instruments, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MINIMIZE, new NonNegativeConstraint(true), callback);
			System.out.println(getPortfolioResultsAsString(instruments, result, instrumentResults));
			System.out.println(getPortfolioResultsByBucketAsString(instruments, maturityBuckets, result, instrumentResults));
		} catch (Exception e) {
			if(e instanceof NoFeasibleSolutionException) {
				PointValuePair lastIteration = callback.getSolution();
				if(lastIteration !=  null) {
					System.out.println(getPortfolioResultsAsString(instruments, lastIteration, instrumentResults));
				} else {
					Logger.getLogger(FixedIncomeOptimizationSandbox.class.getName()).log(Level.SEVERE, e.getMessage());
				}
			} else {
				messageLog.logException(e);
			}
		}
	}
	
	private void fixedIncomePortfolioMaximizeConvexity() {
		String methodName = "Fixed Income Portfolio Maximize Convexity (s.t. Yield >= 3%, Concentration <= 15%, BucketPct <= 25%, long-only, 1% Cash)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		Instruments instruments = null;
		InstrumentResults instrumentResults = null;
		Date today = DateUtil.today();
		try {
			//Retrieve instruments and required results (duration, convexity, yield)
			CSVDataSource csvDataSource = new CSVDataSource.Builder()
					.withScenarioResultsFilePath(Configurator.SCENARIO_RESULTS_FILE_PATH)
					.withSecurityMasterFilePath(Configurator.SECURITY_MASTER_FILE_PATH)
					.build();
			instruments = TestHarnessHelper.getPerformanceInstruments();
			SecurityMasters securityMasters = SecurityMasters.retrieve(csvDataSource);
			instruments.addSecurityMasterData(securityMasters);
			instrumentResults = InstrumentResults.retrieve(csvDataSource, today);
			instrumentResults.addDefaultCashResults();
			
			InstrumentResults convexityResults = instrumentResults.getResults(today, instruments, InstrumentResultEnum.CONVEXITY, true, true);
			double[] convexitys = convexityResults.asArray();
			LinearObjectiveFunction f = new LinearObjectiveFunction(convexitys, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (1% in cash)
			double[] unitCoefficients = getUnitCoefficients(instruments.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			List<LinearConstraint> cashConstraints = getCashConstraints(instruments, Relationship.EQ, new BigDecimal(0.01)); 
			constraints.addAll(cashConstraints);
			
			//Yield constraint
			//Greater than 3 percent
			InstrumentResults yieldResults = instrumentResults.getResults(today, instruments, InstrumentResultEnum.MARKET_YIELD, true, true);
			double[] yields = yieldResults.asArray();
			constraints.add(new LinearConstraint(yields, Relationship.GEQ, 3.0));
			
			//Concentration limits
			//Less than 15% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(instruments, Relationship.LEQ, new BigDecimal(0.15)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Bucket limits
			//Less than 25% of the portfolio in any maturity bucket ({"1y", "2y", "5y", "10y", "30y"})
			instruments.setAndGetInstrumentsByBucket(maturityBuckets);
			List<LinearConstraint> bucketConcentrationConstraints = new ArrayList<LinearConstraint>(); 
			double[][] bucketMatrix = instruments.getInstrumentBucketMatrix(maturityBuckets);
			for(double[] bucketVector: bucketMatrix) {
				bucketConcentrationConstraints.add(new LinearConstraint(bucketVector, Relationship.LEQ, 0.25));
			}
			constraints.addAll(bucketConcentrationConstraints);
			
			//Non-negative constraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(instruments, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MINIMIZE, new NonNegativeConstraint(true), callback);
			System.out.println(getPortfolioResultsAsString(instruments, result, instrumentResults));
			System.out.println(getPortfolioResultsByBucketAsString(instruments, maturityBuckets, result, instrumentResults));
		} catch (Exception e) {
			if(e instanceof NoFeasibleSolutionException) {
				PointValuePair lastIteration = callback.getSolution();
				if(lastIteration !=  null) {
					System.out.println(getPortfolioResultsAsString(instruments, lastIteration, instrumentResults));
				} else {
					Logger.getLogger(FixedIncomeOptimizationSandbox.class.getName()).log(Level.SEVERE, e.getMessage());
				}
			} else {
				messageLog.logException(e);
			}
		}
	}
	
	/*private void fixedIncomePortfolioMaximizeYield() {
		String methodName = "Fixed Income Portfolio Maximize Yield #2 (s.t. Duration <= 4.524, Concentration <= 40%, long-only)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> portfolio = TestHarnessHelper.getInstruments2();
		try {
			double[] yields = TestHarnessHelper.getInstrumentYield(portfolio);
			LinearObjectiveFunction f = new LinearObjectiveFunction(yields, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (0% in cash)
			double[] unitCoefficients = getUnitCoefficients(portfolio.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			
			//Duration constraint
			//Less than 4.524 years
			double[] durations = TestHarnessHelper.getInstrumentDuration(portfolio);
			constraints.add(new LinearConstraint(durations, Relationship.LEQ, 4.524));
			
			//Concentration limits
			//Less than 40% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(portfolio, Relationship.LEQ, new BigDecimal(0.4)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Non-negative constraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(portfolio, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
			System.out.println(getPortfolioResultsAsString(portfolio, result));
			System.out.println(getPortfolioResultsByBucketAsString(portfolio.toArray(new Instrument[portfolio.size()]), maturityBuckets, result));
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			if(lastIteration !=  null) {
				System.out.println(getPortfolioResultsAsString(portfolio, lastIteration));
			} else {
				Logger.getLogger(FixedIncomeOptimizationSandbox.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
	private void fixedIncomePortfolioMaximizeConvexity2() {
		String methodName = "Fixed Income Portfolio Maximize Convexity #2 (s.t. Duration <= 4.52, Concentration <= 40%, long-only)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> portfolio = TestHarnessHelper.getInstruments2();
		try {
			double[] convexitys = TestHarnessHelper.getInstrumentConvexity(portfolio);
			LinearObjectiveFunction f = new LinearObjectiveFunction(convexitys, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (0% in cash)
			double[] unitCoefficients = getUnitCoefficients(portfolio.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			
			//Duration constraint
			//Less than 4.524 years
			double[] durations = TestHarnessHelper.getInstrumentDuration(portfolio);
			constraints.add(new LinearConstraint(durations, Relationship.EQ, 4.524));
			
			//Concentration limits
			//Less than 40% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(portfolio, Relationship.LEQ, new BigDecimal(0.4)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Non-negative constraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(portfolio, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
			System.out.println(getPortfolioResultsAsString(portfolio, result));
			System.out.println(getPortfolioResultsByBucketAsString(portfolio.toArray(new Instrument[portfolio.size()]), maturityBuckets, result));
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			if(lastIteration !=  null) {
				System.out.println(getPortfolioResultsAsString(portfolio, lastIteration));
			} else {
				Logger.getLogger(FixedIncomeOptimizationSandbox.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}*/
	
	private double[] getUnitCoefficients(int size) {
		double[] unitCoefficients = new double[size];
		for(int i = 0; i < size; i++) {
			unitCoefficients[i]=1.0;
		}
		return unitCoefficients;
	}

	public static String getPortfolioResultsAsString(Instruments instruments, PointValuePair result, InstrumentResults instrumentResults) {
		//double[] instrumentWeights = result.getKey();
		List<BigDecimal> instrumentWeights = NumberUtil.getDoubleArrayAsListBigDecimal(result.getKey());
		Instrument[] instrumentsArray = instruments.toArray(new Instrument[instruments.size()]);
		StringBuilder weights = new StringBuilder();//TODO: don't use array
		for(int i = 0; i < instrumentWeights.size(); i++) {
			if(i > 0) {
				weights.append(", ");
			}
			weights.append(String.format("%s=%s", instrumentsArray[i].getInstrumentId(), StringUtil.formatDouble(instrumentWeights.get(i), 4)));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("OptimizationResults Points[%s] ObjFunctionResult [%s]", 
				weights.toString(), StringUtil.formatDouble(result.getValue(), 8)));
		sb.append("\n");
		sb.append(getPortfolioStatisticsAsString(instruments, instrumentWeights, instrumentResults));
		
		return sb.toString();
	}
	
	public static String getPortfolioResultsByBucketAsString(Instruments instruments, MaturityBuckets maturityBuckets, PointValuePair result, InstrumentResults instrumentResults) {
		Map<MaturityBucket, Instruments> instrumentsByBucket = instruments.setAndGetInstrumentsByBucket(maturityBuckets);
		
		Map<MaturityBucket, List<BigDecimal>> instrumentWeightsByBucket = getInstrumentWeightsByBucket(instruments, maturityBuckets, result.getKey());
		StringBuilder sb = new StringBuilder();
		String newLineBreak = "";
		for(MaturityBucket maturityBucket: maturityBuckets) {
			Instruments instrumentsInBucket = instrumentsByBucket.get(maturityBucket);
			if(instrumentsInBucket == null) {
				//No instruments in the bucket
				continue;
			}
			sb.append(newLineBreak);
			sb.append(String.format(" Bucket [%s] = ", maturityBucket.getName()));
			sb.append(getPortfolioStatisticsAsString(instrumentsInBucket, instrumentWeightsByBucket.get(maturityBucket), instrumentResults));
			newLineBreak = "\n";
		}
		return sb.toString();
	}

	public static String getPortfolioStatisticsAsString(Instruments instruments, List<BigDecimal> instrumentWeights, InstrumentResults instrumentsResults) {
		BigDecimal portfolioYield = instruments.getPortfolioStatistic(instrumentWeights, instrumentsResults, InstrumentResultEnum.MARKET_YIELD);
		BigDecimal portfolioDuration = instruments.getPortfolioStatistic(instrumentWeights, instrumentsResults, InstrumentResultEnum.DURATION);
		BigDecimal portfolioConvexity = instruments.getPortfolioStatistic(instrumentWeights, instrumentsResults, InstrumentResultEnum.CONVEXITY);
		return String.format("Weight [%s] Yield [%s] Duration [%s] Convexity [%s]",
				StringUtil.formatDouble(NumberUtil.sum(instrumentWeights).multiply(new BigDecimal(100.)), 3),
				StringUtil.formatDouble(portfolioYield, 3), 
				StringUtil.formatDouble(portfolioDuration, 3), 
				StringUtil.formatDouble(portfolioConvexity, 3));
	}
	
	/**
	 * Returns a List of Instrument weights for each MaturityBucket.
	 * 
	 * e.g.: TODO
	 * 
	 * @param instruments
	 * @param maturityBuckets
	 * @param instrumentWeights
	 * @return
	 */
	public static Map<MaturityBucket, List<BigDecimal>> getInstrumentWeightsByBucket(Instruments instruments, MaturityBuckets maturityBuckets, double[] instrumentWeights) {
		Map<MaturityBucket, List<BigDecimal>> instrumentsWeightsByBucket = new TreeMap<MaturityBucket, List<BigDecimal>>();
		Map<MaturityBucket, Instruments> instrumentsByBucket = instruments.setAndGetInstrumentsByBucket(maturityBuckets);
		instruments.setInstrumentWeights(instrumentWeights);
		Map<Instrument, BigDecimal> weightsByInstrument = instruments.getInstrumentWeights();
		
		for(MaturityBucket maturityBucket: instrumentsByBucket.keySet()) {
			Set<Instrument> instrumentsInBucket = instrumentsByBucket.get(maturityBucket);
			if(instrumentsInBucket != null) {
				instrumentsWeightsByBucket.put(maturityBucket, new ArrayList<BigDecimal>());
				for(Instrument instrument: instrumentsInBucket) {
					instrumentsWeightsByBucket.get(maturityBucket).add(weightsByInstrument.get(instrument));
				}
			}
		}
		return instrumentsWeightsByBucket;
	}
	
	private static List<LinearConstraint> getWeightConstraints(Instruments instruments, Relationship relationship, BigDecimal weight) {
	//TODO: does this belong in another class?
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		int size = instruments.size();
		for(int i = 0; i < size; i++) {
			double[] weightConstraintCoefficients = new double[size];
			for(int j = 0; j < size; j++) {
				if(j == i) {
					weightConstraintCoefficients[j] = 1.0;
				} else {
					weightConstraintCoefficients[j] = 0.0;
				}
			}
			LinearConstraint constraint = new LinearConstraint(weightConstraintCoefficients, relationship, weight.doubleValue());
			constraints.add(constraint);
		}
		return constraints;
	}
	
	private List<LinearConstraint> getCashConstraints(Instruments instruments, Relationship relationship, BigDecimal weight) {
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		double[] weightConstraintCoefficients = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			if(instrument.equals(Instrument.CASH)) {
				weightConstraintCoefficients[i] = 1.0;
			}
			i++;
		}
		constraints.add(new LinearConstraint(weightConstraintCoefficients, relationship, weight.doubleValue()));
		return constraints;
	}
	
	private List<LinearConstraint> getTermToMaturityConstraints(Instruments instruments, Relationship relationship, BigDecimal years, Date date) {
		//Note: the method signature describes the constraints in terms of the TTM being /less than/ the years parameter
		//During the method processing, instruments will be required to have a weighting of zero
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		int multiplier = 1;
		if(relationship == Relationship.GEQ) {
			multiplier = -1;
		}
		int size = instruments.size();
		for(int i = 0; i < size; i++) {
			boolean eligibleInstrument = true;
			double[] weightConstraintCoefficients = new double[size];
			int j = 0;
			for(Instrument instrument: instruments) {
				if(j == i) {
					/*if(instrument.equals(Instrument.CASH)) {
						System.out.println(String.format("Analyzing Instrument [%s]", instrument.getInstrumentId()));
					}*/
					BigDecimal termToMaturity = instrument.getTermToMaturityYears(date);
					weightConstraintCoefficients[j] = 1.0;
					if((termToMaturity.compareTo(years) * multiplier) <= 0) {
						//No op.  TODO: delete?
					} else {
						//Else, the instrument is ineligible so its weight coefficient must be 0.0
						System.out.println(String.format("Ineligible instrument: %s", instrument.toVerboseString()));
						eligibleInstrument = false;
					}
				}
				j++;
			}
			if(!eligibleInstrument) {
				LinearConstraint constraint = new LinearConstraint(weightConstraintCoefficients, Relationship.EQ, 0.0);
				constraints.add(constraint);
			}
		}
		return constraints;
	}

	public static void main(String[] args) {
		FixedIncomeOptimizationSandbox sandbox = new FixedIncomeOptimizationSandbox();
		sandbox.timer = new Timer();
		sandbox.maturityBuckets = TestHarnessHelper.getMaturityBuckets();
		sandbox.fixedIncomePortfolioMinimizeDuration();
		sandbox.fixedIncomePortfolioMaximizeConvexity();
		/*sandbox.fixedIncomePortfolioMaximizeYield();
		sandbox.fixedIncomePortfolioMaximizeConvexity2();*/
		sandbox.timer.print(true);
	}
}
