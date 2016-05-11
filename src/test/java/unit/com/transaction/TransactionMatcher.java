package unit.com.transaction;

import com.transaction.Transaction;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsAnything;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class TransactionMatcher extends TypeSafeDiagnosingMatcher<Transaction> {

    private Matcher<Double> amountMatcher = new IsAnything<>();
    private Matcher<String> typeMatcher = new IsAnything<>();
    private Matcher<Long> parentIdMatcher = new IsAnything<>();

    private TransactionMatcher() {
    }

    public static TransactionMatcher anyTransaction() {
        return new TransactionMatcher();
    }

    @Override
    protected boolean matchesSafely( Transaction transaction, Description description ) {
        List<String> mismatchMessages = new ArrayList<>();

        isMatch( amountMatcher, transaction.amount(), "amount", mismatchMessages );
        isMatch( typeMatcher, transaction.type(), "type", mismatchMessages );
        isMatch( parentIdMatcher, transaction.parentId(), "parentId", mismatchMessages );

        description.appendText( String.join( ", and ", mismatchMessages ) );
        return mismatchMessages.isEmpty();
    }

    private <K> boolean isMatch( Matcher<K> matcher, K item, String itemName, List<String> mismatchMessages ) {
        if ( !matcher.matches( item ) ) {
            mismatchMessages.add( itemName + " was " + item );
            return false;
        }
        return true;
    }

    @Override
    public void describeTo( Description description ) {
        description.appendText( "Transaction" )
                   .appendText( " which has amount " ).appendDescriptionOf( amountMatcher )
                   .appendText( ", and which has type " ).appendDescriptionOf( typeMatcher )
                   .appendText( ", and which has parentId " ).appendDescriptionOf( typeMatcher );

    }

    public TransactionMatcher whichHasAmount( double amount ) {
        amountMatcher = equalTo( amount );
        return this;
    }

    public TransactionMatcher whichHasType( String type ) {
        typeMatcher = equalTo( type );
        return this;
    }

    public TransactionMatcher whichHasParentId( long parentId ) {
        parentIdMatcher = equalTo( parentId );
        return this;
    }
}
