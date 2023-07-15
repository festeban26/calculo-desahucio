package ec.com.company.core.microservices.microserviciocalculodesahucio.models;

import ec.com.company.core.microservices.microserviciocalculodesahucio.calculators.Calculator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpleadoEnum;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Tabla de número de colaboradores por ts (tiempo de servicio).
 * Esta clase representa un arreglo paralelo entre ts y el numero de colaboradores por este ts.
 */
public class ColaboradoresPorTs {

    private static final int TS_MAX = 28;

    private final Map<Integer, Integer> colaboradoresPorTs;

    public ColaboradoresPorTs(Integer[] tiemposServicio, Integer[] colaboradoresPorTs) throws coreException {

        if (ArrayUtils.isEmpty(tiemposServicio)) {
            throw new coreException("Error. El arreglo tiemposServicio pasado al constructor de ColaboradoresPorTs" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (ArrayUtils.isEmpty(colaboradoresPorTs)) {
            throw new coreException("Error. El arreglo numeroColaboradoresPorTs pasado al constructor de ColaboradoresPorTs" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (tiemposServicio.length != colaboradoresPorTs.length) {
            throw new coreException("Error en creación de ColaboradoresPorTs. Los arreglos tiemposServicio " + "[size=" + tiemposServicio.length + "]" +
                    " y numeroColaboradoresPorTs " + "[size=" + colaboradoresPorTs.length + "]" + " deben ser arreglos paralelos" +
                    " por lo que deben tener el mismo tamaño.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        this.colaboradoresPorTs = new HashMap<>();
        for (int i = 0; i < tiemposServicio.length; i++) {
            this.colaboradoresPorTs.put(tiemposServicio[i], colaboradoresPorTs[i]);
        }
    }

    public ColaboradoresPorTs(Map<Integer, Integer> colaboradoresPorTs) {
        this.colaboradoresPorTs = colaboradoresPorTs;
    }

    /**
     * Retorna el número de colaboradores para el tiempo de servicio dado.
     *
     * @param tiempoDeServicio
     * @return
     * @throws coreException
     */
    public Integer getNumeroDeTrabajadores(Integer tiempoDeServicio) throws coreException {

        // Revisar si el map contiene data para el tiempo de servicio especificado
        if (!this.colaboradoresPorTs.containsKey(tiempoDeServicio)) {
            // Si no contiene. Revisar los límites de tiempos de servicio
            int tsMaxValue = Collections.min(this.colaboradoresPorTs.entrySet(), Map.Entry.comparingByValue()).getKey();
            int tsMinValue = Collections.max(this.colaboradoresPorTs.entrySet(), Map.Entry.comparingByValue()).getKey();

            // Advertir al usuario el problema
            throw new coreException("Error. El tiempo de servicio (ts) debe estar entre: " + tsMaxValue + " y "
                    + tsMinValue + " para el actual mapa de colaboradores por tiempo de servicio. "
                    + tiempoDeServicio + " no es un valor válido.", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        // Devolver el qxs de rotación correspondiente al tiempo de servicio
        return this.colaboradoresPorTs.get(tiempoDeServicio);
    }

    private Map<Integer, Integer> getColaboradoresPorTs() {
        return this.colaboradoresPorTs;
    }

    /**
     * Compara si dos objetos del tipo ColaboradoresPorTs contienen la misma información.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ColaboradoresPorTs that)) {
            return false;
        }


        if (this.colaboradoresPorTs.size() != that.getColaboradoresPorTs().size()) {
            return false;
        }

        return this.colaboradoresPorTs.entrySet().stream().allMatch(
                e -> e.getValue().equals(that.getColaboradoresPorTs().get(e.getKey()))
        );
    }


    public static ColaboradoresPorTs getColaboradoresPorTs(LocalDate fechaCalculo,
                                                           List<LocalDate> fechasIngreso) {

        Map<Integer, Integer> colaboradoresPorTs = new HashMap<>();

        // Los tiempos de servicio van de 0 a 28 (inclusive)
        int tiemposDeServicioSize = ColaboradoresPorTs.TS_MAX + 1;
        int[] tiemposDeServicio = IntStream.range(0, tiemposDeServicioSize).toArray();
        // Initialize colaboradoresPorTs. Set count to zero.
        for (int ts = 0; ts < tiemposDeServicioSize; ts++) {
            colaboradoresPorTs.put(tiemposDeServicio[ts], 0);
        }

        for (LocalDate fechaIngreso : fechasIngreso) {
            int ts = Calculator.ts(fechaCalculo, fechaIngreso).setScale(0, RoundingMode.FLOOR).intValue();

            // el ultimo escaño tiene los valores mayores a, por ejemplo, 28 por ello si el ts es mayor o igual al
            // número de escaños se agrega este valor al último valor del arreglo de número de colaboradores por ts
            int tsIndex = Math.min(ts, TS_MAX);
            int oldValue = colaboradoresPorTs.get(tsIndex);
            int newValue = oldValue + 1;
            colaboradoresPorTs.replace(tsIndex, oldValue, newValue);
        }
        return new ColaboradoresPorTs(colaboradoresPorTs);
    }

    public static ColaboradoresPorTs getColaboradoresPorTsDesahucio(LocalDate fechaCalculo,
                                                                    List<EmpleadoRequest> personas) {
        List<LocalDate> fechasIngresoDesahucio = new ArrayList<>();
        for (EmpleadoRequest persona : personas) {
            // Considerar dato solo si el tipo está entre los tipos considerados para el cálculo de colaboradores por ts
            if (ColaboradoresPorTs.getTiposDeEmpleadosAConsiderar().contains(persona.tipo())) {
                fechasIngresoDesahucio.add(persona.fechaIngresoDesahucio());
            }
        }
        return ColaboradoresPorTs.getColaboradoresPorTs(fechaCalculo, fechasIngresoDesahucio);
    }

    public static Set<Integer> getTiposDeEmpleadosAConsiderar() {
        // En rotacion solo se toma en cuenta getEmpleados activos
        return new HashSet<>() {
            {
                add(TipoEmpleadoEnum.TS_MAYOR_IGUAL_25.getTipo());
                add(TipoEmpleadoEnum.TS_ENTRE_10_Y_25.getTipo());
                add(TipoEmpleadoEnum.TS_MENOR_A_10.getTipo());
                add(TipoEmpleadoEnum.TRASPASO_ENTRANTE.getTipo());
            }
        };
    }

    /**
     * Suma el numeor de colaboradores de todos los tiempos de servicio que constan en paràmetro 'colaboradoresPorTs'
     *
     * @param colaboradoresPorTs
     * @return
     */
    public static int getNumeroTrabajadoresConsiderados(ColaboradoresPorTs colaboradoresPorTs) {

        int numeroTrabajadoresConsideradosParaRotacion = 0;
        for (Map.Entry<Integer, Integer> colaboradorPorTs : colaboradoresPorTs.getColaboradoresPorTs().entrySet()) {
            int numeroColaboradores = colaboradorPorTs.getValue();
            numeroTrabajadoresConsideradosParaRotacion += numeroColaboradores;
        }
        return numeroTrabajadoresConsideradosParaRotacion;
    }

}
