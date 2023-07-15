package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.SerializedName;
import ec.com.company.core.microservices.microserviciocalculodesahucio.serializers.ScaledBigDecimalSerializer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultadoProcesoActual extends ResultadoProceso {
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal tfPromedio;
    @SerializedName("cuentaActivos_suma")
    Integer cuentaActivos;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @SerializedName("obd_suma")
    BigDecimal obd;
    @NonNull
    @SerializedName("interesNeto_suma")
    BigDecimal interesNeto;
    @NonNull
    @SerializedName("costoLaboral_suma")
    BigDecimal costoLaboral;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    @SerializedName("valorNominal_suma")
    BigDecimal valorNominal;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal obdConVariacionTasaDsctoMas;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal obdConVariacionTasaDsctoMenos;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal obdConVariacionIncSalarialMas;
    @NonNull
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal obdConVariacionIncSalarialMenos;
    @NonNull
    List<EmpleadoResponse> empleados;
}
