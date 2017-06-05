package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.kalinya.optimization.Instrument;
import com.kalinya.optimization.InstrumentStatistic;
import com.kalinya.optimization.MaturityBucket;
import com.kalinya.util.Assertions;
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
	private MaturityBucket[] maturityBuckets;
	
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
	 * <li>TODO: Target yield relative to benchmark</li>
	 * <li>TODO: Minimum issue size</li>
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
	}
	
	private void fixedIncomePortfolioMinimizeDuration() {
		String methodName = "Fixed Income Portfolio Minimize Duration (s.t. Yield >= 3%, Concentration <= 15%, BucketPct <= 30%, long-only, 1% Cash)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> portfolio = getInstruments();
		try {
			double[] durations = getInstrumentDuration(portfolio);
			LinearObjectiveFunction f = new LinearObjectiveFunction(durations, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (1% in cash)
			double[] unitCoefficients = getUnitCoefficients(portfolio.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			List<LinearConstraint> cashConstraints = getCashConstraints(portfolio, Relationship.EQ, new BigDecimal(0.01)); 
			constraints.addAll(cashConstraints);
			
			//Yield constraint
			//Greater than 3 percent
			double[] yields = getInstrumentYield(portfolio);
			constraints.add(new LinearConstraint(yields, Relationship.GEQ, 2.0));
			
			//Concentration limits
			//Less than 15% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(portfolio, Relationship.LEQ, new BigDecimal(0.15)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Bucket limits
			//Less than 30% of the portfolio in any maturity bucket ({"1y", "2y", "5y", "10y", "30y"})
			List<LinearConstraint> bucketConcentrationConstraints = new ArrayList<LinearConstraint>(); 
			double[][] bucketMatrix = Instrument.getInstrumentBucketMatrix(portfolio.toArray(new Instrument[portfolio.size()]), maturityBuckets);
			for(double[] bucketVector: bucketMatrix) {
				bucketConcentrationConstraints.add(new LinearConstraint(bucketVector, Relationship.LEQ, 0.30));
			}
			constraints.addAll(bucketConcentrationConstraints);
			
			//Term to maturity constraint
			//Less than 16 years to maturity
			List<LinearConstraint> termToMaturityConstraints = getTermToMaturityConstraints(portfolio, Relationship.LEQ, new BigDecimal(16.0)); 
			constraints.addAll(termToMaturityConstraints);
			
			//Non-negative constraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(portfolio, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MINIMIZE, new NonNegativeConstraint(true), callback);
			System.out.println(getPortfolioResultsAsString(portfolio, result));
			System.out.println(getPortfolioResultsByBucketAsString(portfolio.toArray(new Instrument[portfolio.size()]), maturityBuckets, result));
		} catch (Exception e) {
			if(e instanceof NoFeasibleSolutionException) {
				PointValuePair lastIteration = callback.getSolution();
				if(lastIteration !=  null) {
					System.out.println(getPortfolioResultsAsString(portfolio, lastIteration));
				} else {
					Logger.getLogger(FixedIncomeOptimizationSandbox.class.getName()).log(Level.SEVERE, e.getMessage());
				}
			} else {
				Logger.getLogger(FixedIncomeOptimizationSandbox.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
	private void fixedIncomePortfolioMaximizeConvexity() {
		String methodName = "Fixed Income Portfolio Maximize Convexity (s.t. Yield >= 3%, Concentration <= 15%, BucketPct <= 25%, long-only, 1% Cash)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> portfolio = getInstruments();
		try {
			double[] convexitys = getInstrumentConvexity(portfolio);
			LinearObjectiveFunction f = new LinearObjectiveFunction(convexitys, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (1% in cash)
			double[] unitCoefficients = getUnitCoefficients(portfolio.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			List<LinearConstraint> cashConstraints = getCashConstraints(portfolio, Relationship.EQ, new BigDecimal(0.01)); 
			constraints.addAll(cashConstraints);
			
			//Yield constraint
			//Greater than 3 percent
			double[] yields = getInstrumentYield(portfolio);
			constraints.add(new LinearConstraint(yields, Relationship.GEQ, 3.0));
			
			//Concentration limits
			//Less than 15% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(portfolio, Relationship.LEQ, new BigDecimal(0.15)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Bucket limits
			//Less than 25% of the portfolio in any maturity bucket ({"1y", "2y", "5y", "10y", "30y"})
			List<LinearConstraint> bucketConcentrationConstraints = new ArrayList<LinearConstraint>(); 
			double[][] bucketMatrix = Instrument.getInstrumentBucketMatrix(portfolio.toArray(new Instrument[portfolio.size()]), maturityBuckets);
			for(double[] bucketVector: bucketMatrix) {
				bucketConcentrationConstraints.add(new LinearConstraint(bucketVector, Relationship.LEQ, 0.25));
			}
			constraints.addAll(bucketConcentrationConstraints);
			
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
	
	private void fixedIncomePortfolioMaximizeYield() {
		String methodName = "Fixed Income Portfolio Maximize Yield #2 (s.t. Duration <= 4.524, Concentration <= 40%, long-only)";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> portfolio = getInstruments2();
		try {
			double[] yields = getInstrumentYield(portfolio);
			LinearObjectiveFunction f = new LinearObjectiveFunction(yields, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (0% in cash)
			double[] unitCoefficients = getUnitCoefficients(portfolio.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			
			//Duration constraint
			//Less than 4.524 years
			double[] durations = getInstrumentDuration(portfolio);
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
		List<Instrument> portfolio = getInstruments2();
		try {
			double[] convexitys = getInstrumentConvexity(portfolio);
			LinearObjectiveFunction f = new LinearObjectiveFunction(convexitys, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 100% (0% in cash)
			double[] unitCoefficients = getUnitCoefficients(portfolio.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 1.0));
			
			//Duration constraint
			//Less than 4.524 years
			double[] durations = getInstrumentDuration(portfolio);
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
	}
	
	private double[] getUnitCoefficients(int size) {
		double[] unitCoefficients = new double[size];
		for(int i = 0; i < size; i++) {
			unitCoefficients[i]=1.0;
		}
		return unitCoefficients;
	}

	public static String getPortfolioResultsAsString(List<Instrument> portfolio, PointValuePair result) {
		double[] instrumentWeights = result.getKey();
		StringBuilder weights = new StringBuilder();
		for(int i = 0; i < instrumentWeights.length; i++) {
			if(i > 0) {
				weights.append(", ");
			}
			weights.append(String.format("%s=%s", portfolio.get(i), StringUtil.formatDouble(instrumentWeights[i], 4)));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("OptimizationResults Points[%s] ObjFunctionResult [%s]", 
				weights.toString(), StringUtil.formatDouble(result.getValue(), 8)));
		sb.append("\n");
		sb.append(getPortfolioStatisticsAsString(portfolio.toArray(new Instrument[portfolio.size()]), instrumentWeights));
		
		return sb.toString();
	}
	
	public static String getPortfolioResultsByBucketAsString(Instrument[] portfolio, MaturityBucket[] maturityBuckets, PointValuePair result) {
		Map<MaturityBucket, List<Instrument>> instrumentsByBucket = getInstrumentsByBucket(portfolio, maturityBuckets);
		Map<MaturityBucket, double[]> instrumentWeightsByBucket = getInstrumentWeightsByBucket(portfolio, maturityBuckets, result.getKey());
		StringBuilder sb = new StringBuilder();
		String newLineBreak = "";
		for(int i = 0; i < maturityBuckets.length; i++) {
			MaturityBucket maturityBucket = maturityBuckets[i];
			List<Instrument> instrumentsInBucket = instrumentsByBucket.get(maturityBucket);
			if(instrumentsInBucket == null) {
				//No instruments in the bucket
				continue;
			}
			sb.append(newLineBreak);
			sb.append(String.format(" Bucket [%s] = ", maturityBucket.getName()));
			sb.append(getPortfolioStatisticsAsString(instrumentsInBucket.toArray(new Instrument[instrumentsInBucket.size()]), instrumentWeightsByBucket.get(maturityBucket)));
			newLineBreak = "\n";
		}
		return sb.toString();
	}

	public static Map<MaturityBucket, List<Instrument>> getInstrumentsByBucket(Instrument[] portfolio, MaturityBucket[] maturityBuckets) {
		Map<MaturityBucket, List<Instrument>> instrumentsByBucket = new TreeMap<MaturityBucket, List<Instrument>>();
		for(int i = 0; i < maturityBuckets.length; i++) {
			MaturityBucket maturityBucket = maturityBuckets[i];
			for(int j = 0; j < portfolio.length; j++) {
				Instrument instrument = portfolio[j];
				if(instrument.getMaturityBucket().compareTo(maturityBucket) == 0) {
					List<Instrument> instruments = instrumentsByBucket.get(maturityBucket);
					if(instruments == null) {
						instrumentsByBucket.put(maturityBucket, new ArrayList<Instrument>());
					}
					instrumentsByBucket.get(maturityBucket).add(instrument);
				}
			}
		}
		return instrumentsByBucket;
	}

	public static String getPortfolioStatisticsAsString(Instrument[] portfolio, double[] instrumentWeights) {
		BigDecimal portfolioYield = getYield(portfolio, instrumentWeights);
		BigDecimal portfolioDuration = getDuration(portfolio, instrumentWeights);
		BigDecimal portfolioConvexity = getConvexity(portfolio, instrumentWeights);
		return String.format("Weight [%s] Yield [%s] Duration [%s] Convexity [%s]",
				StringUtil.formatDouble(NumberUtil.sum(instrumentWeights).multiply(new BigDecimal(100.)), 3),
				StringUtil.formatDouble(portfolioYield, 3), 
				StringUtil.formatDouble(portfolioDuration, 3), 
				StringUtil.formatDouble(portfolioConvexity, 3));
	}
	
	public static Map<MaturityBucket, double[]> getInstrumentWeightsByBucket(Instrument[] portfolio, MaturityBucket[] maturityBuckets, double[] instrumentWeights) {
		Map<MaturityBucket, ArrayList<Double>>instrumentsWeightsByBucketList = new HashMap<MaturityBucket, ArrayList<Double>>();
		for (int i = 0; i < maturityBuckets.length; i++) {
			MaturityBucket maturityBucket = maturityBuckets[i];
			for (int j = 0; j < portfolio.length; j++) {
				Instrument instrument = portfolio[j];
				if (instrument.getMaturityBucket().compareTo(maturityBucket) == 0) {
					Double instrumentWeight = instrumentWeights[j];
					List<Double> bucketWeights = instrumentsWeightsByBucketList.get(maturityBucket);
					if (bucketWeights == null) {
						instrumentsWeightsByBucketList.put(maturityBucket, new ArrayList<Double>());
					}
					instrumentsWeightsByBucketList.get(maturityBucket).add(instrumentWeight);
				}
			}
		}
		Map<MaturityBucket, double[]> instrumentsWeightsByBucket = new TreeMap<MaturityBucket, double[]>();
		for(MaturityBucket maturityBucket: instrumentsWeightsByBucketList.keySet()) {
			List<Double> bucketInstrumentsWeights = instrumentsWeightsByBucketList.get(maturityBucket);
			instrumentsWeightsByBucket.put(maturityBucket, new double[bucketInstrumentsWeights.size()]);
			int i = 0;
			for(Double instrumentWeight: bucketInstrumentsWeights) {
				instrumentsWeightsByBucket.get(maturityBucket)[i] = instrumentWeight;
				i++;
			}
		}
		return instrumentsWeightsByBucket;
	}
	
	public static BigDecimal getYield(Instrument[] portfolio, double[] instrumentWeights) {
		return getStatistic(portfolio, instrumentWeights, InstrumentStatistic.YIELD);
	}
	
	public static BigDecimal getDuration(Instrument[] portfolio, double[] instrumentWeights) {
		return getStatistic(portfolio, instrumentWeights, InstrumentStatistic.DURATION);
	}
	
	public static BigDecimal getConvexity(Instrument[] portfolio, double[] instrumentWeights) {
		return getStatistic(portfolio, instrumentWeights, InstrumentStatistic.CONVEXITY);
	}
	
	public static BigDecimal getStatistic(Instrument[] portfolio, double[] instrumentWeights, InstrumentStatistic instrumentStatistic) {
		Assertions.notNull("Instruments", "Portfolio of instruments");
		Assertions.isEqual("Instruments and instrument weights", portfolio.length, instrumentWeights.length);
		Instrument instrument = null;
		BigDecimal sumOfWeights = null;
		BigDecimal statisticValue = null;
		try {
			statisticValue = NumberUtil.newBigDecimal(0.0);
			sumOfWeights = NumberUtil.sum(instrumentWeights);
			//TODO: handle long-short portfolios
			if(sumOfWeights.compareTo(BigDecimal.ZERO) == 0) {
				return BigDecimal.ZERO;
			}
			for(int i = 0; i < portfolio.length; i++) {
				instrument = portfolio[i];
				BigDecimal instrumentWeight = NumberUtil.newBigDecimal(instrumentWeights[i]);
				BigDecimal instrumentProRataWeight = instrumentWeight.divide(sumOfWeights, NumberUtil.MATH_CONTEXT);
				statisticValue = statisticValue.add(instrument.getStatistic(instrumentStatistic).multiply(instrumentProRataWeight));
			};
			return statisticValue;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Exception handling Instrument [%s] SumOfWeights [%s]", instrument.toString(), StringUtil.formatPrice(sumOfWeights)), e);
		}
	}
	
	private List<LinearConstraint> getWeightConstraints(List<Instrument> portfolio, Relationship relationship, BigDecimal weight) {
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		int size = portfolio.size();
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
	
	private List<LinearConstraint> getCashConstraints(List<Instrument> portfolio, Relationship relationship, BigDecimal weight) {
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		double[] weightConstraintCoefficients = new double[portfolio.size()];
		int i = 0;
		for(Instrument instrument: portfolio) {
			if(instrument.equals(Instrument.CASH)) {
				weightConstraintCoefficients[i] = 1.0;
			}
			i++;
		}
		constraints.add(new LinearConstraint(weightConstraintCoefficients, relationship, weight.doubleValue()));
		return constraints;
	}
	
	private List<LinearConstraint> getTermToMaturityConstraints(List<Instrument> portfolio, Relationship relationship, BigDecimal years) {
		//Note: the method signature describes the constraints in terms of the TTM being /less than/ the years parameter
		//During the method processing, instruments will be required to have a weighting of zero
		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		int multiplier = 1;
		if(relationship == Relationship.GEQ) {
			multiplier = -1;
		}
		int size = portfolio.size();
		for(int i = 0; i < size; i++) {
			boolean eligibleInstrument = true;
			double[] weightConstraintCoefficients = new double[size];
			for(int j = 0; j < size; j++) {
				if(j == i) {
					BigDecimal termToMaturity = portfolio.get(i).getTermToMaturityYears();
					weightConstraintCoefficients[j] = 1.0;
					if((termToMaturity.compareTo(years) * multiplier) <= 0) {
						//No op.  TODO: delete?
					} else {
						//Else, the instrument is ineligibile so its weight coefficient must be 0.0
						System.out.println(String.format("Ineligible instrument: %s", portfolio.get(i).toVerboseString()));
						eligibleInstrument = false;
					}
				}
			}
			if(!eligibleInstrument) {
				LinearConstraint constraint = new LinearConstraint(weightConstraintCoefficients, Relationship.EQ, 0.0);
				constraints.add(constraint);
			}
		}
		return constraints;
	}

	private static MaturityBucket[] getMaturityBuckets() {
		return MaturityBucket.createMaturityBuckets(new String[]{"1y", "2y", "5y", "10y", "30y"});
	}
	
	private List<Instrument> getInstruments() {
		List<Instrument> portfolio = new ArrayList<>();
		if(maturityBuckets == null) {
			maturityBuckets = getMaturityBuckets();
		}
		portfolio.add(Instrument.CASH);
		portfolio.add(new Instrument.Builder("3yBondDecayed2.5y").withDuration(0.4906).withConvexity(0.2406).withYield(1.70).withMaturityDate(DateUtil.parseSymbolicDate("6m")).build());
		portfolio.add(new Instrument.Builder("3yBondDecayed2y").withDuration(0.9706).withConvexity(0.7155).withYield(2.19).withMaturityDate(DateUtil.parseSymbolicDate("1y")).build());
		portfolio.add(new Instrument.Builder("1yBill").withDuration(0.9889).withConvexity(0.7334).withYield(2.25).withMaturityDate(DateUtil.parseSymbolicDate("1y")).build());
		portfolio.add(new Instrument.Builder("3yBondDecayed1y").withDuration(1.8767).withConvexity(2.2730).withYield(3.00).withMaturityDate(DateUtil.parseSymbolicDate("2y")).build());
		portfolio.add(new Instrument.Builder("3yBond").withDuration(2.7433).withConvexity(4.5964).withYield(3.90).withMaturityDate(DateUtil.parseSymbolicDate("3y")).build());
		portfolio.add(new Instrument.Builder("5yBond").withDuration(4.2299).withConvexity(10.7844).withYield(5.00).withMaturityDate(DateUtil.parseSymbolicDate("5y")).build());
		portfolio.add(new Instrument.Builder("10yBond").withDuration(7.0298).withConvexity(31.6523).withYield(6.30).withMaturityDate(DateUtil.parseSymbolicDate("10y")).build());
		portfolio.add(new Instrument.Builder("15yBond").withDuration(8.6781).withConvexity(52.6504).withYield(7.20).withMaturityDate(DateUtil.parseSymbolicDate("15y")).build());
		portfolio.add(new Instrument.Builder("20yBond").withDuration(9.3945).withConvexity(67.5164).withYield(8.20).withMaturityDate(DateUtil.parseSymbolicDate("20y")).assignToMaturityBucket(maturityBuckets).build());
		Instrument.assignToMaturityBuckets(portfolio, maturityBuckets);
		return portfolio;
	}
	
	private List<Instrument> getInstruments2() {
		List<Instrument> portfolio = new ArrayList<>();
		if(maturityBuckets == null) {
			maturityBuckets = getMaturityBuckets();
		}
		portfolio.add(new Instrument.Builder("Bond1").withDuration(1.863930404).withConvexity(4.455761339).withYield(2.947).withMaturityDate(DateUtil.parseSymbolicDate("2y")).build());
		portfolio.add(new Instrument.Builder("Bond2").withDuration(4.52407262).withConvexity(23.70475552).withYield(3.565).withMaturityDate(DateUtil.parseSymbolicDate("5y")).build());
		portfolio.add(new Instrument.Builder("Bond3").withDuration(8.062189241).withConvexity(77.26053665).withYield(4.180).withMaturityDate(DateUtil.parseSymbolicDate("10y")).build());
		Instrument.assignToMaturityBuckets(portfolio, maturityBuckets);
		return portfolio;
	}
	
	
	private double[] getInstrumentConvexity(List<Instrument> instruments) {
		double[] convexitys = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			convexitys[i] = instrument.getConvexity().doubleValue();
			i++;
		}
		return convexitys;
	}
	
	private double[] getInstrumentDuration(Collection<Instrument> instruments) {
		double[] durations = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			durations[i] = instrument.getDuration().doubleValue();
			i++;
		}
		return durations;
	}
	
	private double[] getInstrumentYield(List<Instrument> instruments) {
		double[] yields = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			yields[i] = instrument.getYield().doubleValue();
			i++;
		}
		return yields;
	}

	public static void main(String[] args) {
		FixedIncomeOptimizationSandbox sandbox = new FixedIncomeOptimizationSandbox();
		sandbox.timer = new Timer();
		sandbox.maturityBuckets = getMaturityBuckets();
		sandbox.fixedIncomePortfolioMinimizeDuration();
		sandbox.fixedIncomePortfolioMaximizeConvexity();
		sandbox.fixedIncomePortfolioMaximizeYield();
		sandbox.fixedIncomePortfolioMaximizeConvexity2();
		sandbox.timer.print(true);
	}
}
