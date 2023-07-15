package ec.com.company.core.microservices.microserviciocalculodesahucio.models.mortalidad;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class RP2000 extends QxsMortalidadPorEdad {

    private final Genero GENERO;
    private final Map<Integer, BigDecimal> qxsMortalidadPorEdad;

    private static final int EDAD_INICIO_QX = 15;

    /**
     * Toma dos arreglos paralelos relacionados de edades y qxs de mortalidad.
     *
     * @param edades        arreglo de edades
     * @param qxsMortalidad arreglo de qxs de mortalidad
     */
    public RP2000(Genero GENERO, Integer[] edades, BigDecimal[] qxsMortalidad) throws coreException {

        if (ArrayUtils.isEmpty(edades)) {
            throw new coreException("Error. El arreglo edades pasado al constructor de RP2000" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (ArrayUtils.isEmpty(qxsMortalidad)) {
            throw new coreException("Error. El arreglo qxsMortalidad pasado al constructor de RP2000" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (edades.length != qxsMortalidad.length) {
            throw new coreException("Error en creacion de RP2000. Los arreglos edades " + "[size=" + edades.length + "]" +
                    " y qxsMortalidad " + "[size=" + qxsMortalidad.length + "]" + " deben ser arreglos paralelos" +
                    " por lo que deben tener el mismo tamaño.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        this.qxsMortalidadPorEdad = new HashMap<>();
        for (int i = 0; i < edades.length; i++) {
            this.qxsMortalidadPorEdad.put(edades[i], qxsMortalidad[i]);
        }
        this.GENERO = GENERO;
    }

    private Map<Integer, BigDecimal> getQxsMortalidadPorEdadMap() {
        return this.qxsMortalidadPorEdad;
    }

    @Override
    public BigDecimal getQx(Genero genero, Integer edad) throws coreException {

        if (this.GENERO != genero) {
            throw new coreException("Error. Los qxs del objeto de esta tabla (RP2000) no están especificados"
                    + " para el género " + GENERO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Revisar si el map contiene data para la edad especificada
        if (!getQxsMortalidadPorEdadMap().containsKey(edad)) {
            // Si no contiene. Revisar los límites de edades
            int edadMaxValue = Collections.min(getQxsMortalidadPorEdadMap().entrySet(), Map.Entry.comparingByValue()).getKey();
            int edadMinValue = Collections.max(getQxsMortalidadPorEdadMap().entrySet(), Map.Entry.comparingByValue()).getKey();

            // Advertir al usuario el problema
            throw new coreException("Error. La edad debe estar entre: " + edadMaxValue + " y "
                    + edadMinValue + " para el actual mapa de qxs de mortalidad (RP2000) por edad. "
                    + edad + " no es un valor válido.", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        // Devolver el qxs de mortalidad correspondiente a la edad
        return getQxsMortalidadPorEdadMap().get(edad);
    }

    @Override
    public BigDecimal getQx(String genero, Integer edad) throws coreException {
        Genero generoEnum = Genero.getEnumByCode(genero);
        return getQx(generoEnum, edad);
    }

    public boolean compareTo(QxsMortalidadPorEdad that, int NUMERO_DECIMALES_A_COMPARAR) throws coreException {

        if (!(that instanceof RP2000)) {
            return false;
        }

        if (getQxsMortalidadPorEdadMap().size() != ((RP2000) that).getQxsMortalidadPorEdadMap().size()) {
            return false;
        }

        if (this.GENERO != ((RP2000) that).GENERO) {
            return false;
        }

        for (Map.Entry<Integer, BigDecimal> thisEntry : this.getQxsMortalidadPorEdadMap().entrySet()) {

            int edad = thisEntry.getKey();

            BigDecimal thisQx = this.getQx(this.GENERO, edad);
            BigDecimal thatQx = that.getQx(((RP2000) that).GENERO, edad);

            // Do the comparison
            BigDecimal a = thisQx.setScale(NUMERO_DECIMALES_A_COMPARAR, RoundingMode.HALF_UP);
            BigDecimal b = thatQx.setScale(NUMERO_DECIMALES_A_COMPARAR, RoundingMode.HALF_UP);
            if (a.compareTo(b) != 0) {
                return false;
            }
        }
        return true;
    }

    public static RP2000 getQxsMortalidadPorEdad_Hombres() throws coreException {
        return new RP2000(Genero.MASCULINO,
                // Edades de 15 a 105 (inclusive). Esto debido a que los QXS_MORTALIDAD_HOMBRES están definidos para esad edades.
                IntStream.range(EDAD_INICIO_QX, QXS_MORTALIDAD_HOMBRES.length + EDAD_INICIO_QX).boxed().toArray(Integer[]::new),
                QXS_MORTALIDAD_HOMBRES);
    }

    public static RP2000 getQxsMortalidadPorEdad_Mujeres() throws coreException {
        return new RP2000(Genero.FEMENINO,
                // Edades de 15 a 105 (inclusive). Esto debido a que los QXS_MORTALIDAD_HOMBRES están definidos para esad edades.
                IntStream.range(EDAD_INICIO_QX, QXS_MORTALIDAD_HOMBRES.length + EDAD_INICIO_QX).boxed().toArray(Integer[]::new),
                QXS_MORTALIDAD_MUJERES);
    }


    private static final BigDecimal[] QXS_MORTALIDAD_HOMBRES = {
            BigDecimal.valueOf(0.000269),
            BigDecimal.valueOf(0.000284),
            BigDecimal.valueOf(0.000301),
            BigDecimal.valueOf(0.000316),
            BigDecimal.valueOf(0.000331),
            BigDecimal.valueOf(0.000345),
            BigDecimal.valueOf(0.000357),
            BigDecimal.valueOf(0.000366),
            BigDecimal.valueOf(0.000373),
            BigDecimal.valueOf(0.000376),
            BigDecimal.valueOf(0.000376),
            BigDecimal.valueOf(0.000378),
            BigDecimal.valueOf(0.000382),
            BigDecimal.valueOf(0.000393),
            BigDecimal.valueOf(0.000412),
            BigDecimal.valueOf(0.000444),
            BigDecimal.valueOf(0.000499),
            BigDecimal.valueOf(0.000562),
            BigDecimal.valueOf(0.000631),
            BigDecimal.valueOf(0.000702),
            BigDecimal.valueOf(0.000773),
            BigDecimal.valueOf(0.000841),
            BigDecimal.valueOf(0.000904),
            BigDecimal.valueOf(0.000964),
            BigDecimal.valueOf(0.001021),
            BigDecimal.valueOf(0.001079),
            BigDecimal.valueOf(0.001142),
            BigDecimal.valueOf(0.001215),
            BigDecimal.valueOf(0.001299),
            BigDecimal.valueOf(0.001397),
            BigDecimal.valueOf(0.001508),
            BigDecimal.valueOf(0.001616),
            BigDecimal.valueOf(0.001734),
            BigDecimal.valueOf(0.00186),
            BigDecimal.valueOf(0.001995),
            BigDecimal.valueOf(0.002138),
            BigDecimal.valueOf(0.002449),
            BigDecimal.valueOf(0.002667),
            BigDecimal.valueOf(0.002916),
            BigDecimal.valueOf(0.003196),
            BigDecimal.valueOf(0.003624),
            BigDecimal.valueOf(0.0042),
            BigDecimal.valueOf(0.004693),
            BigDecimal.valueOf(0.005273),
            BigDecimal.valueOf(0.005945),
            BigDecimal.valueOf(0.006747),
            BigDecimal.valueOf(0.007676),
            BigDecimal.valueOf(0.008757),
            BigDecimal.valueOf(0.010012),
            BigDecimal.valueOf(0.01128),
            BigDecimal.valueOf(0.012737),
            BigDecimal.valueOf(0.014409),
            BigDecimal.valueOf(0.016075),
            BigDecimal.valueOf(0.017871),
            BigDecimal.valueOf(0.019802),
            BigDecimal.valueOf(0.022206),
            BigDecimal.valueOf(0.02457),
            BigDecimal.valueOf(0.027281),
            BigDecimal.valueOf(0.030387),
            BigDecimal.valueOf(0.0339),
            BigDecimal.valueOf(0.037834),
            BigDecimal.valueOf(0.042169),
            BigDecimal.valueOf(0.046906),
            BigDecimal.valueOf(0.052123),
            BigDecimal.valueOf(0.057927),
            BigDecimal.valueOf(0.064368),
            BigDecimal.valueOf(0.072041),
            BigDecimal.valueOf(0.080486),
            BigDecimal.valueOf(0.089718),
            BigDecimal.valueOf(0.099779),
            BigDecimal.valueOf(0.110757),
            BigDecimal.valueOf(0.122797),
            BigDecimal.valueOf(0.136043),
            BigDecimal.valueOf(0.15059),
            BigDecimal.valueOf(0.16642),
            BigDecimal.valueOf(0.183408),
            BigDecimal.valueOf(0.199769),
            BigDecimal.valueOf(0.216605),
            BigDecimal.valueOf(0.233662),
            BigDecimal.valueOf(0.250693),
            BigDecimal.valueOf(0.267491),
            BigDecimal.valueOf(0.283905),
            BigDecimal.valueOf(0.299852),
            BigDecimal.valueOf(0.315296),
            BigDecimal.valueOf(0.330207),
            BigDecimal.valueOf(0.344556),
            BigDecimal.valueOf(0.358628),
            BigDecimal.valueOf(0.371685),
            BigDecimal.valueOf(0.38304),
            BigDecimal.valueOf(0.392003),
            BigDecimal.valueOf(0.397886)
    };

    private static final BigDecimal[] QXS_MORTALIDAD_MUJERES = {
            BigDecimal.valueOf(0.00017),
            BigDecimal.valueOf(0.000177),
            BigDecimal.valueOf(0.000184),
            BigDecimal.valueOf(0.000188),
            BigDecimal.valueOf(0.00019),
            BigDecimal.valueOf(0.000191),
            BigDecimal.valueOf(0.000192),
            BigDecimal.valueOf(0.000194),
            BigDecimal.valueOf(0.000197),
            BigDecimal.valueOf(0.000201),
            BigDecimal.valueOf(0.000207),
            BigDecimal.valueOf(0.000214),
            BigDecimal.valueOf(0.000223),
            BigDecimal.valueOf(0.000235),
            BigDecimal.valueOf(0.000248),
            BigDecimal.valueOf(0.000264),
            BigDecimal.valueOf(0.000307),
            BigDecimal.valueOf(0.00035),
            BigDecimal.valueOf(0.000394),
            BigDecimal.valueOf(0.000435),
            BigDecimal.valueOf(0.000475),
            BigDecimal.valueOf(0.000514),
            BigDecimal.valueOf(0.000554),
            BigDecimal.valueOf(0.000598),
            BigDecimal.valueOf(0.000648),
            BigDecimal.valueOf(0.000706),
            BigDecimal.valueOf(0.000774),
            BigDecimal.valueOf(0.000852),
            BigDecimal.valueOf(0.000937),
            BigDecimal.valueOf(0.001029),
            BigDecimal.valueOf(0.001124),
            BigDecimal.valueOf(0.001223),
            BigDecimal.valueOf(0.001326),
            BigDecimal.valueOf(0.001434),
            BigDecimal.valueOf(0.00155),
            BigDecimal.valueOf(0.001676),
            BigDecimal.valueOf(0.001852),
            BigDecimal.valueOf(0.002018),
            BigDecimal.valueOf(0.002207),
            BigDecimal.valueOf(0.002424),
            BigDecimal.valueOf(0.002717),
            BigDecimal.valueOf(0.00309),
            BigDecimal.valueOf(0.003478),
            BigDecimal.valueOf(0.003923),
            BigDecimal.valueOf(0.004441),
            BigDecimal.valueOf(0.005055),
            BigDecimal.valueOf(0.005814),
            BigDecimal.valueOf(0.006657),
            BigDecimal.valueOf(0.007648),
            BigDecimal.valueOf(0.008619),
            BigDecimal.valueOf(0.009706),
            BigDecimal.valueOf(0.010954),
            BigDecimal.valueOf(0.012163),
            BigDecimal.valueOf(0.013445),
            BigDecimal.valueOf(0.01486),
            BigDecimal.valueOf(0.016742),
            BigDecimal.valueOf(0.018579),
            BigDecimal.valueOf(0.020665),
            BigDecimal.valueOf(0.02297),
            BigDecimal.valueOf(0.025458),
            BigDecimal.valueOf(0.028106),
            BigDecimal.valueOf(0.030966),
            BigDecimal.valueOf(0.034105),
            BigDecimal.valueOf(0.037595),
            BigDecimal.valueOf(0.041506),
            BigDecimal.valueOf(0.045879),
            BigDecimal.valueOf(0.05078),
            BigDecimal.valueOf(0.056294),
            BigDecimal.valueOf(0.062506),
            BigDecimal.valueOf(0.069517),
            BigDecimal.valueOf(0.077446),
            BigDecimal.valueOf(0.086376),
            BigDecimal.valueOf(0.096337),
            BigDecimal.valueOf(0.107303),
            BigDecimal.valueOf(0.119154),
            BigDecimal.valueOf(0.131682),
            BigDecimal.valueOf(0.144604),
            BigDecimal.valueOf(0.157618),
            BigDecimal.valueOf(0.170433),
            BigDecimal.valueOf(0.182799),
            BigDecimal.valueOf(0.194509),
            BigDecimal.valueOf(0.205379),
            BigDecimal.valueOf(0.21524),
            BigDecimal.valueOf(0.223947),
            BigDecimal.valueOf(0.231387),
            BigDecimal.valueOf(0.237467),
            BigDecimal.valueOf(0.244834),
            BigDecimal.valueOf(0.254498),
            BigDecimal.valueOf(0.266044),
            BigDecimal.valueOf(0.279055),
            BigDecimal.valueOf(0.293116)
    };
}
