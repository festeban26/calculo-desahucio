package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import java.util.Comparator;
import java.util.List;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResultadosProcesoComparator<T> implements Comparator<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ResultadosProcesoComparator.class);
    protected final EmpleadosListComparator empleadosListComparator = new EmpleadosListComparator();

    protected int compareEmployees(List<EmpleadoResponse> empleados1, List<EmpleadoResponse> empleados2) {
        LOGGER.debug("Comparing lists of EmpleadoResponse objects");
        return empleadosListComparator.compare(empleados1, empleados2);
    }
}