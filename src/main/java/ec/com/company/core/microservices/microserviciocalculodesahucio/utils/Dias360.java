package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import net.finmath.time.daycount.DayCountConvention_30U_360;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Dias360 {

    private static final DayCountConvention_30U_360 DAY_COUNT_CONVENTION_30_U_360 = new DayCountConvention_30U_360();

    public static BigDecimal getDays(LocalDate fechaInicio, LocalDate fechaFin) {
        return BigDecimal.valueOf(DAY_COUNT_CONVENTION_30_U_360.getDaycount(fechaInicio, fechaFin));
    }


    public static BigDecimal getDays(Date fechaInicio, Date fechaFin) {

        LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return BigDecimal.valueOf(DAY_COUNT_CONVENTION_30_U_360.getDaycount(inicio, fin));
    }
}