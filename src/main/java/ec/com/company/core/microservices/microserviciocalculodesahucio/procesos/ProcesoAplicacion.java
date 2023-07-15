package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.ResultadoCalculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.ResultadoDesahucio;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.*;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpresaEnum;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoTraspasoEnum;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint.ProcesoCalculo;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

public class ProcesoAplicacion extends ProcesoCalculo {
    @NonNull
    private final ResultadoProcesoActual resultadoProcesoActual;
    private final ResultadoProcesoEstimacion resultadoProcesoEstimado;
    private final ResultadoProcesoTasaAlterna resultadoProcesoTasaAlterna;
    private final ResultadoProcesoTraspasos resultadoProcesoTraspasos;

    @Builder
    public ProcesoAplicacion(@NonNull EstudioRequest estudio,
                             @NonNull ResultadoProcesoActual resultadoProcesoActual,
                             ResultadoProcesoEstimacion resultadoProcesoEstimado,
                             ResultadoProcesoTasaAlterna resultadoProcesoTasaAlterna,
                             ResultadoProcesoTraspasos resultadoProcesoTraspasos) {
        super(estudio);
        this.resultadoProcesoActual = resultadoProcesoActual;
        this.resultadoProcesoEstimado = resultadoProcesoEstimado;
        this.resultadoProcesoTasaAlterna = resultadoProcesoTasaAlterna;
        this.resultadoProcesoTraspasos = resultadoProcesoTraspasos;
    }

