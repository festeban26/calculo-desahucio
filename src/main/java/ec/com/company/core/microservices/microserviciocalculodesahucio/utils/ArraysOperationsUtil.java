/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author esteban.flores
 */
public class ArraysOperationsUtil {

    public static final MathContext MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);

    public static BigDecimal[] substract(BigDecimal minuend, BigDecimal[] subtrahend) {
        BigDecimal[] difference = new BigDecimal[subtrahend.length];

        for (int i = 0; i < subtrahend.length; i++) {
            difference[i] = minuend.subtract(subtrahend[i]);
        }

        return difference;
    }

    public static BigDecimal[] multiply(BigDecimal[] multiplicands, int[] multipliers) {
        BigDecimal[] product = new BigDecimal[multiplicands.length];

        for (int i = 0; i < multiplicands.length; i++) {
            product[i] = multiplicands[i].multiply(new BigDecimal(multipliers[i]));
        }

        return product;
    }

    public static BigDecimal[] divide(BigDecimal dividend, BigDecimal[] divisors) {
        BigDecimal[] output = new BigDecimal[divisors.length];

        for (int i = 0; i < divisors.length; i++) {
            output[i] = ArraysOperationsUtil.divide(dividend, divisors[i]);
        }

        return output;

    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, ArraysOperationsUtil.MATH_CONTEXT);
    }

    // This function converts both the base and the exponents to double
    // so some data may be lost
    public static BigDecimal[] pow(BigDecimal base, BigDecimal[] exponents) {
        BigDecimal[] output = new BigDecimal[exponents.length];
        double baseAsDouble = base.doubleValue();

        for (int i = 0; i < output.length; i++) {
            double exponent = exponents[i].doubleValue();
            double result = Math.pow(baseAsDouble, exponent);
            output[i] = new BigDecimal(result);
        }

        return output;
    }

    public static BigDecimal[] convertToBigDecimalArray(int[] arr) {
        BigDecimal[] output = new BigDecimal[arr.length];

        for (int i = 0; i < arr.length; i++) {
            output[i] = new BigDecimal(arr[i]);
        }

        return output;
    }

    // Excel SUMPRODUCT function 
    public static BigDecimal sumproduct(BigDecimal[]... arrays) {
        BigDecimal[] product = multiply(arrays);
        return sum(product);
    }

    // All args must have the same size
    public static BigDecimal[] multiply(BigDecimal[]... arrays) {

        if (arrays.length == 1) {
            return arrays[0];

        } else {
            int lenght = arrays[0].length;
            BigDecimal[] output = new BigDecimal[lenght];

            // Copy the first array into the output. It will serve as
            // the base of the calculation
            for (int i = 0; i < lenght; i++) {
                output[i] = new BigDecimal(arrays[0][i].toString());
            }

            for (int i = 1; i < arrays.length; i++) {
                // Only continue if all arrays have the same size, else return null
                if (arrays[i].length != lenght) {
                    return null;
                }
                // multiply operation
                for (int j = 0; j < arrays[0].length; j++) {
                    output[j] = output[j].multiply(arrays[i][j]);
                }
            }
            return output;
        }
    }

    // Returns the sum of the elements of an array
    public static BigDecimal sum(BigDecimal[] array) {
        BigDecimal sum = BigDecimal.ZERO;

        for (BigDecimal value : array) {
            sum = value.add(sum);
        }

        return sum;

    }

    public static BigDecimal[] multiply(BigDecimal[] multiplicands, BigDecimal multiplier) {
        BigDecimal[] product = new BigDecimal[multiplicands.length];

        for (int i = 0; i < multiplicands.length; i++) {
            product[i] = multiplicands[i].multiply(multiplier);
        }

        return product;
    }
}
