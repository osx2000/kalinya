package com.kalinya.optimization.examples;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.linear.SolutionCallback;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import com.kalinya.optimization.Instrument;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

/**
 * Fixed Income Example implemented using Apache Commons-Math Optim Package
 * 
 * @see <a href=
 *      "https://financetrainingcourse.com/education/2012/09/fixed-income-investment-portfolio-management-optimization-case-study-risk-training/">Working
 *      example in Excel</a>
 * @see <a href=
 *      "http://commons.apache.org/proper/commons-math/userguide/optimization.html">Commons-Math
 *      Optimization Documentation</a>
 */
public final class CommonsFixedIncomeExample {
	private static Timer timer = null;

	public CommonsFixedIncomeExample(int i) {
		if(i == 1) {
			fixedIncomePortfolioMinimizeDuration();
		} else if (i == 2) {
			fixedIncomePortfolioMaximizeConvexity();
		} else {
			throw new IllegalArgumentException(String.format("Unknown argument [%s]", i));
		}
	}
	
	private void fixedIncomePortfolioMinimizeDuration() {
		String methodName = "Fixed Income Portfolio Minimize Duration";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> instruments = getInstruments();
		try {
			double[] durations = getInstrumentDuration(instruments);
			LinearObjectiveFunction f = new LinearObjectiveFunction(durations, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 99% (1% in cash)
			double[] unitCoefficients = getUnitCoefficients(instruments.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 0.99));
			
			//Yield constraint
			//Greater than 3 percent
			double[] yields = getInstrumentYield(instruments);
			constraints.add(new LinearConstraint(yields, Relationship.GEQ, 3.0));
			
			//Concentration limits
			//Less than 13% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(instruments, Relationship.LEQ, new BigDecimal(0.13)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Non-negative contraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(instruments, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MINIMIZE, new NonNegativeConstraint(true), callback);
			printResult(instruments, result);
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			if(lastIteration !=  null) {
				printResult(instruments, lastIteration);
			} else {
				Logger.getLogger(CommonsFixedIncomeExample.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
	
	private void fixedIncomePortfolioMaximizeConvexity() {
		String methodName = "Fixed Income Portfolio Maximize Convexity";
		timer.start(methodName);
		System.err.println(methodName);
		SolutionCallback callback = new SolutionCallback();
		List<Instrument> instruments = getInstruments();
		try {
			double[] convexitys = getInstrumentConvexity(instruments);
			LinearObjectiveFunction f = new LinearObjectiveFunction(convexitys, 0.0);
			
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

			//Weights sum to 99% (1% in cash)
			double[] unitCoefficients = getUnitCoefficients(instruments.size());
			constraints.add(new LinearConstraint(unitCoefficients, Relationship.EQ, 0.99));
			
			//Yield constraint
			//Greater than 3 percent
			double[] yields = getInstrumentYield(instruments);
			constraints.add(new LinearConstraint(yields, Relationship.GEQ, 3.0));
			
			//Concentration limits
			//Less than 13% of the portfolio in any one instrument
			List<LinearConstraint> instrumentConcentrationConstraints = getWeightConstraints(instruments, Relationship.LEQ, new BigDecimal(0.13)); 
			constraints.addAll(instrumentConcentrationConstraints);
			
			//Non-negative contraints
			List<LinearConstraint> longOnlyConstraints = getWeightConstraints(instruments, Relationship.GEQ, BigDecimal.ZERO); 
			constraints.addAll(longOnlyConstraints);
			
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
			printResult(instruments, result);
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			if(lastIteration !=  null) {
				printResult(instruments, lastIteration);
			} else {
				Logger.getLogger(CommonsFixedIncomeExample.class.getName()).log(Level.SEVERE, e.getMessage());
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

	private void printResult(List<Instrument> instruments, PointValuePair result) {
		double[] points = result.getKey();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < points.length; i++) {
			
			if(i > 0) {
				sb.append(", ");
			}
			sb.append(String.format("%s=%s", instruments.get(i), StringUtil.formatDouble(points[i], 4)));
		}
		System.out.println(String.format("OptimizationResults Points[%s] ObjFunctionResult [%s]", 
				sb.toString(), StringUtil.formatDouble(result.getValue(), 8)));
	}

	private List<LinearConstraint> getWeightConstraints(List<Instrument> instruments, Relationship relationship, BigDecimal weight) {
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
			LinearConstraint contraint = new LinearConstraint(weightConstraintCoefficients, relationship, weight.doubleValue());
			constraints.add(contraint);
		}
		return constraints;
	}

	private List<Instrument> getInstruments() {
		List<Instrument> instruments = new ArrayList<>();
		instruments.add(new Instrument.Builder("20yBond").withDuration(9.3945).withConvexity(67.5164).withYield(8.20).build());
		instruments.add(new Instrument.Builder("15yBond").withDuration(8.6781).withConvexity(52.6504).withYield(7.20).build());
		instruments.add(new Instrument.Builder("10yBond").withDuration(7.0298).withConvexity(31.6523).withYield(6.30).build());
		instruments.add(new Instrument.Builder("5yBond").withDuration(4.2299).withConvexity(10.7844).withYield(5.00).build());
		instruments.add(new Instrument.Builder("3yBond").withDuration(2.7433).withConvexity(4.5964).withYield(3.90).build());
		instruments.add(new Instrument.Builder("3yBondDecayed2y").withDuration(1.8767).withConvexity(2.2730).withYield(3.00).build());
		instruments.add(new Instrument.Builder("3yBondDecayed1y").withDuration(0.9706).withConvexity(0.7155).withYield(2.19).build());
		instruments.add(new Instrument.Builder("3yBondDecayed6m").withDuration(0.4906).withConvexity(0.2406).withYield(1.70).build());
		instruments.add(new Instrument.Builder("1yBill").withDuration(0.9889).withConvexity(0.7334).withYield(2.25).build());
		return instruments;
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
		timer = new Timer();
		new CommonsFixedIncomeExample(1);
		new CommonsFixedIncomeExample(2);
		timer.print(true);
	}
}
