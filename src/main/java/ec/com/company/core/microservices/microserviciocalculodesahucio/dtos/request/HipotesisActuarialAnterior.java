package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;


import lombok.NonNull;

import java.math.BigDecimal;

public record HipotesiscompanylAnterior(
        // Para el validador se muestre la tabla hipotesis companyl, la cual muestra que los valores van entre 0.5 y 10.48.
        @NonNull BigDecimal tasaFinancieraDescuento,
        // Para el validador se muestre la tabla hipotesis companyl, la cual muestra que los valores van entre 0 y 90.
        @NonNull BigDecimal tasaRotacionPromedio,
        @NonNull BigDecimal tasaIncrementoSalarios,
        @NonNull BigDecimal porcentajeIncrementoSalarialEstimado) {
}
