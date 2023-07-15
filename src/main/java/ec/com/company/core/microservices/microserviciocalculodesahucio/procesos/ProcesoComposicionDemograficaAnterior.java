package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.ComposicionDemograficaAnteriorCalculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.SumAttributesInterface;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoActual;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoCda;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoEstimacion;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpresaEnum;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.Filter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.ProcesoActualFilter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.ProcesoEstimacionFilter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint.ProcesoCalculo;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Solo se realiza cuando la getEmpresa es antigua. La única diferencia es que la tasa de descuento es la tasa de descuento
 * usada en el proceso anterior.
 */
public class ProcesoComposicionDemograficaAnterior extends ProcesoCalculo implements SumAttributesInterface {

    @NonNull
    @Getter
    private final ResultadoProcesoActual resultadoProcesoActual;
    @NonNull
    @Getter
    final ResultadoProcesoEstimacion resultadoProcesoEstimacion;


    @Builder
    public ProcesoComposicionDemograficaAnterior(@NonNull EstudioRequest estudio,
                                                 @NonNull ResultadoProcesoActual resultadoProcesoActual,
                                                 ResultadoProcesoEstimacion resultadoProcesoEstimacion) {
        super(estudio);
        this.resultadoProcesoActual = resultadoProcesoActual;
        this.resultadoProcesoEstimacion = resultadoProcesoEstimacion;
    }

    @Override
    public ResultadoProcesoCda perform() throws coreException {

        if (getEstudio().empleados().size() == 0) {
            // TODO return empty result and warn about it
            return null;
        }

        final var fechaCalculo = getEstudio().parametros().getFechaValoracion();
        final var fechaProyectada = getEstudio().parametros().getFechaProyectada();
        final var fechaEstimacion = getEstudio().parametros().getFechaEstimacion();
        final var hipotesiscompanyl = getEstudio().hipotesiscompanyl();

        final var proporcion = ComposicionDemograficaAnteriorCalculator.proporcion(fechaCalculo, fechaEstimacion);
        final var interesProporcional = ComposicionDemograficaAnteriorCalculator.interesProporcional
                (fechaCalculo, fechaProyectada, hipotesiscompanyl.tasaFinancieraDescuento());

        TipoEmpresaEnum tipoEmpresa = TipoEmpresaEnum.getValueOf(getEstudio().empresa().estado());

        HashMap<String, EmpleadoResponse> empleadosResponseProcesoAnterior = getEmpleadosProcesoAnterior(tipoEmpresa);
        HashMap<String, EmpleadoResponse> empleadosResponseProcesoActual = getEmpleadosProcesoActual(tipoEmpresa);

        var empleadosResponse = new ArrayList<EmpleadoResponse>();
        // Para cada empleado del request
        for (var empleadoRequest : getEstudio().empleados()) {

            int tipo = empleadoRequest.tipo();

            EmpleadoResponse empleadoProcesoAnterior = null;
            // Solo para empresas nuevas o que inician operaciones
            if (tipoEmpresa == TipoEmpresaEnum.NUEVA) {
                empleadoProcesoAnterior = empleadosResponseProcesoAnterior.get(empleadoRequest.identificacion());
            }

            EmpleadoResponse empleadoProcesoActual = null;
            // Solo para empresas nuevas o que inician operaciones
            if (tipoEmpresa == TipoEmpresaEnum.INICIA_OPERACIONES) {
                empleadoProcesoActual = empleadosResponseProcesoActual.get(empleadoRequest.identificacion());
            }

            var obd = switch (tipoEmpresa) {
                case ANTIGUA ->
                        ComposicionDemograficaAnteriorCalculator.obdAntigua(tipo, empleadoRequest.reservaDesahucio());
                case NUEVA -> ComposicionDemograficaAnteriorCalculator.obdNueva(tipo, empleadoProcesoAnterior.getObd());
                case INICIA_OPERACIONES -> BigDecimal.ZERO;
            };

            var costoLaboral = switch (tipoEmpresa) {
                case ANTIGUA -> ComposicionDemograficaAnteriorCalculator
                        .costoLaboralAntigua(tipo, empleadoRequest.costoLaboralDesahucio(), proporcion);
                case NUEVA -> ComposicionDemograficaAnteriorCalculator
                        .costoLaboralNueva(tipo, empleadoProcesoAnterior.getCostoLaboral(), proporcion);
                case INICIA_OPERACIONES -> ComposicionDemograficaAnteriorCalculator
                        .costoLaboralIniciaOperaciones(tipo, empleadoProcesoActual.getObd(), proporcion, interesProporcional);
            };

            var interesNeto = switch (tipoEmpresa) {
                case ANTIGUA -> ComposicionDemograficaAnteriorCalculator
                        .interesNetoAntigua(tipo, empleadoRequest.interesNetoDesahucio(), proporcion);
                case NUEVA -> ComposicionDemograficaAnteriorCalculator
                        .interesNetoNueva(tipo, empleadoProcesoAnterior.getInteresNeto(), proporcion);
                case INICIA_OPERACIONES -> ComposicionDemograficaAnteriorCalculator
                        .interesNetoIniciaOperaciones(tipo, empleadoProcesoActual.getObd(), costoLaboral, proporcion);
            };

            var empleadoResponse = EmpleadoResponse.builder()
                    .identificacion(empleadoRequest.identificacion())
                    .obd(obd)
                    .interesNeto(interesNeto)
                    .costoLaboral(costoLaboral)
                    .build();
            empleadosResponse.add(empleadoResponse);
        }

        return ResultadoProcesoCda.builder()
                .empleados(empleadosResponse)
                .obd(sumAttribute(empleadosResponse, EmpleadoResponse::getObd))
                .interesNeto(sumAttribute(empleadosResponse, EmpleadoResponse::getInteresNeto))
                .costoLaboral(sumAttribute(empleadosResponse, EmpleadoResponse::getCostoLaboral))
                .build();
    }

    // Si es nueva se lo realiza con las personas del proceso de estimacion y los resultados de estimacion
    public static Filter getFilterDependingOnEstadoEmpresa(EstudioRequest estudio) {
        if (estudio.empresa().estado().equalsIgnoreCase(TipoEmpresaEnum.NUEVA.getCodigo())) {
            return new ProcesoEstimacionFilter(estudio.parametros().getFechaEstimacion());
        } else {
            return new ProcesoActualFilter();
        }
    }

    private HashMap<String, EmpleadoResponse> getEmpleadosProcesoAnterior(TipoEmpresaEnum tipoEmpresa) {
        if (tipoEmpresa == TipoEmpresaEnum.NUEVA) {
            return resultadoProcesoEstimacion.getEmpleados()
                    .stream()
                    .collect(Collectors.toMap(EmpleadoResponse::getIdentificacion, empleado -> empleado, (e1, e2) -> e1, HashMap::new));
        }
        return null;
    }

    private HashMap<String, EmpleadoResponse> getEmpleadosProcesoActual(TipoEmpresaEnum tipoEmpresa) {
        if (tipoEmpresa == TipoEmpresaEnum.INICIA_OPERACIONES) {
            return resultadoProcesoActual.getEmpleados()
                    .stream()
                    .collect(Collectors.toMap(EmpleadoResponse::getIdentificacion, empleado -> empleado, (e1, e2) -> e1, HashMap::new));
        }
        return null;
    }
}
