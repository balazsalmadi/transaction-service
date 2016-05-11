package com.transaction;


import com.fasterxml.jackson.annotation.*;

/**
 * This class represents a transaction data model. It contains the transaction amount, its type and an optional parentId.
 */
public class Transaction {

    private double amount;
    private String type;
    private Long parentId;

    @JsonCreator
    public Transaction( @JsonProperty( "amount" ) double amount, @JsonProperty( "type" ) String type ) {
        this.amount = amount;
        this.type = type;
    }

    public static Transaction rootTransaction( double amount, String type ) {
        return new Transaction( amount, type );
    }

    public static Transaction childTransaction( double amount, String type, Long parentId ) {
        Transaction transaction = rootTransaction( amount, type );
        transaction.parentId( parentId );
        return transaction;
    }

    @JsonGetter( "amount" )
    public double amount() {
        return amount;
    }

    @JsonGetter( "type" )
    public String type() {
        return type;
    }

    @JsonGetter( "parent_id" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public Long parentId() {
        return parentId;
    }

    @JsonSetter( "parent_id" )
    public void parentId( Long parentId ) {
        this.parentId = parentId;
    }

    @Override public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
