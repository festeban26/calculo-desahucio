package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ec.com.company.core.microservices.microserviciocalculodesahucio.comparators.BigDecimalComparator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.serializers.ScaledBigDecimalSerializer;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultadoProcesoAplicacion extends ResultadoProceso {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultadoProcesoAplicacion.class);
    private final static int NUMBER_OF_DECIMAL_PLACES_TO_COMPARE = 2;

    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal beneficiosPagados;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal costoLaboralActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal costoLaboralProyectado;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal costoNetoActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal costoNetoProyectado;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal costoTrabajadoresActivos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal impactoObdIncrementoSalarialMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal impactoObdIncrementoSalarialMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal impactoObdRotacionMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal impactoObdRotacionMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal impactoObdTasaDescuentoMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal impactoObdTasaDescuentoMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal interesNetoActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal interesNetoProyectado;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal obdActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal obdAnterior;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal obdTotal;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal pasivoReservaActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal pasivoReservaAnterior;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal pgcompanylesOriActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal pgcompanylesOriAnterior;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal pgcompanylAjustesExperiencia;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal pgcompanylCambiosSupuestosFinancieros;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal pgReconocidasOri;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal sumandoPgAjusteExperiencia;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal valorMercadoActivosPlanFinActual;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal valorMercadoActivosPlanFinAnterior;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal valorMercadoActivosPlanInicio;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionObdIncrementoSalarialMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionObdIncrementoSalarialMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionObdRotacionMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionObdRotacionMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionObdTasaDescuentoMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionObdTasaDescuentoMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @NonNull
    BigDecimal variacionReservasNoRegularizadasAnterior;


    /**
     * Compares this instance of ResultadoProcesoGlobal with another object for equality
     * by checking various fields and logs any differences found.
     *
     * @param obj The object to be compared with this instance.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ResultadoProcesoAplicacion that)) {
            return false;
        }

        boolean areAllFieldsEqual = compareAndLogBigDecimals(beneficiosPagados, that.beneficiosPagados, "beneficios_pagados")
                && compareAndLogBigDecimals(costoLaboralActual, that.costoLaboralActual, "costoLaboralActual")
                && compareAndLogBigDecimals(costoLaboralProyectado, that.costoLaboralProyectado, "costoLaboralProyectado")
                && compareAndLogBigDecimals(costoNetoActual, that.costoNetoActual, "costoNetoActual")
                && compareAndLogBigDecimals(costoNetoProyectado, that.costoNetoProyectado, "costoNetoProyectado")
                && compareAndLogBigDecimals(costoTrabajadoresActivos, that.costoTrabajadoresActivos, "costoTrabajadoresActivos")
                && compareAndLogBigDecimals(impactoObdIncrementoSalarialMas, that.impactoObdIncrementoSalarialMas, "impactoObdIncrementoSalarialMas")
                && compareAndLogBigDecimals(impactoObdIncrementoSalarialMenos, that.impactoObdIncrementoSalarialMenos, "impactoObdIncrementoSalarialMenos")
                // TODO EMFA pruebas sensibilidad rotacion
                //&& compareAndLogBigDecimals(impactoObdRotacionMas, that.impactoObdRotacionMas, "impactoObdRotacionMas")
                //&& compareAndLogBigDecimals(impactoObdRotacionMenos, that.impactoObdRotacionMenos, "impactoObdRotacionMenos")
                && compareAndLogBigDecimals(impactoObdTasaDescuentoMas, that.impactoObdTasaDescuentoMas, "impactoObdTasaDescuentoMas")
                && compareAndLogBigDecimals(impactoObdTasaDescuentoMenos, that.impactoObdTasaDescuentoMenos, "impactoObdTasaDescuentoMenos")
                && compareAndLogBigDecimals(interesNetoActual, that.interesNetoActual, "interesNetoActual")
                && compareAndLogBigDecimals(interesNetoProyectado, that.interesNetoProyectado, "interesNetoProyectado")
                && compareAndLogBigDecimals(obdActual, that.obdActual, "obdActual")
                && compareAndLogBigDecimals(obdAnterior, that.obdAnterior, "obdAnterior")
                && compareAndLogBigDecimals(pasivoReservaActual, that.pasivoReservaActual, "pasivoReservaActual")
                && compareAndLogBigDecimals(pasivoReservaAnterior, that.pasivoReservaAnterior, "pasivoReservaAnterior")
                && compareAndLogBigDecimals(pgcompanylesOriActual, that.pgcompanylesOriActual, "pgcompanylesOriActual")
                && compareAndLogBigDecimals(pgcompanylesOriAnterior, that.pgcompanylesOriAnterior, "pgcompanylesOriAnterior")
                && compareAndLogBigDecimals(pgcompanylAjustesExperiencia, that.pgcompanylAjustesExperiencia, "pgcompanylAjustesExperiencia")
                && compareAndLogBigDecimals(pgcompanylCambiosSupuestosFinancieros, that.pgcompanylCambiosSupuestosFinancieros, "pgcompanylCambiosSupuestosFinancieros")
                && compareAndLogBigDecimals(pgReconocidasOri, that.pgReconocidasOri, "pgReconocidasOri")
                && compareAndLogBigDecimals(variacionObdIncrementoSalarialMas, that.variacionObdIncrementoSalarialMas, "variacionObdIncrementoSalarialMas")
                && compareAndLogBigDecimals(variacionObdIncrementoSalarialMenos, that.variacionObdIncrementoSalarialMenos, "variacionObdIncrementoSalarialMenos")
                // TODO EMFA pruebas sensibilidad rotacion
                //&& compareAndLogBigDecimals(variacionObdRotacionMas, that.variacionObdRotacionMas, "variacionObdRotacionMas")
                //&& compareAndLogBigDecimals(variacionObdRotacionMenos, that.variacionObdRotacionMenos, "variacionObdRotacionMenos")
                && compareAndLogBigDecimals(variacionObdTasaDescuentoMas, that.variacionObdTasaDescuentoMas, "variacionObdTasaDescuentoMas")
                && compareAndLogBigDecimals(variacionObdTasaDescuentoMenos, that.variacionObdTasaDescuentoMenos, "variacionObdTasaDescuentoMenos")
                && compareAndLogBigDecimals(variacionReservasNoRegularizadasAnterior, that.variacionReservasNoRegularizadasAnterior, "variacionReservasNoRegularizadasAnterior");

        if (!areAllFieldsEqual) {
            LOGGER.error("ResultadoProcesoGlobal is different: {} vs {}", this, that);
        }
        return areAllFieldsEqual;
    }

    private boolean compareAndLogBigDecimals(BigDecimal field1, BigDecimal field2, String fieldName) {
        boolean isEqual = BigDecimalComparator.isBigDecimalEqual(field1, field2);
        if (!isEqual) {
            logDifference(fieldName, field1, field2);
        }
        return isEqual;
    }

    private boolean compareAndLogObjects(Object field1, Object field2, String fieldName) {
        boolean isEqual = Objects.equals(field1, field2);
        if (!isEqual) {
            logDifference(fieldName, field1, field2);
        }
        return isEqual;
    }

    private void logDifference(String field, Object thisValue, Object thatValue) {
        LOGGER.error("ResultadoProcesoGlobal - {} is different: {} vs {}", field, thisValue, thatValue);
    }

}
