package ec.com.company.core.microservices.microserviciocalculodesahucio.filters;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpleadoEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProcesoActualFilter implements Filter {
    // Tipos de getEmpleados a los que aplica el proceso
    private final HashSet<Integer> TIPOS_PROCESO_ACTUAL = new HashSet<>() {{
        add(TipoEmpleadoEnum.TRASPASO_ENTRANTE.getTipo());
        add(TipoEmpleadoEnum.JUBILADO_PENDIENTE_PAGO.getTipo());
        add(TipoEmpleadoEnum.JUBILADO_PENSIONISTA.getTipo());
        add(TipoEmpleadoEnum.TS_MAYOR_IGUAL_25.getTipo());
        add(TipoEmpleadoEnum.TS_ENTRE_10_Y_25.getTipo());
        add(TipoEmpleadoEnum.TS_MENOR_A_10.getTipo());

    }};

    @Override
    public List<EmpleadoRequest> filter(List<EmpleadoRequest> personas) {
        List<EmpleadoRequest> personasProceso = new ArrayList<>();
        for (var persona : personas) {
            // Check tipos
            if (TIPOS_PROCESO_ACTUAL.contains(persona.tipo())) {
                personasProceso.add(persona);
            }
        }
        return personasProceso;
    }
}
