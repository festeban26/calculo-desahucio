package ec.com.company.core.microservices.microserviciocalculodesahucio.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serializer personalizado para la clase BigDecimal que permite serializar valores con escala ajustada si es necesario.
 */
public class ScaledBigDecimalSerializer extends JsonSerializer<BigDecimal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScaledBigDecimalSerializer.class);
    private static final int MAX_SCALE = AppConstants.RESPONSE_BIGDECIMALS_MAX_SCALE;
    private static final RoundingMode ROUNDING_MODE = AppConstants.JSONS_NUMERIC_ROUNDING_MODE;

    /**
     * Serializa un valor BigDecimal, ajustando la escala si es necesario.
     *
     * @param value    El valor BigDecimal a serializar.
     * @param jgen     El JsonGenerator para escribir el valor serializado.
     * @param provider El SerializerProvider.
     * @throws IOException si ocurre un error de E/S durante la serialización.
     */
    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        LOGGER.debug("Serializing BigDecimal value: {}", value);

        // Ajusta la escala si es necesario
        BigDecimal scaleBd = value;
        if (value.scale() > MAX_SCALE) {
            LOGGER.debug("BigDecimal value exceeds scale limit. Scaling value from {} to {} with rounding mode: {}",
                    value.scale(), MAX_SCALE, ROUNDING_MODE);
            scaleBd = value.setScale(MAX_SCALE, ROUNDING_MODE);
        }

        jgen.writeNumber(scaleBd.stripTrailingZeros());
        LOGGER.trace("Serialized BigDecimal value: {}", scaleBd);
    }
}