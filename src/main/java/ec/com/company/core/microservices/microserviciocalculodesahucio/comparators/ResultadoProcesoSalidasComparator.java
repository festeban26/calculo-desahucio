package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoSalidas;

import java.util.Objects;

public class ResultadoProcesoSalidasComparator extends ResultadosProcesoComparator<ResultadoProcesoSalidas> {

    @Override
    public int compare(ResultadoProcesoSalidas rps1, ResultadoProcesoSalidas rps2) {
        if (rps1 == null && rps2 == null) {
            return 0; // Both are null, so they are considered equal
        }

        Objects.requireNonNull(rps1, "rps1 cannot be null");
        Objects.requireNonNull(rps2, "rps2 cannot be null");

        return compareEmployees(rps1.getEmpleados(), rps2.getEmpleados());
    }
}
