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
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.apache.commons.math3.util.FastMath;

import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

public final class CommonsMultiVariateExample {
	private static Timer timer = null;

	@SuppressWarnings("unused")
	private CommonsMultiVariateExample() {
		//suppress ctor
	}
	
	public CommonsMultiVariateExample(int i) {
		if(i == 1) {
			exampleOne();
		} else if (i == 2) {
			exampleTwo();
		} else if (i == 3) {
			linearProgrammingExample();
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
		 * https://image.slidesharecdn.com/commons-math-apachecon-na-2015-150415101937-conversion-gate02/95/programming-math-in-java-lessons-from-apache-commons-math-37-638.jpg?cb=1429093833
		 */
		try {
			// Linear Programming Maximize 7x1 + 3x2 subject to 3x1 - 5x3 <= 0 2x1 - 5x4 <= 0 ... (more constraints) 
			LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 7, 3, 0, 0 }, 0);
			List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
			constraints.add(new LinearConstraint(new double[] { 3, 0, -5, 0 }, Relationship.LEQ, 0.0));
			constraints.add(new LinearConstraint(new double[] { 2, 0, 0, -5 }, Relationship.LEQ, 0.0));
			SimplexSolver solver = new SimplexSolver();
			PointValuePair result = solver.optimize(new MaxIter(100), f, new LinearConstraintSet(constraints),
					GoalType.MAXIMIZE, new NonNegativeConstraint(true), SimpleBounds.unbounded(constraints.size()));
			double[] points = result.getKey();
			System.out.println(String.format("OptimizationResults Points[%s, %s] ObjFunctionResult [%s]", points[0], points[1], StringUtil.formatDouble(result.getValue(), 8)));
		} catch (Exception e) {
			Logger.getLogger(CommonsMultiVariateExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}

	public static void main(String[] args) {
		timer = new Timer();
		new CommonsMultiVariateExample(1);
		new CommonsMultiVariateExample(2);
		new CommonsMultiVariateExample(3);
		timer.print(true);
	}
}
