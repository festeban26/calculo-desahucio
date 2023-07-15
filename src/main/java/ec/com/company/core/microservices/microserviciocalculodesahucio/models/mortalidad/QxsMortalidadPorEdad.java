package ec.com.company.core.microservices.microserviciocalculodesahucio.models.mortalidad;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public abstract class QxsMortalidadPorEdad {

    public enum Genero {
        MASCULINO("M"),
        FEMENINO("F");

        private final String code;

        Genero(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Genero getEnumByCode(String code) {
            for (Genero e : Genero.values()) {
                if (e.code.equals(code)) return e;
            }
            return null;
        }
    }

    public enum VersionTablaMortalidadEnum {
        TMIESS2002,
        RP2000;

        public static VersionTablaMortalidadEnum fromString(String text) throws coreException {
            for (VersionTablaMortalidadEnum enumValue : VersionTablaMortalidadEnum.values()) {
                if (enumValue.name().equalsIgnoreCase(text)) {
                    return enumValue;
                }
            }
            throw new coreException("Error. No existe el valor '" + text + "' en VersionTablaMortalidadEnum",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public abstract BigDecimal getQx(Genero genero, Integer edad) throws coreException;

    public abstract BigDecimal getQx(String genero, Integer edad) throws coreException;

    public abstract boolean compareTo(QxsMortalidadPorEdad that, int NUMERO_DECIMALES_A_COMPARAR) throws coreException;


}
