package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.SerializedName;
import ec.com.company.core.microservices.microserviciocalculodesahucio.serializers.ScaledBigDecimalSerializer;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@EqualsAndHashCode(callSuper=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultadoProcesoEstimacion extends ResultadoProceso {
    @NonNull
    @SerializedName("obd_suma")
    @JsonSerialize(using = ScaledBigDecimalSerializer.class)
    BigDecimal obd;
    @NonNull
    @SerializedName("interesNeto_suma")
    BigDecimal interesNeto;
    @NonNull
    @SerializedName("costoLaboral_suma")
    BigDecimal costoLaboral;
    @NonNull
    List<EmpleadoResponse> empleados;
}
