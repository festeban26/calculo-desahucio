package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class BigDecimalComparator {
    private final static int NUMBER_OF_DECIMAL_PLACES_TO_COMPARE = 2;

    public static boolean isBigDecimalEqual(BigDecimal bd1, BigDecimal bd2) {
        // If both BigDecimals are null, they are considered equal
        if (bd1 == null && bd2 == null) {
            return true;
        }

        // If either BigDecimal is null, they are not equal
        if (bd1 == null || bd2 == null) {
            return false;
        }

        // Set the scale and rounding mode for both BigDecimals
        BigDecimal bd1Scaled = bd1.setScale(NUMBER_OF_DECIMAL_PLACES_TO_COMPARE, RoundingMode.HALF_UP);
        BigDecimal bd2Scaled = bd2.setScale(NUMBER_OF_DECIMAL_PLACES_TO_COMPARE, RoundingMode.HALF_UP);

        // Compare the two scaled BigDecimals
        int comparisonResult = bd1Scaled.compareTo(bd2Scaled);

        // If the comparison result is not 0, the BigDecimals are not equal
        return comparisonResult == 0;
    }


}
