package com.kalinya.optimization.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.linear.SolutionCallback;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.util.FastMath;

import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

/**
 * Apache Commons-Math Optimization Documentation
 * 
 * @see <a href=
 *      "http://commons.apache.org/proper/commons-math/userguide/optimization.html">Commons-Math
 *      Optimization Documentation</a>
 */
public final class CommonsMultiVariateExample {
	private static Timer timer = null;

	public CommonsMultiVariateExample(int i) {
		if(i == 1) {
			exampleOne();
		} else if (i == 2) {
			exampleTwo();
		} else if (i == 3) {
			linearProgrammingExample();
		} else if (i == 4) {
			linearProgrammingFarmExample();
		} else if (i == 5) {
			linearProgrammingMachinesExample();
		} else {
			throw new IllegalArgumentException(String.format("Unknown argument [%s]", i));
		}
	}
	
	private void exampleOne() {
		try {
			timer.start("MultivariateFunction #1");

			/*
			 * https://image.slidesharecdn.com/commons-math-apachecon-na-2015-150415101937-conversion-gate02/95/programming-math-in-java-lessons-from-apache-commons-math-18-638.jpg?cb=1429093833
			 */
			MultivariateFunction f = getMultivariateFunction();
			PowellOptimizer optim = new PowellOptimizer(1E-13, 1e-40);
			PointValuePair result = optim.optimize(new MaxEval(1000), new ObjectiveFunction(f), GoalType.MINIMIZE,
					new InitialGuess(new double[] { -1, 1 }));
			double[] points = result.getKey();

			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", points[0], points[1], StringUtil.formatDouble(result.getValue(), 8)));
		} catch (Exception e) {
			Logger.getLogger(CommonsMultiVariateExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}
	
	private MultivariateFunction getMultivariateFunction() {
		//Multivariate optimization Find the minimum value of 100(y - x2 )2 + (1 - x)2 , starting at the point (-1, 1) 
		return new MultivariateFunction() {
				public double value(double[] x) {
					double xs = x[0] * x[0];
					return (100 * FastMath.pow((x[1] - xs), 2)) + FastMath.pow(1 - x[0], 2);
				}
			};
	}

	private void exampleTwo() {
		try {
			timer.start("MultivariateFunction #2");
			/*
			 * https://image.slidesharecdn.com/commons-math-apachecon-na-2015-150415101937-conversion-gate02/95/programming-math-in-java-lessons-from-apache-commons-math-19-638.jpg?cb=1429093833
			 */
			BOBYQAOptimizer optim = new BOBYQAOptimizer(4);
			MultivariateFunction f = getMultivariateFunction();
			PointValuePair result = optim.optimize(new MaxEval(1000), new ObjectiveFunction(f), GoalType.MINIMIZE,
					new InitialGuess(new double[] { -1, 1 }), SimpleBounds.unbounded(2));
			//Note additional parameter to optimize
			double[] points = result.getKey();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", points[0], points[1], StringUtil.formatDouble(result.getValue(), 8)));
		} catch (Exception e) {
			Logger.getLogger(CommonsMultiVariateExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}
	
	private void linearProgrammingExample() {
		timer.start("LinearProgramming");
		/*
		 * https://stackoverflow.com/questions/39309486/apache-commons-math3-how-to-use-linear-programming
		 */
		SolutionCallback callback = new SolutionCallback();
		try {
			// Linear Programming Maximize 7x1 + 3x2 subject to 3x1 - 5x3 <= 0 2x1 - 5x4 <= 0 ... (more constraints) 
			LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 3, 5 }, 0);
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
			constraints.add(new LinearConstraint(new double[] { 2, 8 }, Relationship.LEQ, 13.0));
			constraints.add(new LinearConstraint(new double[] { 5, -1 }, Relationship.LEQ, 11.0));
			constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, 0.0));
			constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, 0.0));
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
			double[] points = result.getKey();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", points[0], points[1], StringUtil.formatDouble(result.getValue(), 8)));
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", lastIteration.getKey()[0], lastIteration.getKey()[1], StringUtil.formatDouble(lastIteration.getValue(), 8)));
			Logger.getLogger(CommonsMultiVariateExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}
	
	private void linearProgrammingFarmExample() {
		timer.start("LinearProgrammingFarmExample");
		/*
		 * https://www.algebra.com/algebra/homework/coordinate/word/THEO-2011-08-28-03.lesson
		 */
		SolutionCallback callback = new SolutionCallback();
		try {
			//x1=wheat, x2=barley
			//maximize 5000.x1 + 3000.x2
			//s.t.
			// x1+x2 <= 8
			// 2.x1+x2<=10
			// x1>=0
			// x2>=0
			LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 5000, 3000 }, 0);
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
			constraints.add(new LinearConstraint(new double[] { 1,  1}, Relationship.LEQ, 8.0));
			constraints.add(new LinearConstraint(new double[] { 2, 1 }, Relationship.LEQ, 10.0));
			//Non-negative contraints
			constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, 0.0));
			constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, 0.0));
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
			double[] points = result.getKey();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", points[0], points[1], StringUtil.formatDouble(result.getValue(), 8)));
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", lastIteration.getKey()[0], lastIteration.getKey()[1], StringUtil.formatDouble(lastIteration.getValue(), 8)));
			Logger.getLogger(CommonsMultiVariateExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}
	
	private void linearProgrammingMachinesExample() {
		//http://people.brunel.ac.uk/~mastjjb/jeb/or/morelp.html
		timer.start("LinearProgrammingMachinesExample");
		SolutionCallback callback = new SolutionCallback();
		try {
			//x1=ProductX, x2=ProductY
			//maximize x1+30-75 + x2+90-95 == x1+x2-50
			//s.t.
			// 50.x1 + 24.x2 <= 40x60 == 50.x1 + 24.x2 <= 2400
			// 30.x1 + 33.x2 <= 35x60 == 30.x1 + 33.x2 <= 2100
			// x1 >= 75-30 == x1>=45
			// x2 >= 95-90 == x2>=5
			LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 1 }, -50);
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
			constraints.add(new LinearConstraint(new double[] { 50,  24}, Relationship.LEQ, 2400.0));
			constraints.add(new LinearConstraint(new double[] { 30, 33 }, Relationship.LEQ, 2100.0));
			//Non-negative contraints
			constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, 45.0));
			constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, 5.0));
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), callback);
			double[] points = result.getKey();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", points[0], points[1], StringUtil.formatDouble(result.getValue(), 8)));
		} catch (Exception e) {
			PointValuePair lastIteration = callback.getSolution();
			if(lastIteration !=  null) {
				System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", lastIteration.getKey()[0], lastIteration.getKey()[1], StringUtil.formatDouble(lastIteration.getValue(), 8)));
			} else {
				Logger.getLogger(CommonsMultiVariateExample.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
	

	public static void main(String[] args) {
		timer = new Timer();
		//new CommonsMultiVariateExample(1);
		//new CommonsMultiVariateExample(2);
		new CommonsMultiVariateExample(5);
		timer.print(true);
	}
}
