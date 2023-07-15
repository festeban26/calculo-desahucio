package ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Tabla de qx de rotación. Cada qx corresponde a un ts (tiempo de servicio)
 */
public class QxsRotacionPorTs {
    private final Map<Integer, BigDecimal> qxsRotacionMap;

    /**
     * Toma dos arreglos paralelos relacionados de tiempo se servicio y qxs de rotación.
     *
     * @param tiemposServicio arreglo de tiempos de servicio
     * @param qxsRotacion     arreglo de qxs de rotación
     */
    public QxsRotacionPorTs(Integer[] tiemposServicio, BigDecimal[] qxsRotacion) throws coreException {

        if (ArrayUtils.isEmpty(tiemposServicio)) {
            throw new coreException("Error. El arreglo tiemposServicio pasado al constructor de QxsRotacionMap" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (ArrayUtils.isEmpty(qxsRotacion)) {
            throw new coreException("Error. El arreglo qxsRotacion pasado al constructor de QxsRotacionMap" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (tiemposServicio.length != qxsRotacion.length) {
            throw new coreException("Error en creacion de QxsRotacionPorTs. Los arreglos tiemposServicio " + "[size=" + tiemposServicio.length + "]" +
                    " y qxsRotacion " + "[size=" + qxsRotacion.length + "]" + " deben ser arreglos paralelos" +
                    " por lo que deben tener el mismo tamaño.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        this.qxsRotacionMap = new HashMap<>();
        for (int i = 0; i < tiemposServicio.length; i++) {
            this.qxsRotacionMap.put(tiemposServicio[i], qxsRotacion[i]);
        }
    }

    public QxsRotacionPorTs(Map<Integer, BigDecimal> qxsRotacionMap) {
        this.qxsRotacionMap = qxsRotacionMap;
    }

    private Map<Integer, BigDecimal> getQxsRotacionMap() {
        return this.qxsRotacionMap;
    }

    public BigDecimal getQx(Integer tiempoDeServicio) throws coreException {

        // Revisar si el map contiene data para el tiempo de servicio especificado
        if (!getQxsRotacionMap().containsKey(tiempoDeServicio)) {
            // Si no contiene. Revisar los límites de tiempos de servicio
            int tsMaxValue = Collections.min(this.getQxsRotacionMap().entrySet(), Map.Entry.comparingByValue()).getKey();
            int tsMinValue = Collections.max(this.getQxsRotacionMap().entrySet(), Map.Entry.comparingByValue()).getKey();

            // Advertir al usuario el problema
            throw new coreException("Error. El tiempo de servicio (ts) debe estar entre: " + tsMaxValue + " y "
                    + tsMinValue + " para el actual mapa de qxs de rotación. "
                    + tiempoDeServicio + " no es un valor válido.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Devolver el qxs de rotación correspondiente al tiempo de servicio
        return getQxsRotacionMap().get(tiempoDeServicio);
    }

    public boolean compareTo(QxsRotacionPorTs that, int NUMERO_DECIMALES_A_COMPARAR) throws coreException {

        if (getQxsRotacionMap().size() != that.getQxsRotacionMap().size()) {
            return false;
        }

        for (Map.Entry<Integer, BigDecimal> thisEntry : this.getQxsRotacionMap().entrySet()) {

            int ts = thisEntry.getKey();

            BigDecimal thisQxRotacion = this.getQx(ts);
            BigDecimal thatQxRotacion = that.getQx(ts);

            // Do the comparison
            BigDecimal a = thisQxRotacion.setScale(NUMERO_DECIMALES_A_COMPARAR, RoundingMode.HALF_UP);
            BigDecimal b = thatQxRotacion.setScale(NUMERO_DECIMALES_A_COMPARAR, RoundingMode.HALF_UP);
            if (a.compareTo(b) != 0) {
                return false;
            }
        }
        return true;
    }

    public static QxsRotacionPorTs getQxsRotacionEstandar() throws coreException {
        return new QxsRotacionPorTs(IntStream.range(0, QXS_ROTACION_ESTANDAR.length).boxed().toArray(Integer[]::new),
                QXS_ROTACION_ESTANDAR);
    }

    private static final BigDecimal[] QXS_ROTACION_ESTANDAR = {
            BigDecimal.valueOf(0.382992917163262),
            BigDecimal.valueOf(0.279934276490238),
            BigDecimal.valueOf(0.217795186147224),
            BigDecimal.valueOf(0.18177103693465),
            BigDecimal.valueOf(0.149334239586566),
            BigDecimal.valueOf(0.131984887067943),
            BigDecimal.valueOf(0.122160286228323),
            BigDecimal.valueOf(0.113221891246563),
            BigDecimal.valueOf(0.111574338843411),
            BigDecimal.valueOf(0.0951015503447886),
            BigDecimal.valueOf(0.0848977741819984),
            BigDecimal.valueOf(0.0840632656378207),
            BigDecimal.valueOf(0.0837618091123001),
            BigDecimal.valueOf(0.0859347407502864),
            BigDecimal.valueOf(0.0840350283124029),
            BigDecimal.valueOf(0.0756875948160899),
            BigDecimal.valueOf(0.0706921241782626),
            BigDecimal.valueOf(0.0772436469580625),
            BigDecimal.valueOf(0.0907434972945141),
            BigDecimal.valueOf(0.0984292393889653),
            BigDecimal.valueOf(0.135441549186791),
            BigDecimal.valueOf(0.142402346188505),
            BigDecimal.valueOf(0.128537145143278),
            BigDecimal.valueOf(0.126767765388136),
            BigDecimal.valueOf(0.114305832133638),
            BigDecimal.valueOf(0.204130775797038),
            BigDecimal.valueOf(0.460182972606322),
            BigDecimal.valueOf(0.699646757008153),
            BigDecimal.valueOf(1)
    };

}
