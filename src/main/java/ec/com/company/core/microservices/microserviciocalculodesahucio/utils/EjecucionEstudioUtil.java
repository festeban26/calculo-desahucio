package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoCalculo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpresaEnum;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;

public class EjecucionEstudioUtil {
    public static ResultadoCalculo ejecutar(EstudioRequest estudio)
            throws coreException {

        // TODO if getEmpleados activos > 1
        int activeEmployees = 1;

        ResultadoCalculo resultadoCalculo = new ResultadoCalculo();

        var resultadoActual = EjecutarCalculoUtil.performProcesoActual(estudio);
        resultadoCalculo.setActual(resultadoActual);

        var resultadoSalidas = EjecutarCalculoUtil.performProcesoSalidas(estudio);
        resultadoCalculo.setSalidas(resultadoSalidas);

        TipoEmpresaEnum tipoEmpresa = TipoEmpresaEnum.getValueOf(estudio.empresa().estado());
        switch (tipoEmpresa) {
            case ANTIGUA -> {
                var resultadoTasaAlterna = EjecutarCalculoUtil.performProcesoTasaAlterna(estudio);
                resultadoCalculo.setTasaAlterna(resultadoTasaAlterna);
            }
            case NUEVA, INICIA_OPERACIONES -> {
                var resultadoEstimacion = EjecutarCalculoUtil.performProcesoEstimacion(
                        estudio, resultadoActual);
                resultadoCalculo.setEstimacion(resultadoEstimacion);
            }
        }

        var resultadoCda = EjecutarCalculoUtil.performProcesoCda(estudio, resultadoActual, resultadoCalculo.getEstimacion());
        resultadoCalculo.setComposicionDemograficaAnterior(resultadoCda);

        var resultadoProcesoGlobal = EjecutarCalculoUtil.performProcesoAplicacion(estudio, resultadoCalculo);
        resultadoCalculo.setResultadoAplicacion(resultadoProcesoGlobal);

        return resultadoCalculo;
    }
}
