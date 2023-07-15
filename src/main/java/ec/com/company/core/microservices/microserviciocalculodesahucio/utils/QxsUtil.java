package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.CentrosDeCostoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.CentroDeCostoVo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.ColaboradoresPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTsPorCentroDeCosto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QxsUtil {

    public static QxsRotacionPorTs getQxsRotacionPorTs_desahucio(
            LocalDate fechaCalculo,
            BigDecimal tasaRotacionPromedio,
            BigDecimal credibilidadRotacion,
            List<EmpleadoRequest> empleados
    ) throws coreException {

        ColaboradoresPorTs colaboradoresPorTs = ColaboradoresPorTs.getColaboradoresPorTsDesahucio(
                fechaCalculo,
                empleados);

        int numeroTrabajadoresConsideradosParaRotacion = ColaboradoresPorTs.getNumeroTrabajadoresConsiderados(colaboradoresPorTs);
        if(numeroTrabajadoresConsideradosParaRotacion > 0 ){
            return TablaActUtil.calcularQxsRotacionEspecificos(
                    colaboradoresPorTs,
                    QxsRotacionPorTs.getQxsRotacionEstandar(),
                    tasaRotacionPromedio,
                    credibilidadRotacion);
        }
        return null;
    }

    public static QxsRotacionPorTsPorCentroDeCosto getQxsRotacionPorTsPorCentroCosto_desahucio(
            LocalDate fechaCalculo,
            BigDecimal tasaRotacionPromedio,
            BigDecimal credibilidadRotacion,
            List<EmpleadoRequest> empleados,
            List<CentrosDeCostoRequest> centrosDeCostos
    ) throws coreException {

        List<CentroDeCostoVo> centrosDeCosto = CentrosCostoUtil.getCentrosDeCosto(
                centrosDeCostos,
                empleados,
                tasaRotacionPromedio);

        Map<String, QxsRotacionPorTs> qxsRotacionPorTsPorCentroDeCostoMap = new HashMap<>();
        for (CentroDeCostoVo centroDeCostoVo : centrosDeCosto) {
            QxsRotacionPorTs qxsRotacionPorTs = getQxsRotacionPorTs_desahucio(
                    fechaCalculo,
                    centroDeCostoVo.getTasaRotacion(),
                    credibilidadRotacion,
                    centroDeCostoVo.getEmpleados()
            );
            if(qxsRotacionPorTs != null){
                qxsRotacionPorTsPorCentroDeCostoMap.put(centroDeCostoVo.getNombre(), qxsRotacionPorTs);
            }
        }
        return new QxsRotacionPorTsPorCentroDeCosto(qxsRotacionPorTsPorCentroDeCostoMap);
    }

}
