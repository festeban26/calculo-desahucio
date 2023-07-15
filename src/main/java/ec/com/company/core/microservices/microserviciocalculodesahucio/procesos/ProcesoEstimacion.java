package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.CalculadoraPorFlujos;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.Calculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.SumAttributesInterface;
import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.ObdCalculatorInput;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoActual;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoEstimacion;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTsPorCentroDeCosto;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint.ProcesoCalculo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.utils.TablaMortalidadUtil;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;


public class ProcesoEstimacion extends ProcesoCalculo implements SumAttributesInterface {
    @NonNull
    private final ResultadoProcesoActual resultadoProcesoActual;
    private final QxsRotacionPorTs qxsRotacionPorTs;
    private final QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto;

    @Builder
    public ProcesoEstimacion(@NonNull EstudioRequest estudio,
                             @NonNull ResultadoProcesoActual resultadoProcesoActual,
                             QxsRotacionPorTs qxsRotacionPorTs,
                             QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto) {
        super(estudio);
        this.resultadoProcesoActual = resultadoProcesoActual;
        this.qxsRotacionPorTs = qxsRotacionPorTs;
        this.qxsRotacionPorTsPorCentroDeCosto = qxsRotacionPorTsPorCentroDeCosto;
    }

    @Override
    public ResultadoProcesoEstimacion perform() throws coreException {

        if (getEstudio().empleados().size() == 0) {
            // TODO return empty result and warn about it
            return null;
        }
        final var empleadosResponse = new ArrayList<EmpleadoResponse>();
        final var fechaCalculo = getEstudio().parametros().getFechaEstimacion();
        final var fechaProyectada = getEstudio().parametros().getFechaValoracion();
        final var hipotesiscompanyl = getEstudio().hipotesiscompanyl();

        var obdsProcesoActual = new HashMap<String, BigDecimal>();
        for (var empleadoResponseProcesoActual : resultadoProcesoActual.getEmpleados()) {
            var id = empleadoResponseProcesoActual.getIdentificacion();
            var obdProcesoActual = empleadoResponseProcesoActual.getObd();
            obdsProcesoActual.put(id, obdProcesoActual);
        }

        // Para cada empleado
        for (var empleadoRequest : getEstudio().empleados()) {

            final var ts = Calculator.ts(fechaCalculo, empleadoRequest.fechaIngresoJubilacion());
            final var ts2 = Calculator.ts(fechaCalculo, empleadoRequest.fechaIngresoDesahucio());
            final var tf = Calculator.tf(ts);
            final var tipo = empleadoRequest.tipo();
            final var sueldo = Calculator.sueldoEstimado(tipo, empleadoRequest.remuneracionPromedioDesahucio(),
                    hipotesiscompanyl.porcentajeIncrementoSalarialEstimadoProporcional());
            final var edad = Calculator.edad(fechaCalculo, empleadoRequest.fechaNacimiento());
            final var tw = Calculator.tw(tipo, edad, tf);
            final var valorNominal = Calculator.valorNominal(tipo, sueldo, ts2);

            QxsRotacionPorTs qxsRotacionPorTs = null;
            if (!getEstudio().parametros().getUsaTasaRotacionPorCentroDeCosto()) {
                qxsRotacionPorTs = this.qxsRotacionPorTs;
            } else {
                var nombreCentroDeCosto = AppConstants.TEXTO_CENTRO_DE_COSTO_NINGUNO;
                if (empleadoRequest.nombreCentroDeCosto() != null && empleadoRequest.nombreCentroDeCosto().trim().isEmpty()) {
                    nombreCentroDeCosto = empleadoRequest.nombreCentroDeCosto();
                    qxsRotacionPorTs = this.qxsRotacionPorTsPorCentroDeCosto.getQxsRotacionPorTs(nombreCentroDeCosto);
                }
            }

            final var tablaMortalidad = new TablaMortalidadUtil(getEstudio().parametros().getVersionTablaMortalidadDesahucio());
            var obdCalculatorInput = ObdCalculatorInput.builder()
                    .nombrePersona(empleadoRequest.nombreCompleto())
                    .tipoPersona(tipo)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldo)
                    .tsPersona(ts2)
                    .edadPersona(edad)
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento())
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios())
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obd = CalculadoraPorFlujos.obd(obdCalculatorInput);

            final var interesProporcional = Calculator.interesProporcional
                    (fechaCalculo, fechaProyectada, getEstudio().hipotesiscompanyl().tasaFinancieraDescuento());

            final var interesNeto = Calculator.interesNeto(obd, interesProporcional);

            final var obdActual = obdsProcesoActual.get(empleadoRequest.identificacion());
            final var costoLaboral = Calculator.costoLaboral(tipo, obd, obdActual, interesNeto);

            var empleadoResponse = EmpleadoResponse.builder()
                    .identificacion(empleadoRequest.identificacion())
                    .ts(ts)
                    .ts2(ts2)
                    .sueldo(sueldo)
                    .edad(edad)
                    .tf(tf)
                    .tipoCalculado(tipo)
                    .tw(tw)
                    .valorNominal(valorNominal)
                    .obd(obd)
                    .interesNeto(interesNeto)
                    .costoLaboral(costoLaboral)
                    .build();
            empleadosResponse.add(empleadoResponse);
        }

        return ResultadoProcesoEstimacion.builder()
                .empleados(empleadosResponse)
                .obd(sumAttribute(empleadosResponse, EmpleadoResponse::getObd))
                .interesNeto(sumAttribute(empleadosResponse, EmpleadoResponse::getInteresNeto))
                .costoLaboral(sumAttribute(empleadosResponse, EmpleadoResponse::getCostoLaboral))
                .build();
    }
}
