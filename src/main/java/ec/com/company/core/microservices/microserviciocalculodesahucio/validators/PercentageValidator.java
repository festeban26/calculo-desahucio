package ec.com.company.core.microservices.microserviciocalculodesahucio.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class PercentageValidator implements Validator {

    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?%?$");

    @Override
    public boolean supports(Class<?> clazz) {
        return BigDecimal.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BigDecimal value = (BigDecimal) target;

        if (value != null) {
            String stringValue = value.toString();

            if (!PERCENTAGE_PATTERN.matcher(stringValue).matches()) {
                errors.rejectValue(null, "percentage.invalid", "Invalid percentage value: " + stringValue);
            } else if (stringValue.contains(".")) {
                int decimalPlaces = stringValue.substring(stringValue.indexOf('.') + 1).length();

                if (decimalPlaces > 2) {
                    errors.rejectValue(null, "percentage.decimal-places", "Invalid percentage value: too many decimal places");
                }
            } else if (stringValue.endsWith("%")) {
                errors.rejectValue(null, "percentage.invalid", "Invalid percentage value: percentage sign should not be included in the input value");
            }
        }
    }
}