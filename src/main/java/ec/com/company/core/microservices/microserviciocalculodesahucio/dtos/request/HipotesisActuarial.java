package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;


import lombok.NonNull;

import java.math.BigDecimal;

public record Hipotesiscompanyl(@NonNull BigDecimal tasaFinancieraDescuento,
                                 @NonNull BigDecimal tasaRotacionPromedio,
                                 @NonNull BigDecimal tasaIncrementoSalarios,
                                 @NonNull BigDecimal porcentajeIncrementoSalarialEstimado,
                                 @NonNull BigDecimal porcentajeIncrementoSalarialEstimadoProporcional,
                                 @NonNull BigDecimal porcentajeVariacionSensibilidad,
                                 @NonNull BigDecimal porcentajeVariacionSensibilidadRotacion) {
}
