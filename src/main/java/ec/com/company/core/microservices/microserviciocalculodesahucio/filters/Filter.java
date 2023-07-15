package ec.com.company.core.microservices.microserviciocalculodesahucio.filters;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;

import java.util.List;

public interface Filter {
    List<EmpleadoRequest> filter(List<EmpleadoRequest> personas);
}
