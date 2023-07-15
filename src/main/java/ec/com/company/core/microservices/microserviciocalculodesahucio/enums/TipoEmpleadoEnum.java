package ec.com.company.core.microservices.microserviciocalculodesahucio.enums;

/**
 *
 * @author esteban.flores
 */
public enum TipoEmpleadoEnum {

    /**
     * Activos: 0 - 4
     * Cesantes y salidas: 5 - 7
     */

    TRASPASO_ENTRANTE(-1),
    JUBILADO_PENDIENTE_PAGO(0),
    JUBILADO_PENSIONISTA(1),
    TS_MAYOR_IGUAL_25(2),
    TS_ENTRE_10_Y_25(3),
    TS_MENOR_A_10(4),
    SALIDA_SIN_PAGO(5),
    FALLECIDO(6),
    PAGO(7),
    TRASPASO_SALIENTE(11);

    private final int tipo;

    TipoEmpleadoEnum(final int tipo) {
        this.tipo = tipo;
    }

    public int getTipo() {
        return this.tipo;
    }

    @Override
    public String toString() {
        return String.valueOf(this.tipo);
    }

}
