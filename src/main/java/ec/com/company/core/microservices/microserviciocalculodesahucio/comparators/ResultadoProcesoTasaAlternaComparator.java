package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoTasaAlterna;

import java.util.Objects;

public class ResultadoProcesoTasaAlternaComparator extends ResultadosProcesoComparator<ResultadoProcesoTasaAlterna> {

    @Override
    public int compare(ResultadoProcesoTasaAlterna rpta1, ResultadoProcesoTasaAlterna rpta2) {
        if (rpta1 == null && rpta2 == null) {
            return 0; // Both are null, so they are considered equal
        }

        Objects.requireNonNull(rpta1, "rpta1 cannot be null");
        Objects.requireNonNull(rpta2, "rpta2 cannot be null");

        return compareEmployees(rpta1.getEmpleados(), rpta2.getEmpleados());
    }
}

