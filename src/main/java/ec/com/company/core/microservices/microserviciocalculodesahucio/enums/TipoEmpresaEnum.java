package ec.com.company.core.microservices.microserviciocalculodesahucio.enums;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;


/**
 * Enumeración que representa los diferentes tipos de empresas.
 */
public enum TipoEmpresaEnum {
    ANTIGUA("A"),   // Representa una empresa antigua
    NUEVA("N"),     // Representa una empresa nueva
    INICIA_OPERACIONES("I");    // Representa una empresa que inicia operaciones

    private static final Logger LOGGER = LoggerFactory.getLogger(TipoEmpresaEnum.class);
    private static final Map<String, TipoEmpresaEnum> CODE_MAP = new HashMap<>();

    static {
        // Se inicializa el mapa de códigos con los valores de los enumeradores
        for (TipoEmpresaEnum tipoEmpresaEnum : values()) {
            CODE_MAP.put(tipoEmpresaEnum.getCodigo().toUpperCase(), tipoEmpresaEnum);
        }
    }

    private final String codigo;

    /**
     * Constructor de la enumeración TipoEmpresaEnum.
     *
     * @param codigo El código asociado al tipo de empresa.
     */
    TipoEmpresaEnum(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el código asociado al tipo de empresa.
     *
     * @return El código asociado al tipo de empresa.
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Obtiene el valor TipoEmpresaEnum correspondiente al tipo de empresa proporcionado.
     *
     * @param tipoEmpresa El tipo de empresa.
     * @return El valor TipoEmpresaEnum correspondiente.
     * @throws coreException Si se proporciona un tipo de empresa inválido, se lanza esta excepción.
     */
    public static TipoEmpresaEnum getValueOf(String tipoEmpresa)
            throws coreException {
        LOGGER.debug("Attempting to get TipoEmpresaEnum for tipoEmpresa: {}", tipoEmpresa);
        TipoEmpresaEnum tipoEmpresaEnum = CODE_MAP.get(tipoEmpresa.toUpperCase());
        if (tipoEmpresaEnum == null) {
            LOGGER.error("Invalid tipoEmpresa value provided: {}", tipoEmpresa);
            // Cuando se envía un tipo de empresa inválido, se debe retornar un error 400
            throw new coreException("Invalid tipoEmpresa value: " + tipoEmpresa, HttpStatus.BAD_REQUEST);
        }
        LOGGER.debug("Retrieved TipoEmpresaEnum: {}", tipoEmpresaEnum);
        return tipoEmpresaEnum;
    }
}