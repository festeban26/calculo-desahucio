package ec.com.company.core.microservices.microserviciocalculodesahucio.filters;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpleadoEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProcesoEstimacionFilter implements Filter {

    private final LocalDate fechaEstimacion;

    public ProcesoEstimacionFilter(LocalDate fechaEstimacion) {
        this.fechaEstimacion = fechaEstimacion;
    }

    // Tipos de getEmpleados a los que aplica el proceso
    private final static HashSet<Integer> TIPOS_PROCESO_ESTIMACION = new HashSet<>() {{
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
            // && Check fecha estimacion. Se toma en cuenta a la persona cuando la fecha de desahucio es anterior (menor) a la fecha de estimación
            if (TIPOS_PROCESO_ESTIMACION.contains(persona.tipo())
                    && persona.fechaIngresoDesahucio().compareTo(this.fechaEstimacion) < 0) {
                personasProceso.add(persona);
            }
        }
        return personasProceso;
    }
}
