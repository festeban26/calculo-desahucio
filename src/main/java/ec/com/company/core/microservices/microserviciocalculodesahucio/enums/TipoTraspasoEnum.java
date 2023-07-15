package ec.com.company.core.microservices.microserviciocalculodesahucio.enums;

public enum TipoTraspasoEnum {

    // Ningún traspaso
    NINGUNO("N"),
    // Traspasos con reserva acumulada + gasto a la fecha
    RESERVA_GASTO("G"),
    // Traspasos con reserva acumulada
    RESERVA_ACUMULADA("R");

    private final String label;

    TipoTraspasoEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public static TipoTraspasoEnum getEnum(String tipoTraspaso) {
        for (TipoTraspasoEnum tipoTraspasoEnum : values()) {
            if (tipoTraspasoEnum.getLabel().equalsIgnoreCase(tipoTraspaso)) {
                return tipoTraspasoEnum;
            }
        }
        throw new IllegalArgumentException(String.valueOf(tipoTraspaso));
    }
}
