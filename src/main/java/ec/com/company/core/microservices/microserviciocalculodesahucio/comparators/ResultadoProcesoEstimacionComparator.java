package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoEstimacion;

import java.util.Objects;

public class ResultadoProcesoEstimacionComparator  extends ResultadosProcesoComparator<ResultadoProcesoEstimacion> {

    @Override
    public int compare(ResultadoProcesoEstimacion rpe1, ResultadoProcesoEstimacion rpe2) {
        if (rpe1 == null && rpe2 == null) {
            return 0; // Both are null, so they are considered equal
        }

        Objects.requireNonNull(rpe1, "rpe1 cannot be null");
        Objects.requireNonNull(rpe2, "rpe2 cannot be null");

        return compareEmployees(rpe1.getEmpleados(), rpe2.getEmpleados());
    }
}