    @Override
    public ResultadoProcesoAplicacion perform()
            throws coreException {

        if (getEstudio().empleados().size() == 0) {
            // TODO return empty result and warn about it
            return null;
        }

        final var fechaCalculo = getEstudio().parametros().getFechaValoracion();
        final var fechaProyectada = getEstudio().parametros().getFechaProyectada();
        final var fechaEstimacion = getEstudio().parametros().getFechaEstimacion();
        final var hipotesiscompanyl = getEstudio().hipotesiscompanyl();
        final var factorAnio = ResultadoCalculator.factorAnio(fechaCalculo, fechaEstimacion);
        final var estadoEmpresa = getEstudio().empresa().estado();
        final var tipoEmpresa = TipoEmpresaEnum.getValueOf(estadoEmpresa);
        final var tipoTraspaso = TipoTraspasoEnum.getEnum(getEstudio().parametros().getTipoTraspaso());
        final var obdProcesoActual = this.resultadoProcesoActual.getObd();

        final ResultadoDesahucio resultadoAnterior = switch (tipoEmpresa) {
            case ANTIGUA -> getEstudio().estudioAnterior().resultadoDesahucio();
            case NUEVA, INICIA_OPERACIONES -> null;
        };

        final var pgcompanylCambiosSupuestosFinancieros = ResultadoCalculator.pgcompanylCambiosSupuestosFinancieros(
                this.resultadoProcesoActual.getCuentaActivos(),
                estadoEmpresa,
                obdProcesoActual,
                this.resultadoProcesoTasaAlterna);

        final var beneficiosPagados = switch (tipoEmpresa) {
            case ANTIGUA -> getEstudio().estudioAnterior().resultadoDesahucio().getBeneficiosPagados().negate();
            case NUEVA, INICIA_OPERACIONES -> BigDecimal.ZERO;
        };

        final var transferenciasEmpleados = ResultadoCalculator.transferenciasEmpleados(this.resultadoProcesoTraspasos);
        final var subTotalCLINNuevaOperacion = ResultadoCalculator.subTotalCLINNuevaOperacion(
                estadoEmpresa,
                obdProcesoActual,
                pgcompanylCambiosSupuestosFinancieros,
                beneficiosPagados,
                transferenciasEmpleados);

        // =========================================== Costo Laboral Actual ============================================
        final var costoLaboralActual = switch (tipoEmpresa) {
            case ANTIGUA -> ResultadoCalculator.costoLaboralActual_EmpresaAntigua(resultadoAnterior, factorAnio);
            case NUEVA ->
                    ResultadoCalculator.costoLaboralActual_EmpresaNueva(this.resultadoProcesoEstimado, factorAnio);
            case INICIA_OPERACIONES -> ResultadoCalculator.costoLaboralActual_EmpresaIniciaOperaciones(
                    tipoTraspaso,
                    fechaCalculo,
                    fechaProyectada,
                    hipotesiscompanyl.tasaFinancieraDescuento(),
                    subTotalCLINNuevaOperacion,
                    this.resultadoProcesoEstimado);
        };

        // ============================================ Interes Neto Actual ============================================
        final var interesNetoActual = switch (tipoEmpresa) {
            case ANTIGUA -> ResultadoCalculator.interesNetoActual_EmpresaAntigua(resultadoAnterior, factorAnio);
            case NUEVA -> ResultadoCalculator.interesNetoActual_EmpresaNueva(this.resultadoProcesoEstimado, factorAnio);
            case INICIA_OPERACIONES -> ResultadoCalculator.interesNetoActual_EmpresaIniciaOperaciones(tipoTraspaso,
                    subTotalCLINNuevaOperacion,
                    costoLaboralActual,
                    this.resultadoProcesoEstimado);
        };

        // =============================================================================================================
        final var sumandoPgAjusteExperiencia = ResultadoCalculator.sumandoPgAjusteExperiencia(
                obdProcesoActual,
                costoLaboralActual,
                interesNetoActual,
                pgcompanylCambiosSupuestosFinancieros,
                beneficiosPagados,
                transferenciasEmpleados);

        // ================================================ Obd anterior ===============================================
        final var obdAnterior = switch (tipoEmpresa) {
            case ANTIGUA -> resultadoAnterior.getObd();
            case NUEVA ->
                    ResultadoCalculator.obdAnterior_EmpresaNueva(this.resultadoProcesoEstimado, sumandoPgAjusteExperiencia);
            case INICIA_OPERACIONES -> BigDecimal.ZERO;
        };

        final var pgcompanylAjustesExperiencia = ResultadoCalculator.pgcompanylAjustesExperiencia(
                obdProcesoActual,
                obdAnterior,
                costoLaboralActual,
                interesNetoActual,
                pgcompanylCambiosSupuestosFinancieros,
                beneficiosPagados,
                transferenciasEmpleados);

        final var obdActual = obdAnterior
                .add(costoLaboralActual)
                .add(interesNetoActual)
                .add(pgcompanylCambiosSupuestosFinancieros)
                .add(pgcompanylAjustesExperiencia)
                .add(beneficiosPagados)
                .add(transferenciasEmpleados);

        final var pasivoReservaAnterior = switch (tipoEmpresa) {
            case ANTIGUA -> getEstudio().estudioAnterior().resultadoDesahucio().getReserva();
            case NUEVA, INICIA_OPERACIONES -> BigDecimal.ZERO;
        };
        final var costoNetoActual = costoLaboralActual.add(interesNetoActual);
        final var variacionReservasNoRegularizadasAnterior = obdAnterior.subtract(pasivoReservaAnterior);
        final var pgcompanylesOriActual = pgcompanylAjustesExperiencia.add(pgcompanylCambiosSupuestosFinancieros);
        final var pgReconocidasOri = variacionReservasNoRegularizadasAnterior.add(pgcompanylesOriActual);
        final var pasivoReservaActual = pasivoReservaAnterior
                .add(costoNetoActual)
                .add(pgReconocidasOri)
                .add(beneficiosPagados)
                .add(transferenciasEmpleados);

        final var costoLaboralProyectado = this.resultadoProcesoActual.getCostoLaboral();
        final var interesNetoProyectado = this.resultadoProcesoActual.getInteresNeto();
        final var costoNetoProyectado = costoLaboralProyectado.add(interesNetoProyectado);

        // ========================================= Costo Trabajadores Activos ========================================
        final var costoTrabajadoresActivos = switch (tipoEmpresa) {
            case ANTIGUA -> ResultadoCalculator.costoTrabajadoresActivos_EmpresaAntigua(
                    resultadoAnterior.getCostoLaboral(),
                    resultadoAnterior.getInteresNeto(),
                    factorAnio);
            case NUEVA -> ResultadoCalculator.costoTrabajadoresActivos_EmpresaAntigua(
                    this.resultadoProcesoEstimado.getCostoLaboral(),
                    this.resultadoProcesoEstimado.getInteresNeto(),
                    factorAnio);
            case INICIA_OPERACIONES -> costoLaboralActual.add(interesNetoActual);
        };

        final var pgcompanylesOriAnterior = switch (tipoEmpresa) {
            case ANTIGUA -> resultadoAnterior.getPgcompanylOri();
            case NUEVA, INICIA_OPERACIONES -> BigDecimal.ZERO;
        };

        // Sensibilidad
        final var variacionObdTasaDescuentoMenos = this.resultadoProcesoActual.getObdConVariacionTasaDsctoMenos().subtract(obdProcesoActual);
        final var impactoObdTasaDescuentoMenos = ResultadoCalculator.impactoObd(variacionObdTasaDescuentoMenos, obdProcesoActual);
        final var variacionObdTasaDescuentoMas = this.resultadoProcesoActual.getObdConVariacionTasaDsctoMas().subtract(obdProcesoActual);
        final var impactoObdTasaDescuentoMas = ResultadoCalculator.impactoObd(variacionObdTasaDescuentoMas, obdProcesoActual);
        final var variacionObdIncrementoSalarialMenos = this.resultadoProcesoActual.getObdConVariacionIncSalarialMenos().subtract(obdProcesoActual);
        final var impactoObdIncrementoSalarialMenos = ResultadoCalculator.impactoObd(variacionObdIncrementoSalarialMenos, obdProcesoActual);
        final var variacionObdIncrementoSalarialMas = this.resultadoProcesoActual.getObdConVariacionIncSalarialMas().subtract(obdProcesoActual);
        final var impactoObdIncrementoSalarialMas = ResultadoCalculator.impactoObd(variacionObdIncrementoSalarialMas, obdProcesoActual);
        // TODO EMFA. Wrong reference. But sensibilidad rotacion is not ready yet.
        final var variacionObdRotacionMenos = this.resultadoProcesoActual.getObdConVariacionIncSalarialMenos();
        final var impactoObdRotacionMenos = ResultadoCalculator.impactoObd(variacionObdRotacionMenos, obdProcesoActual);
        final var variacionObdRotacionMas = this.resultadoProcesoActual.getObdConVariacionIncSalarialMas();
        final var impactoObdRotacionMas = ResultadoCalculator.impactoObd(variacionObdRotacionMas, obdProcesoActual);

        // TODO EMFA creo que me falta efectoNetoOri
        /* Aqui la formula desde la db
        Nota: No considerar pgcompanylesOriAnterior a partir de 2018
        var reconocimientoSalidas = estudio.getReconocimientoSalidas();
        var reversoProvisionesOri = (reconocimientoSalidas != null && reconocimientoSalidas.equalsIgnoreCase("E")) ?
	        0.00 : servicioPasadoTotalLiquidaciones;
        variacionReservasNoRegularizadasAnterior +
        pgcompanylesOriActual +
        reversoProvisionesOri;
         */

        return ResultadoProcesoAplicacion.builder()
                .beneficiosPagados(beneficiosPagados)
                .costoLaboralActual(costoLaboralActual)
                .costoLaboralProyectado(costoLaboralProyectado)
                .costoNetoActual(costoNetoActual)
                .costoNetoProyectado(costoNetoProyectado)
                .costoTrabajadoresActivos(costoTrabajadoresActivos)
                .impactoObdIncrementoSalarialMas(impactoObdIncrementoSalarialMas)
                .impactoObdIncrementoSalarialMenos(impactoObdIncrementoSalarialMenos)
                .impactoObdRotacionMas(impactoObdRotacionMas)
                .impactoObdRotacionMenos(impactoObdRotacionMenos)
                .impactoObdTasaDescuentoMas(impactoObdTasaDescuentoMas)
                .impactoObdTasaDescuentoMenos(impactoObdTasaDescuentoMenos)
                .interesNetoActual(interesNetoActual)
                .interesNetoProyectado(interesNetoProyectado)
                .obdActual(obdActual)
                .obdAnterior(obdAnterior)
                .obdTotal(obdProcesoActual)
                .pasivoReservaActual(pasivoReservaActual)
                .pasivoReservaAnterior(pasivoReservaAnterior)
                .pgcompanylAjustesExperiencia(pgcompanylAjustesExperiencia)
                .pgcompanylCambiosSupuestosFinancieros(pgcompanylCambiosSupuestosFinancieros)
                .pgcompanylesOriActual(pgcompanylesOriActual)
                .pgcompanylesOriAnterior(pgcompanylesOriAnterior)
                .pgReconocidasOri(pgReconocidasOri)
                .sumandoPgAjusteExperiencia(sumandoPgAjusteExperiencia)
                .valorMercadoActivosPlanFinActual(BigDecimal.ZERO)
                .valorMercadoActivosPlanFinAnterior(BigDecimal.ZERO)
                .valorMercadoActivosPlanInicio(BigDecimal.ZERO)
                .variacionObdIncrementoSalarialMas(variacionObdIncrementoSalarialMas)
                .variacionObdIncrementoSalarialMenos(variacionObdIncrementoSalarialMenos)
                .variacionObdRotacionMas(variacionObdRotacionMas)
                .variacionObdRotacionMenos(variacionObdRotacionMenos)
                .variacionObdTasaDescuentoMas(variacionObdTasaDescuentoMas)
                .variacionObdTasaDescuentoMenos(variacionObdTasaDescuentoMenos)
                .variacionReservasNoRegularizadasAnterior(variacionReservasNoRegularizadasAnterior)
                .build();
    }
}
