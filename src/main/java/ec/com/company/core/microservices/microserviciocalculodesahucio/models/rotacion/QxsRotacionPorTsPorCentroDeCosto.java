package ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class QxsRotacionPorTsPorCentroDeCosto {

    public enum TipoCalculo {
        JUBILACION,
        DESAHUCIO
    }

    private final Map<String, QxsRotacionPorTs> qxsRotacionPorTsPorCentroDeCostoMap;

    public QxsRotacionPorTsPorCentroDeCosto(Map<String, QxsRotacionPorTs> qxsRotacionPorTsPorCentroDeCostoMap) {
        this.qxsRotacionPorTsPorCentroDeCostoMap = qxsRotacionPorTsPorCentroDeCostoMap;
    }

    public QxsRotacionPorTs getQxsRotacionPorTs(String nombreCentroCosto) throws coreException {
        // Revisar si el map contiene data para el centro de costo
        if (!this.qxsRotacionPorTsPorCentroDeCostoMap.containsKey(nombreCentroCosto)) {
            // Si no contiene, advertir al usuario el problema
            throw new coreException("Error. No existe información de qx de rotacion por tiempo de servicio para el" +
                    " centro de costo '" + nombreCentroCosto + "'", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Devolver el qxs de rotación correspondiente al tiempo de servicio
        return this.qxsRotacionPorTsPorCentroDeCostoMap.get(nombreCentroCosto);
    }
}
