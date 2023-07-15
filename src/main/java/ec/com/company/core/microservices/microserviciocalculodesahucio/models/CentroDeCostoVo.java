package ec.com.company.core.microservices.microserviciocalculodesahucio.models;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Value
public class CentroDeCostoVo {

    String nombre;
    BigDecimal tasaRotacion;
    @Builder.Default
    List<EmpleadoRequest> empleados = new ArrayList<>();

}
