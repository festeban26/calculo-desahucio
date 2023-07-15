package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ResultadoDesahucio {
    @NonNull
    BigDecimal costoLaboral = BigDecimal.ZERO;
    @NonNull
    BigDecimal interesNeto = BigDecimal.ZERO;
    @NonNull
    BigDecimal obd = BigDecimal.ZERO;
    BigDecimal pgcompanylOri = BigDecimal.ZERO;
    @JsonAlias("beneficiosPagados")
    BigDecimal beneficiosPagados = BigDecimal.ZERO;
    @JsonAlias("saldoContable")
    BigDecimal reserva = BigDecimal.ZERO;
}
