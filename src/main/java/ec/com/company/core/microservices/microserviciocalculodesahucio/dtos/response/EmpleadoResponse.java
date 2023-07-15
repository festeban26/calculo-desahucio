package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ec.com.company.core.microservices.microserviciocalculodesahucio.comparators.BigDecimalComparator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.serializers.ScaledBigDecimalSerializer;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpleadoResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoResponse.class);
    @NonNull
    private String identificacion;
    private String nombreCompleto;
    private String sexo;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal ts;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal ts2;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal ts2Proyectado;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal tf;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal sueldo;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal remuneracionPromedio;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal sueldoProyectado;
    private Integer tipoCalculado;
    private Integer edad;
    private Integer edadProyectada;
    private Integer tw;
    private BigDecimal valorNominal;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal obd;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal obdProyectado;
    @With
    @Setter
    private BigDecimal interesNeto;
    @With
    @Setter
    private BigDecimal costoLaboral;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal obdConVariacionTasaDsctoMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal obdConVariacionTasaDsctoMenos;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal obdConVariacionIncSalarialMas;
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    private BigDecimal obdConVariacionIncSalarialMenos;


    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    /**
     * Compares this instance of EmpleadoResponse with another object for equality
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

        if (!(obj instanceof EmpleadoResponse that)) {
            return false;
        }

        boolean areAllFieldsEqual = compareAndLogBigDecimals(ts, that.ts, "ts")
                && compareAndLogBigDecimals(ts2, that.ts2, "ts2")
                && compareAndLogBigDecimals(ts2Proyectado, that.ts2Proyectado, "ts2Proyectado")
                && compareAndLogBigDecimals(tf, that.tf, "tf")
                && compareAndLogBigDecimals(sueldo, that.sueldo, "sueldo")
                && compareAndLogBigDecimals(sueldoProyectado, that.sueldoProyectado, "sueldoProyectado")
                && compareAndLogObjects(tipoCalculado, that.tipoCalculado, "tipoCalculado")
                && compareAndLogObjects(edad, that.edad, "edad")
                && compareAndLogObjects(edadProyectada, that.edadProyectada, "edadProyectada")
                && compareAndLogObjects(tw, that.tw, "tw")
                && compareAndLogBigDecimals(valorNominal, that.valorNominal, "valorNominal")
                && compareAndLogBigDecimals(obd, that.obd, "obd")
                && compareAndLogBigDecimals(obdProyectado, that.obdProyectado, "obdProyectado")
                && compareAndLogBigDecimals(interesNeto, that.interesNeto, "interesNeto")
                && compareAndLogBigDecimals(costoLaboral, that.costoLaboral, "costoLaboral")
                && compareAndLogBigDecimals(obdConVariacionTasaDsctoMas, that.obdConVariacionTasaDsctoMas, "obdConVariacionTasaDsctoMas")
                && compareAndLogBigDecimals(obdConVariacionTasaDsctoMenos, that.obdConVariacionTasaDsctoMenos, "obdConVariacionTasaDsctoMenos")
                && compareAndLogBigDecimals(obdConVariacionIncSalarialMas, that.obdConVariacionIncSalarialMas, "obdConVariacionIncSalarialMas")
                && compareAndLogBigDecimals(obdConVariacionIncSalarialMenos, that.obdConVariacionIncSalarialMenos, "obdConVariacionIncSalarialMenos");

        if (!areAllFieldsEqual) {
            LOGGER.error("Employee {} - EmpleadoResponse is different: {} vs {}", this.identificacion, this, that);
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
        LOGGER.error("Employee {} - {} is different: {} vs {}", this.identificacion, field, thisValue, thatValue);
    }
}

