package ec.com.company.core.microservices.microserviciocalculodesahucio.filters;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpleadoEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProcesoSalidasFilter implements Filter {

    // Tipos de getEmpleados a los que aplica el proceso
    private final HashSet<Integer> TIPOS_PROCESO_SALIDAS = new HashSet<>() {{
        add(TipoEmpleadoEnum.SALIDA_SIN_PAGO.getTipo());
        add(TipoEmpleadoEnum.FALLECIDO.getTipo());
        add(TipoEmpleadoEnum.PAGO.getTipo());
    }};

    @Override
    public List<EmpleadoRequest> filter(List<EmpleadoRequest> personas) {
        List<EmpleadoRequest> personasProceso = new ArrayList<>();
        for (var persona : personas) {
            // Check tipos
            if (TIPOS_PROCESO_SALIDAS.contains(persona.tipo())) {
                personasProceso.add(persona);
            }
        }
        return personasProceso;
    }
}
