package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultadoCalculo {
    ResultadoProcesoActual actual;
    ResultadoProcesoEstimacion estimacion;
    ResultadoProcesoSalidas salidas;
    ResultadoProcesoTraspasos traspasos;
    ResultadoProcesoTasaAlterna tasaAlterna;
    ResultadoProcesoCda composicionDemograficaAnterior;
    ResultadoProcesoAplicacion resultadoAplicacion;
}
