package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A comparator class for ResultadoCalculo.
 */
public class ResultadoCalculoComparator implements Comparator<ResultadoCalculo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultadoCalculoComparator.class);
    private static final String START_COMPARISON = "Starting comparison for {} ";
    private static final String NOT_MATCH = "{} does not match for both ResultadoCalculo objects";
    private static final String MATCHES = "{} matches for both ResultadoCalculo objects";

    private final List<ComparatorInfo> comparatorInfoList;

    public ResultadoCalculoComparator() {
        this.comparatorInfoList = createComparatorInfoList();
    }

    @Override
    public int compare(ResultadoCalculo firstCalcResult, ResultadoCalculo secondCalcResult) {
        for (ComparatorInfo comparatorInfo : comparatorInfoList) {
            int comparisonResult = compareWithLogging(comparatorInfo.comparator, firstCalcResult, secondCalcResult, comparatorInfo.logMessage);

            if (comparisonResult != 0) {
                LOGGER.info(NOT_MATCH, comparatorInfo.logMessage);
                return comparisonResult;
            }
            LOGGER.info(MATCHES, comparatorInfo.logMessage);
        }
        return 0;
    }

    private List<ComparatorInfo> createComparatorInfoList() {
        return Arrays.asList(
                new ComparatorInfo(createProcesoActualComparator(), "ResultadoProcesoActual"),
                new ComparatorInfo(createProcesoEstimacionComparator(), "ResultadoProcesoEstimacion"),
                new ComparatorInfo(createProcesoTasaAlternaComparator(), "ResultadoProcesoTasaAlterna"),
                new ComparatorInfo(createProcesoCdaComparator(), "ResultadoProcesoCda"),
                new ComparatorInfo(createProcesoSalidasComparator(), "ResultadoProcesoSalidas"),
                new ComparatorInfo(createProcesoGlobalComparator(), "ResultadoProcesoGlobal")
        );
    }

    private Comparator<ResultadoCalculo> createProcesoActualComparator() {
        return Comparator.comparing(ResultadoCalculo::getActual, new ResultadoProcesoActualComparator());
    }

    private Comparator<ResultadoCalculo> createProcesoEstimacionComparator() {
        // nullsFirst is used becase ResultadoCalculo.resultadoEstimado() might be null
        Comparator<ResultadoProcesoEstimacion> procesoEstimacionComparator = Comparator.nullsFirst(new ResultadoProcesoEstimacionComparator());
        return Comparator.comparing(ResultadoCalculo::getEstimacion, procesoEstimacionComparator);
    }

    private Comparator<ResultadoCalculo> createProcesoTasaAlternaComparator() {
        // nullsFirst is used becase ResultadoCalculo.resultadoTasaAlterna() might be null
        Comparator<ResultadoProcesoTasaAlterna> tasaAlternaComparator = Comparator.nullsFirst(new ResultadoProcesoTasaAlternaComparator());
        return Comparator.comparing(ResultadoCalculo::getTasaAlterna, tasaAlternaComparator);
    }

    private Comparator<ResultadoCalculo> createProcesoCdaComparator(){
        // nullsFirst is used becase ResultadoCalculo.resultadoCda() might be null
        Comparator<ResultadoProcesoCda> cdaComparator = Comparator.nullsFirst(new ResultadoProcesoCdaComparator());
        return Comparator.comparing(ResultadoCalculo::getComposicionDemograficaAnterior, cdaComparator);
    }

    private Comparator<ResultadoCalculo> createProcesoSalidasComparator(){
        // nullsFirst is used becase ResultadoCalculo.resultadoSalidas() might be null
        Comparator<ResultadoProcesoSalidas> salidasComparator = Comparator.nullsFirst(new ResultadoProcesoSalidasComparator());
        return Comparator.comparing(ResultadoCalculo::getSalidas, salidasComparator);
    }

    private Comparator<ResultadoCalculo> createProcesoGlobalComparator(){
        // nullsFirst is used becase ResultadoCalculo.resultadoProcesoGlobal() might be null
        Comparator<ResultadoProcesoAplicacion> procesoGlobalComparator = Comparator.nullsFirst(new ResultadoProcesoAplicacionComparator());
        return Comparator.comparing(ResultadoCalculo::getResultadoAplicacion, procesoGlobalComparator);
    }

    private int compareWithLogging(Comparator<ResultadoCalculo> comparator, ResultadoCalculo firstCalcResult, ResultadoCalculo secondCalcResult, String comparatorName) {
        LOGGER.info(START_COMPARISON, comparatorName);
        return comparator.compare(firstCalcResult, secondCalcResult);
    }

    /**
     * A utility class to hold a comparator and its associated log message.
     */
    static class ComparatorInfo {
        final Comparator<ResultadoCalculo> comparator;
        final String logMessage;

        ComparatorInfo(Comparator<ResultadoCalculo> comparator, String logMessage) {
            this.comparator = comparator;
            this.logMessage = logMessage;
        }
    }
}