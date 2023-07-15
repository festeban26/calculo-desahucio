package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProceso;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class ProcesoCalculo implements ProcesoStrategy<ResultadoProceso> {
    @NonNull
    private final EstudioRequest estudio;
    public ProcesoCalculo(@NonNull EstudioRequest estudio) {
        this.estudio = estudio;
    }

    public EstudioRequest getEstudio() {
        return estudio;
    }
}
