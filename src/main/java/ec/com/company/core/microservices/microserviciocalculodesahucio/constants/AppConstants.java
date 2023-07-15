package ec.com.company.core.microservices.microserviciocalculodesahucio.constants;

import ec.com.company.core.microservices.microserviciocalculodesahucio.models.mortalidad.QxsMortalidadPorEdad;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

public abstract class AppConstants {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final MathContext CALCULO_BIGDECIMALS_MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);

    public static final String TEXTO_CENTRO_DE_COSTO_NINGUNO = "Ninguno";

    public static final int JSONS_MAX_NUMERIC_SCALE = 9;
    public static final int RESPONSE_BIGDECIMALS_MAX_SCALE = 5;
    public static final RoundingMode JSONS_NUMERIC_ROUNDING_MODE = RoundingMode.HALF_UP;


    // Default values
    public static final String DEFAULT_VALUE_VERSION_TABLA_MORTALIDAD_DESAHUCIO = QxsMortalidadPorEdad.VersionTablaMortalidadEnum.TMIESS2002.name();
}
