package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.CalculadoraPorFlujos;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.Calculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.SumAttributesInterface;
import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.ObdCalculatorInput;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoTasaAlterna;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTsPorCentroDeCosto;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint.ProcesoCalculo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.utils.TablaMortalidadUtil;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Solo se realiza cuando la getEmpresa es antigua. La única diferencia es que la tasa de descuento es la tasa de descuento
 * usada en el proceso anterior.
 */
public class ProcesoTasaAlterna extends ProcesoCalculo implements SumAttributesInterface {
    private final QxsRotacionPorTs qxsRotacionPorTs;
    private final QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto;
    @Builder
    public ProcesoTasaAlterna(@NonNull EstudioRequest estudio,
                              QxsRotacionPorTs qxsRotacionPorTs,
                              QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto) {
        super(estudio);
        this.qxsRotacionPorTs = qxsRotacionPorTs;
        this.qxsRotacionPorTsPorCentroDeCosto = qxsRotacionPorTsPorCentroDeCosto;
    }

    @Override
    public ResultadoProcesoTasaAlterna perform() throws coreException {

        if (getEstudio().empleados().size() == 0) {
            // TODO return empty result and warn about it
            return null;
        }

        final var fechaCalculo = getEstudio().parametros().getFechaValoracion();
        final var hipotesiscompanylAnterior = getEstudio().estudioAnterior().hipotesiscompanyl();

        final var empleadosResponse = new ArrayList<EmpleadoResponse>();
        // Para cada empleado
        for (var empleadoRequest : getEstudio().empleados()) {
            final var ts = Calculator.ts(fechaCalculo, empleadoRequest.fechaIngresoJubilacion());
            final var ts2 = Calculator.ts(fechaCalculo, empleadoRequest.fechaIngresoDesahucio());
            final var tipoCalculado = Calculator.tipoCalculado(empleadoRequest.tipo(), ts);
            final var sueldo = Calculator.sueldo(empleadoRequest.tipo(), empleadoRequest.remuneracionPromedioDesahucio());
            final var edad = Calculator.edad(fechaCalculo, empleadoRequest.fechaNacimiento());

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
                    .tipoPersona(tipoCalculado)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldo)
                    .tsPersona(ts2)
                    .edadPersona(edad)
                    .tasaDescuento(hipotesiscompanylAnterior.tasaFinancieraDescuento())
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanylAnterior.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanylAnterior.tasaIncrementoSalarios())
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obd = CalculadoraPorFlujos.obd(obdCalculatorInput);

            var empleadoResponse = EmpleadoResponse.builder()
                    .identificacion(empleadoRequest.identificacion())
                    .ts2(ts2)
                    .sueldo(sueldo)
                    .edad(edad)
                    .obd(obd)
                    .build();
            empleadosResponse.add(empleadoResponse);
        }

        return ResultadoProcesoTasaAlterna.builder()
                .empleados(empleadosResponse)
                .obd(sumAttribute(empleadosResponse, EmpleadoResponse::getObd))
                .build();
    }
}
