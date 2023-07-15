package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.CalculadoraPorFlujos;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.*;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.Filter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.ProcesoActualFilter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.ProcesoEstimacionFilter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.filters.ProcesoSalidasFilter;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTsPorCentroDeCosto;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public abstract class EjecutarCalculoUtil {

    public static ResultadoProcesoActual performProcesoActual(EstudioRequest estudioRequest) throws coreException {

        var filtroActual = new ProcesoActualFilter();
        var empleadosActual = filtroActual.filter(estudioRequest.empleados());
        var estudioParaActual = estudioRequest.withEmpleados(empleadosActual);
        BigDecimal tasaRotacionPromedio = estudioRequest.hipotesiscompanyl().tasaRotacionPromedio().multiply(BigDecimal.valueOf(0.01));
        QxsRotacionResult qxsRotacionResult = getQxsRotacionResult(estudioRequest, tasaRotacionPromedio, empleadosActual);
        // TODO sensibilidad

        return ProcesoActual.builder()
                .estudio(estudioParaActual)
                .qxsRotacionPorTs(qxsRotacionResult.qxsRotacionPorTs.orElse(null))
                .qxsRotacionPorTsPorCentroDeCosto(qxsRotacionResult.qxsRotacionPorTsPorCentroDeCosto.orElse(null))
                .build()
                .perform();
    }

    public static ResultadoProcesoEstimacion performProcesoEstimacion(EstudioRequest estudioRequest,
                                                                      ResultadoProcesoActual resultadoActual) throws coreException {

        var filtroEstimacion = new ProcesoEstimacionFilter(estudioRequest.parametros().getFechaEstimacion());
        var empleadosEstimacion = filtroEstimacion.filter(estudioRequest.empleados());
        var estudioParaEstimacion = estudioRequest.withEmpleados(empleadosEstimacion);
        BigDecimal tasaRotacionPromedio = estudioRequest.hipotesiscompanyl().tasaRotacionPromedio().multiply(BigDecimal.valueOf(0.01));
        QxsRotacionResult qxsRotacionResult = getQxsRotacionResult(estudioRequest, tasaRotacionPromedio, estudioRequest.empleados());

        return ProcesoEstimacion.builder()
                .estudio(estudioParaEstimacion)
                .resultadoProcesoActual(resultadoActual)
                .qxsRotacionPorTs(qxsRotacionResult.qxsRotacionPorTs.orElse(null))
                .qxsRotacionPorTsPorCentroDeCosto(qxsRotacionResult.qxsRotacionPorTsPorCentroDeCosto.orElse(null))
                .build()
                .perform();
    }

    public static ResultadoProcesoTasaAlterna performProcesoTasaAlterna(EstudioRequest estudioRequest) throws coreException {

        var filtroTasaAlterna = new ProcesoActualFilter();
        var empleadosTasaAlterna = filtroTasaAlterna.filter(estudioRequest.empleados());
        var estudioParaTasaAlterna = estudioRequest.withEmpleados(empleadosTasaAlterna);
        var tasaRotacionPromedioAnterior = estudioRequest.estudioAnterior().hipotesiscompanyl().tasaRotacionPromedio()
                .multiply(BigDecimal.valueOf(0.01));
        QxsRotacionResult qxsRotacionResult = getQxsRotacionResult(estudioRequest, tasaRotacionPromedioAnterior, empleadosTasaAlterna);

        return ProcesoTasaAlterna.builder()
                .estudio(estudioParaTasaAlterna)
                .qxsRotacionPorTs(qxsRotacionResult.qxsRotacionPorTs.orElse(null))
                .qxsRotacionPorTsPorCentroDeCosto(qxsRotacionResult.qxsRotacionPorTsPorCentroDeCosto.orElse(null))
                .build()
                .perform();
    }

    public static ResultadoProcesoCda performProcesoCda(EstudioRequest estudioRequest,
                                                        ResultadoProcesoActual resultadoActual,
                                                        ResultadoProcesoEstimacion resultadoProcesoEstimacion)
            throws coreException {
        Filter filtroCda = ProcesoComposicionDemograficaAnterior.getFilterDependingOnEstadoEmpresa(estudioRequest);
        var empleadosCda = filtroCda.filter(estudioRequest.empleados());
        var estudioParaCda = estudioRequest.withEmpleados(empleadosCda);
        var procesoCda = new ProcesoComposicionDemograficaAnterior(estudioParaCda, resultadoActual, resultadoProcesoEstimacion);
        return procesoCda.perform();
    }

    public static ResultadoProcesoSalidas performProcesoSalidas(EstudioRequest estudioRequest) {

        var filtroSalidas = new ProcesoSalidasFilter();
        var empleadosSalidas = filtroSalidas.filter(estudioRequest.empleados());
        var estudioParaSalidas = estudioRequest.withEmpleados(empleadosSalidas);

        var procesoSalidas = new ProcesoSalidas(estudioParaSalidas);
        return procesoSalidas.perform();
    }

    public static ResultadoProcesoAplicacion performProcesoAplicacion(EstudioRequest estudioRequest, ResultadoCalculo resultadoCalculo)
            throws coreException {
        return ProcesoAplicacion.builder()
                .estudio(estudioRequest)
                .resultadoProcesoActual(resultadoCalculo.getActual())
                .resultadoProcesoEstimado(resultadoCalculo.getEstimacion())
                .resultadoProcesoTasaAlterna(resultadoCalculo.getTasaAlterna())
                // TODO proceso traspasos
                .resultadoProcesoTraspasos(null)
                .build()
                .perform();
    }

    private record QxsRotacionResult(Optional<QxsRotacionPorTs> qxsRotacionPorTs,
                                     Optional<QxsRotacionPorTsPorCentroDeCosto> qxsRotacionPorTsPorCentroDeCosto) {
    }

    private static QxsRotacionResult getQxsRotacionResult(EstudioRequest estudioRequest, BigDecimal tasaRotacionPromedio, List<EmpleadoRequest> empleadosParaQxRotacion)
            throws coreException {
        boolean usaTasaRotacionPorCentroDeCosto = estudioRequest.parametros().getUsaTasaRotacionPorCentroDeCosto();
        LocalDate fechaValoracion = estudioRequest.parametros().getFechaValoracion();

        QxsRotacionPorTs qxsRotacionPorTs = !usaTasaRotacionPorCentroDeCosto ? QxsUtil.getQxsRotacionPorTs_desahucio(
                fechaValoracion,
                tasaRotacionPromedio,
                CalculadoraPorFlujos.PARAM_CREDIBILIDAD_ROTACION,
                empleadosParaQxRotacion) : null;

        QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto = usaTasaRotacionPorCentroDeCosto ? QxsUtil.getQxsRotacionPorTsPorCentroCosto_desahucio(
                fechaValoracion,
                tasaRotacionPromedio,
                CalculadoraPorFlujos.PARAM_CREDIBILIDAD_ROTACION,
                empleadosParaQxRotacion,
                estudioRequest.centrosDeCosto()) : null;

        return new QxsRotacionResult(Optional.ofNullable(qxsRotacionPorTs), Optional.ofNullable(qxsRotacionPorTsPorCentroDeCosto));
    }
}
