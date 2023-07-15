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

public class TMIESS2002 extends QxsMortalidadPorEdad {

    private final Genero GENERO;
    private final Map<Integer, BigDecimal> qxsMortalidadPorEdad;

    private static final int EDAD_INICIO_QX = 15;

    /**
     * Toma dos arreglos paralelos relacionados de edades y qxs de mortalidad.
     *
     * @param edades        arreglo de edades
     * @param qxsMortalidad arreglo de qxs de mortalidad
     */
    public TMIESS2002(Genero GENERO, Integer[] edades, BigDecimal[] qxsMortalidad) throws coreException {

        if (ArrayUtils.isEmpty(edades)) {
            throw new coreException("Error. El arreglo edades pasado al constructor de TMIESS2022" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (ArrayUtils.isEmpty(qxsMortalidad)) {
            throw new coreException("Error. El arreglo qxsMortalidad pasado al constructor de TMIESS2022" +
                    " está vacío o es null.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (edades.length != qxsMortalidad.length) {
            throw new coreException("Error en creacion de TMIESS2022. Los arreglos edades " + "[size=" + edades.length + "]" +
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
            throw new coreException("Error. Los qxs del objeto de esta tabla (TMIESS2022) no están especificados"
                    + " para el género " + GENERO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Revisar si el map contiene data para la edad especificada
        if (!getQxsMortalidadPorEdadMap().containsKey(edad)) {
            // Si no contiene. Revisar los límites de edades
            int edadMaxValue = Collections.min(getQxsMortalidadPorEdadMap().entrySet(), Map.Entry.comparingByValue()).getKey();
            int edadMinValue = Collections.max(getQxsMortalidadPorEdadMap().entrySet(), Map.Entry.comparingByValue()).getKey();

            // Advertir al usuario el problema
            throw new coreException("Error. La edad debe estar entre: " + edadMaxValue + " y "
                    + edadMinValue + " para el actual mapa de qxs de mortalidad (TMIESS2022) por edad. "
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

        if (!(that instanceof TMIESS2002)) {
            return false;
        }

        if (getQxsMortalidadPorEdadMap().size() != ((TMIESS2002) that).getQxsMortalidadPorEdadMap().size()) {
            return false;
        }

        if (this.GENERO != ((TMIESS2002) that).GENERO) {
            return false;
        }

        for (Map.Entry<Integer, BigDecimal> thisEntry : this.getQxsMortalidadPorEdadMap().entrySet()) {

            int edad = thisEntry.getKey();

            BigDecimal thisQx = this.getQx(this.GENERO, edad);
            BigDecimal thatQx = that.getQx(((TMIESS2002) that).GENERO, edad);

            // Do the comparison
            BigDecimal a = thisQx.setScale(NUMERO_DECIMALES_A_COMPARAR, RoundingMode.HALF_UP);
            BigDecimal b = thatQx.setScale(NUMERO_DECIMALES_A_COMPARAR, RoundingMode.HALF_UP);
            if (a.compareTo(b) != 0) {
                return false;
            }
        }
        return true;
    }

    public static TMIESS2002 getQxsMortalidadPorEdad_Hombres() throws coreException {
        return new TMIESS2002(Genero.MASCULINO,
                // Edades de 15 a 105 (inclusive). Esto debido a que los QXS_MORTALIDAD_HOMBRES están definidos para esad edades.
                IntStream.range(EDAD_INICIO_QX, QXS_MORTALIDAD_HOMBRES.length + EDAD_INICIO_QX).boxed().toArray(Integer[]::new),
                QXS_MORTALIDAD_HOMBRES);
    }

    public static TMIESS2002 getQxsMortalidadPorEdad_Mujeres() throws coreException {
        return new TMIESS2002(Genero.FEMENINO,
                // Edades de 15 a 105 (inclusive). Esto debido a que los QXS_MORTALIDAD_HOMBRES están definidos para esad edades.
                IntStream.range(EDAD_INICIO_QX, QXS_MORTALIDAD_HOMBRES.length + EDAD_INICIO_QX).boxed().toArray(Integer[]::new),
                QXS_MORTALIDAD_MUJERES);
    }


    private static final BigDecimal[] QXS_MORTALIDAD_HOMBRES = {
            BigDecimal.valueOf(0.002799053),
            BigDecimal.valueOf(0.002820308),
            BigDecimal.valueOf(0.002843842),
            BigDecimal.valueOf(0.002869899),
            BigDecimal.valueOf(0.002898749),
            BigDecimal.valueOf(0.002930692),
            BigDecimal.valueOf(0.002966059),
            BigDecimal.valueOf(0.003005217),
            BigDecimal.valueOf(0.003048572),
            BigDecimal.valueOf(0.003096574),
            BigDecimal.valueOf(0.003149721),
            BigDecimal.valueOf(0.003208563),
            BigDecimal.valueOf(0.00327371),
            BigDecimal.valueOf(0.003345839),
            BigDecimal.valueOf(0.003425695),
            BigDecimal.valueOf(0.003514108),
            BigDecimal.valueOf(0.003611991),
            BigDecimal.valueOf(0.00372036),
            BigDecimal.valueOf(0.003840335),
            BigDecimal.valueOf(0.003973158),
            BigDecimal.valueOf(0.004120204),
            BigDecimal.valueOf(0.004282991),
            BigDecimal.valueOf(0.004463204),
            BigDecimal.valueOf(0.004662702),
            BigDecimal.valueOf(0.004883547),
            BigDecimal.valueOf(0.005128015),
            BigDecimal.valueOf(0.005398627),
            BigDecimal.valueOf(0.005698172),
            BigDecimal.valueOf(0.006029732),
            BigDecimal.valueOf(0.006396716),
            BigDecimal.valueOf(0.006802893),
            BigDecimal.valueOf(0.007252432),
            BigDecimal.valueOf(0.007749937),
            BigDecimal.valueOf(0.008300499),
            BigDecimal.valueOf(0.008909742),
            BigDecimal.valueOf(0.009583878),
            BigDecimal.valueOf(0.010329756),
            BigDecimal.valueOf(0.011154981),
            BigDecimal.valueOf(0.012067881),
            BigDecimal.valueOf(0.013077688),
            BigDecimal.valueOf(0.014194573),
            BigDecimal.valueOf(0.015429748),
            BigDecimal.valueOf(0.016795566),
            BigDecimal.valueOf(0.018305631),
            BigDecimal.valueOf(0.019974914),
            BigDecimal.valueOf(0.021819885),
            BigDecimal.valueOf(0.02385864),
            BigDecimal.valueOf(0.026111055),
            BigDecimal.valueOf(0.028598935),
            BigDecimal.valueOf(0.031346177),
            BigDecimal.valueOf(0.034378944),
            BigDecimal.valueOf(0.037725839),
            BigDecimal.valueOf(0.041418091),
            BigDecimal.valueOf(0.04548974),
            BigDecimal.valueOf(0.04997782),
            BigDecimal.valueOf(0.054922544),
            BigDecimal.valueOf(0.060367474),
            BigDecimal.valueOf(0.066359677),
            BigDecimal.valueOf(0.072949856),
            BigDecimal.valueOf(0.080192449),
            BigDecimal.valueOf(0.088145676),
            BigDecimal.valueOf(0.096871531),
            BigDecimal.valueOf(0.106435683),
            BigDecimal.valueOf(0.116907289),
            BigDecimal.valueOf(0.128358669),
            BigDecimal.valueOf(0.140864833),
            BigDecimal.valueOf(0.154502814),
            BigDecimal.valueOf(0.169350788),
            BigDecimal.valueOf(0.185486918),
            BigDecimal.valueOf(0.202987905),
            BigDecimal.valueOf(0.221927188),
            BigDecimal.valueOf(0.242372757),
            BigDecimal.valueOf(0.264384548),
            BigDecimal.valueOf(0.288011387),
            BigDecimal.valueOf(0.313287484),
            BigDecimal.valueOf(0.340228475),
            BigDecimal.valueOf(0.368827076),
            BigDecimal.valueOf(0.399048418),
            BigDecimal.valueOf(0.430825221),
            BigDecimal.valueOf(0.464053007),
            BigDecimal.valueOf(0.498585643),
            BigDecimal.valueOf(0.534231572),
            BigDecimal.valueOf(0.570751196),
            BigDecimal.valueOf(0.607855931),
            BigDecimal.valueOf(0.645209504),
            BigDecimal.valueOf(0.682432091),
            BigDecimal.valueOf(0.719107806),
            BigDecimal.valueOf(0.754795925),
            BigDecimal.valueOf(0.789045972),
            BigDecimal.valueOf(0.821416411),
            BigDecimal.valueOf(1)
    };

    private static final BigDecimal[] QXS_MORTALIDAD_MUJERES = {
            BigDecimal.valueOf(0.000778747),
            BigDecimal.valueOf(0.000791474),
            BigDecimal.valueOf(0.000805606),
            BigDecimal.valueOf(0.000821297),
            BigDecimal.valueOf(0.00083872),
            BigDecimal.valueOf(0.000858065),
            BigDecimal.valueOf(0.000879545),
            BigDecimal.valueOf(0.000903395),
            BigDecimal.valueOf(0.000929878),
            BigDecimal.valueOf(0.000959282),
            BigDecimal.valueOf(0.00099193),
            BigDecimal.valueOf(0.001028181),
            BigDecimal.valueOf(0.001068431),
            BigDecimal.valueOf(0.001113122),
            BigDecimal.valueOf(0.001162744),
            BigDecimal.valueOf(0.001217839),
            BigDecimal.valueOf(0.001279011),
            BigDecimal.valueOf(0.001346932),
            BigDecimal.valueOf(0.001422343),
            BigDecimal.valueOf(0.001506071),
            BigDecimal.valueOf(0.001599031),
            BigDecimal.valueOf(0.001702242),
            BigDecimal.valueOf(0.001816833),
            BigDecimal.valueOf(0.001944055),
            BigDecimal.valueOf(0.0020853),
            BigDecimal.valueOf(0.002242112),
            BigDecimal.valueOf(0.002416202),
            BigDecimal.valueOf(0.002609472),
            BigDecimal.valueOf(0.002824029),
            BigDecimal.valueOf(0.003062213),
            BigDecimal.valueOf(0.00332662),
            BigDecimal.valueOf(0.003620128),
            BigDecimal.valueOf(0.00394593),
            BigDecimal.valueOf(0.004307566),
            BigDecimal.valueOf(0.004708964),
            BigDecimal.valueOf(0.005154475),
            BigDecimal.valueOf(0.005648925),
            BigDecimal.valueOf(0.00619766),
            BigDecimal.valueOf(0.006806606),
            BigDecimal.valueOf(0.007482326),
            BigDecimal.valueOf(0.008232088),
            BigDecimal.valueOf(0.00906394),
            BigDecimal.valueOf(0.009986787),
            BigDecimal.valueOf(0.011010485),
            BigDecimal.valueOf(0.012145931),
            BigDecimal.valueOf(0.013405172),
            BigDecimal.valueOf(0.014801518),
            BigDecimal.valueOf(0.016349667),
            BigDecimal.valueOf(0.018065838),
            BigDecimal.valueOf(0.01996792),
            BigDecimal.valueOf(0.022075623),
            BigDecimal.valueOf(0.024410648),
            BigDecimal.valueOf(0.026996864),
            BigDecimal.valueOf(0.029860496),
            BigDecimal.valueOf(0.03303032),
            BigDecimal.valueOf(0.036537872),
            BigDecimal.valueOf(0.040417657),
            BigDecimal.valueOf(0.044707361),
            BigDecimal.valueOf(0.049448065),
            BigDecimal.valueOf(0.054684455),
            BigDecimal.valueOf(0.060465007),
            BigDecimal.valueOf(0.066842168),
            BigDecimal.valueOf(0.07387249),
            BigDecimal.valueOf(0.081616726),
            BigDecimal.valueOf(0.090139865),
            BigDecimal.valueOf(0.099511078),
            BigDecimal.valueOf(0.10980352),
            BigDecimal.valueOf(0.121094298),
            BigDecimal.valueOf(0.133463502),
            BigDecimal.valueOf(0.146994076),
            BigDecimal.valueOf(0.161770658),
            BigDecimal.valueOf(0.177878449),
            BigDecimal.valueOf(0.195401689),
            BigDecimal.valueOf(0.214421746),
            BigDecimal.valueOf(0.235014761),
            BigDecimal.valueOf(0.25724881),
            BigDecimal.valueOf(0.281180553),
            BigDecimal.valueOf(0.306851335),
            BigDecimal.valueOf(0.334282761),
            BigDecimal.valueOf(0.363471788),
            BigDecimal.valueOf(0.394385423),
            BigDecimal.valueOf(0.426955193),
            BigDecimal.valueOf(0.46107163),
            BigDecimal.valueOf(0.49657909),
            BigDecimal.valueOf(0.533271362),
            BigDecimal.valueOf(0.570888596),
            BigDecimal.valueOf(0.60911619),
            BigDecimal.valueOf(0.647586327),
            BigDecimal.valueOf(0.685882876),
            BigDecimal.valueOf(0.723550292),
            BigDecimal.valueOf(1),
    };
}
