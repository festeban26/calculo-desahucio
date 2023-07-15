package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoActual;

import java.util.Objects;

public class ResultadoProcesoActualComparator
        extends ResultadosProcesoComparator<ResultadoProcesoActual> {

    @Override
    public int compare(ResultadoProcesoActual rpa1, ResultadoProcesoActual rpa2) {
        if (rpa1 == null && rpa2 == null) {
            return 0; // Both are null, so they are considered equal
        }

        Objects.requireNonNull(rpa1, "rpa1 cannot be null");
        Objects.requireNonNull(rpa2, "rpa2 cannot be null");

        return compareEmployees(rpa1.getEmpleados(), rpa2.getEmpleados());
    }
}
