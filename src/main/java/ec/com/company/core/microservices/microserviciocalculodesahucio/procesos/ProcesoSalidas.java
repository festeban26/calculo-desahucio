package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.SalidasCalculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.SumAttributesInterface;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoSalidas;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint.ProcesoCalculo;
import lombok.NonNull;

import java.util.ArrayList;

public class ProcesoSalidas extends ProcesoCalculo implements SumAttributesInterface {

    public ProcesoSalidas(@NonNull EstudioRequest estudio) {
        super(estudio);
    }

    @Override
    public ResultadoProcesoSalidas perform() {

        if (getEstudio().empleados().size() == 0) {
            // TODO return empty result and warn about it
            return null;
        }

        final var empleadosResponse = new ArrayList<EmpleadoResponse>();
        for (var empleadoRequest : getEstudio().empleados()) {

            final var tipo = empleadoRequest.tipo();
            final var edad = SalidasCalculator.edad(getEstudio().parametros().getFechaValoracion(),
                    empleadoRequest.fechaNacimiento());
            final var ts = SalidasCalculator.ts(empleadoRequest.fechaIngresoDesahucio(),
                    getEstudio().parametros().getFechaValoracion(), getEstudio().parametros().getFechaEstimacion());
            final var obd = empleadoRequest.reservaDesahucio();
            final var costoLaboral = SalidasCalculator.costoLaboral(
                    getEstudio().parametros().getFechaValoracion(),
                    getEstudio().parametros().getFechaEstimacion(),
                    empleadoRequest.costoLaboralDesahucio());
            final var interesNeto = SalidasCalculator.interesNeto(
                    getEstudio().parametros().getFechaValoracion(),
                    getEstudio().parametros().getFechaEstimacion(),
                    empleadoRequest.interesNetoDesahucio());

            var empleadoResponse = EmpleadoResponse.builder()
                    .identificacion(empleadoRequest.identificacion())
                    .edad(edad)
                    .ts(ts)
                    .tipoCalculado(tipo)
                    .obd(obd)
                    .interesNeto(interesNeto)
                    .costoLaboral(costoLaboral)
                    .build();
            empleadosResponse.add(empleadoResponse);
        }

        return ResultadoProcesoSalidas.builder()
                .empleados(empleadosResponse)
                .obd(sumAttribute(empleadosResponse, EmpleadoResponse::getObd))
                .interesNeto(sumAttribute(empleadosResponse, EmpleadoResponse::getInteresNeto))
                .costoLaboral(sumAttribute(empleadosResponse, EmpleadoResponse::getCostoLaboral))
                .build();
    }
}
