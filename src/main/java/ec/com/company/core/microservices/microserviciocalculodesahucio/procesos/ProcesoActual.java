package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.CalculadoraPorFlujos;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.Calculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.SumAttributesInterface;
import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.ObdCalculatorInput;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoProcesoActual;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTsPorCentroDeCosto;
import ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint.ProcesoCalculo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.utils.TablaMortalidadUtil;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ProcesoActual extends ProcesoCalculo implements SumAttributesInterface{
    private final QxsRotacionPorTs qxsRotacionPorTs;
    private final QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto;

    @Builder
    public ProcesoActual(@NonNull EstudioRequest estudio,
                         QxsRotacionPorTs qxsRotacionPorTs,
                         QxsRotacionPorTsPorCentroDeCosto qxsRotacionPorTsPorCentroDeCosto) {
        super(estudio);
        this.qxsRotacionPorTs = qxsRotacionPorTs;
        this.qxsRotacionPorTsPorCentroDeCosto = qxsRotacionPorTsPorCentroDeCosto;
    }

    @Override
    public ResultadoProcesoActual perform() throws coreException {

        if (getEstudio().empleados().size() == 0) {
            // TODO return empty result and warn about it
            return null;
        }

        final var fechaCalculo = getEstudio().parametros().getFechaValoracion();
        final var fechaProyectada = getEstudio().parametros().getFechaProyectada();
        final var hipotesiscompanyl = getEstudio().hipotesiscompanyl();

        final var empleadosResponse = new ArrayList<EmpleadoResponse>();
        // Para cada empleado
        for (var empleadoRequest : getEstudio().empleados()) {

            final var ts = Calculator.ts(fechaCalculo, empleadoRequest.fechaIngresoJubilacion());
            final var ts2 = Calculator.ts(fechaCalculo, empleadoRequest.fechaIngresoDesahucio());
            final var ts2Proyectado = Calculator.ts(fechaProyectada, empleadoRequest.fechaIngresoDesahucio());
            final var tf = Calculator.tf(ts);
            final var tipoCalculado = Calculator.tipoCalculado(empleadoRequest.tipo(), ts);
            final var sueldo = Calculator.sueldo(empleadoRequest.tipo(), empleadoRequest.remuneracionPromedioDesahucio());
            final var sueldoProyectado = Calculator.sueldoProyectado(empleadoRequest.tipo(), empleadoRequest.remuneracionPromedioDesahucio(),
                    hipotesiscompanyl.porcentajeIncrementoSalarialEstimadoProporcional());
            final var edad = Calculator.edad(fechaCalculo, empleadoRequest.fechaNacimiento());
            final int edadProyectada = Calculator.edad(fechaProyectada, empleadoRequest.fechaNacimiento());
            final var tw = Calculator.tw(tipoCalculado, edad, tf);
            final var valorNominal = Calculator.valorNominal(empleadoRequest.tipo(),
                    empleadoRequest.remuneracionPromedioDesahucio(), ts2);

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
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento())
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios())
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obd = CalculadoraPorFlujos.obd(obdCalculatorInput);

            var obdProyectadoCalculatorInput = ObdCalculatorInput.builder()
                    .nombrePersona(empleadoRequest.nombreCompleto())
                    .tipoPersona(tipoCalculado)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldoProyectado)
                    .tsPersona(ts2Proyectado)
                    .edadPersona(edadProyectada)
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento())
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios())
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obdProyectado = CalculadoraPorFlujos.obd(obdProyectadoCalculatorInput);

            final var interesProporcional = Calculator.interesProporcional
                    (fechaCalculo, fechaProyectada, getEstudio().hipotesiscompanyl().tasaFinancieraDescuento());

            final var interesNeto = Calculator.interesNeto(obd, interesProporcional);

            final var costoLaboral = Calculator.costoLaboral(tipoCalculado, obd, obdProyectado, interesNeto);

            // ============================================== Sensibilidad =============================================
            final var obdConVariacionTasaDsctoMasInput = ObdCalculatorInput.builder()
                    .nombrePersona(empleadoRequest.nombreCompleto())
                    .tipoPersona(tipoCalculado)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldo)
                    .tsPersona(ts2)
                    .edadPersona(edad)
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento().add(hipotesiscompanyl.porcentajeVariacionSensibilidad()))
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios())
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obdConVariacionTasaDsctoMas = CalculadoraPorFlujos.obd(obdConVariacionTasaDsctoMasInput);

            final var obdConVariacionTasaDsctoMenosInput = ObdCalculatorInput.builder()
                    .nombrePersona(empleadoRequest.nombreCompleto())
                    .tipoPersona(tipoCalculado)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldo)
                    .tsPersona(ts2)
                    .edadPersona(edad)
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento().subtract(hipotesiscompanyl.porcentajeVariacionSensibilidad()))
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios())
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obdConVariacionTasaDsctoMenos = CalculadoraPorFlujos.obd(obdConVariacionTasaDsctoMenosInput);

            final var obdConVariacionIncSalarialMasInput = ObdCalculatorInput.builder()
                    .nombrePersona(empleadoRequest.nombreCompleto())
                    .tipoPersona(tipoCalculado)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldo)
                    .tsPersona(ts2)
                    .edadPersona(edad)
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento())
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios().add(hipotesiscompanyl.porcentajeVariacionSensibilidad()))
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obdConVariacionIncSalarialMas = CalculadoraPorFlujos.obd(obdConVariacionIncSalarialMasInput);

            final var obdConVariacionIncSalarialMenosInput = ObdCalculatorInput.builder()
                    .nombrePersona(empleadoRequest.nombreCompleto())
                    .tipoPersona(tipoCalculado)
                    .genero(empleadoRequest.sexo())
                    .sueldoPersona(sueldo)
                    .tsPersona(ts2)
                    .edadPersona(edad)
                    .tasaDescuento(hipotesiscompanyl.tasaFinancieraDescuento())
                    .tasaIncrementoSalarial_cortoPlazo(hipotesiscompanyl.porcentajeIncrementoSalarialEstimado())
                    .tasaIncrementoSalarial_largoPlazo(hipotesiscompanyl.tasaIncrementoSalarios().subtract(hipotesiscompanyl.porcentajeVariacionSensibilidad()))
                    .qxsRotacionPorTs(qxsRotacionPorTs)
                    .tablaMortalidad(tablaMortalidad)
                    .tFact(getEstudio().parametros().getTFactCalculoPorFlujos())
                    .build();
            final BigDecimal obdConVariacionIncSalarialMenos = CalculadoraPorFlujos.obd(obdConVariacionIncSalarialMenosInput);


            // TODO EMFA sensibilidad rotacion. Ver normal + cc
            var empleadoResponse = EmpleadoResponse.builder()
                    .identificacion(empleadoRequest.identificacion())
                    .nombreCompleto(empleadoRequest.nombreCompleto())
                    .sexo(empleadoRequest.sexo())
                    .remuneracionPromedio(empleadoRequest.remuneracionPromedioDesahucio())
                    .ts(ts)
                    .ts2(ts2)
                    .ts2Proyectado(ts2Proyectado)
                    .sueldo(sueldo)
                    .sueldoProyectado(sueldoProyectado)
                    .edad(edad)
                    .edadProyectada(edadProyectada)
                    .tf(tf)
                    .tipoCalculado(tipoCalculado)
                    .tw(tw)
                    .valorNominal(valorNominal)
                    .obd(obd)
                    .obdProyectado(obdProyectado)
                    .interesNeto(interesNeto)
                    .costoLaboral(costoLaboral)
                    .obdConVariacionTasaDsctoMas(obdConVariacionTasaDsctoMas)
                    .obdConVariacionTasaDsctoMenos(obdConVariacionTasaDsctoMenos)
                    .obdConVariacionIncSalarialMas(obdConVariacionIncSalarialMas)
                    .obdConVariacionIncSalarialMenos(obdConVariacionIncSalarialMenos)
                    .build();
            empleadosResponse.add(empleadoResponse);
        }

        // Calculos globales
        final var cuentaActivosSuma = Calculator.cuentaActivos_suma(getEstudio().empleados());
        final var tfPromedio = Calculator.tf_promedio(empleadosResponse);

        return ResultadoProcesoActual.builder()
                .empleados(empleadosResponse)
                .cuentaActivos(cuentaActivosSuma)
                .obd(sumAttribute(empleadosResponse, EmpleadoResponse::getObd))
                .interesNeto(sumAttribute(empleadosResponse, EmpleadoResponse::getInteresNeto))
                .costoLaboral(sumAttribute(empleadosResponse, EmpleadoResponse::getCostoLaboral))
                .valorNominal(sumAttribute(empleadosResponse, EmpleadoResponse::getValorNominal))
                .tfPromedio(tfPromedio)
                .obdConVariacionTasaDsctoMas(sumAttribute(empleadosResponse, EmpleadoResponse::getObdConVariacionTasaDsctoMas))
                .obdConVariacionTasaDsctoMenos(sumAttribute(empleadosResponse, EmpleadoResponse::getObdConVariacionTasaDsctoMenos))
                .obdConVariacionIncSalarialMas(sumAttribute(empleadosResponse, EmpleadoResponse::getObdConVariacionIncSalarialMas))
                .obdConVariacionIncSalarialMenos(sumAttribute(empleadosResponse, EmpleadoResponse::getObdConVariacionIncSalarialMenos))
                .build();
    }
}
