package com.transaction.rest;

/**
 * This class is a wrapper to provide the expected JSON response for sum of all amount in a transaction chain.
 */
public class TransactionChainSum {

    private double sum;

    public TransactionChainSum( double sum ) {
        this.sum = sum;
    }

    public double getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "TransactionChainSum{" +
                "sum=" + sum +
                '}';
    }
}
