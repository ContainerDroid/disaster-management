package com.androidiot.dm;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealVector;

public class AHPHelper {

	final static double randomConsistencyIndex[] =
		{0.0, 0.0, 0.58, 0.9, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49};
	Array2DRowRealMatrix comparisonMatrix;
	double[] weights;
	int count;
	/* Index of largest eigenvalue/eigenvector */
	RealVector largestEigenvector;
	double     largestEigenvalue;

	private double[] normalizeVector(RealVector v) {
		double[] normal = v.toArray();
		double sum = 0.0;
		for (double d : v.toArray()) {
			sum += d;
		}
		for (int k = 0; k < v.getDimension(); k++) {
			normal[k] = v.getEntry(k) / sum;
		}
		return normal;
	}

	public AHPHelper(double[][] comparisonMatrix, int count) {
		int evIdx = 0;

		this.comparisonMatrix = new
			Array2DRowRealMatrix(comparisonMatrix);
		EigenDecomposition evd = new
			EigenDecomposition(this.comparisonMatrix);
		this.count = count;

		System.out.println("Eigenvalues: ");
		for (int i = 0; i < evd.getRealEigenvalues().length; i++) {
			System.out.println(evd.getRealEigenvalues()[i]);
			if (evd.getRealEigenvalue(i) > evd.getRealEigenvalue(evIdx)) {
				evIdx = i;
			}
		}
		largestEigenvector = evd.getEigenvector(evIdx);
		largestEigenvalue  = evd.getRealEigenvalue(evIdx);
		System.out.println("evIdx=" + evIdx);
		System.out.println("EigenValue=" + evd.getRealEigenvalue(evIdx));
		weights = normalizeVector(largestEigenvector);
	}

	public double getConsistencyIndex() {
		return (largestEigenvalue - (double) count) /
			(double) (count - 1);
	}

	public double getConsistencyRatio() {
		return getConsistencyIndex() /
			randomConsistencyIndex[count] * 100.0;
	}

	public double[] getLargestEigenvector() {
		return largestEigenvector.toArray();
	}

	public double getLargestEigenvalue() {
		return largestEigenvalue;
	}

	public double[] getPriorityVector() {
		return weights;
	}
};
