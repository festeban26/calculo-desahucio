package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;

import java.math.BigDecimal;

public record CentrosDeCostoRequest (String nombre, BigDecimal rotacionPromedio){
}
