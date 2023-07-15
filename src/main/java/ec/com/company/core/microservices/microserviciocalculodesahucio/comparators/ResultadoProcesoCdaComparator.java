package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoCda;

import java.util.Objects;

public class ResultadoProcesoCdaComparator extends ResultadosProcesoComparator<ResultadoProcesoCda> {

    @Override
    public int compare(ResultadoProcesoCda resultadoProcesoCda1, ResultadoProcesoCda resultadoProcesoCda2) {
        if (resultadoProcesoCda1 == null && resultadoProcesoCda2 == null) {
            return 0; // Both are null, so they are considered equal
        }

        Objects.requireNonNull(resultadoProcesoCda1, "resultadoProcesoCda1 cannot be null");
        Objects.requireNonNull(resultadoProcesoCda2, "resultadoProcesoCda2 cannot be null");

        return compareEmployees(resultadoProcesoCda1.getEmpleados(), resultadoProcesoCda2.getEmpleados());
    }
}
