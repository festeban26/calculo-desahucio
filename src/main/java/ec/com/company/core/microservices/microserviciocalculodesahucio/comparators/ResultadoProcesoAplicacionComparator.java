package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoAplicacion;

import java.util.Objects;

public class ResultadoProcesoAplicacionComparator
        extends ResultadosProcesoComparator<ResultadoProcesoAplicacion>{

    @Override
    public int compare(ResultadoProcesoAplicacion rpg1, ResultadoProcesoAplicacion rpg2) {
        Objects.requireNonNull(rpg1, "rpg1 cannot be null");
        Objects.requireNonNull(rpg2, "rpg2 cannot be null");
        return rpg1.equals(rpg2) ? 0 : 1;
    }
}
