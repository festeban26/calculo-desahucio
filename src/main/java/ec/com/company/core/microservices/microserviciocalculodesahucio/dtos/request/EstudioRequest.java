package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;

import jakarta.validation.Valid;
import lombok.NonNull;

import java.util.List;

public record EstudioRequest(
        @Valid
        @NonNull
        Empresa empresa,
        @Valid
        EstudioAnterior estudioAnterior,
        @Valid
        @NonNull
        Hipotesiscompanyl hipotesiscompanyl,
        @Valid
        List<EmpleadoRequest> empleados,
        @Valid
        Parametros parametros,
        @NonNull
        String numeroProceso,
        List<CentrosDeCostoRequest> centrosDeCosto) {
    public EstudioRequest withEmpleados(List<EmpleadoRequest> empleados) {
        return new EstudioRequest(empresa(),
                estudioAnterior(),
                hipotesiscompanyl(),
                empleados,
                parametros(),
                numeroProceso(),
                centrosDeCosto());
    }
}
