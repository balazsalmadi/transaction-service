package component.com.transaction;

import com.transaction.Transaction;

public final class TransactionStringFactory {

    private TransactionStringFactory() {
    }

    public static String transactionAsString( Transaction transaction ) {
        if ( transaction.parentId() == null ) {
            return "{ \"amount\": " + transaction.amount() + ", \"type\": \"" + transaction.type() + "\"}";
        }
        return "{ \"amount\": " + transaction.amount() + ", \"type\": \"" + transaction.type() + "\", \"parent_id\": " + transaction.parentId() + "}";
    }
}
