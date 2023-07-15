package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// Use getter and setter when using JsonAlias
@Getter
@Setter
public class Parametros {
    @NonNull
    @JsonFormat(pattern = AppConstants.DATE_PATTERN, timezone = "America/Guayaquil")
    LocalDate fechaValoracion;

    @NonNull
    @JsonFormat(pattern = AppConstants.DATE_PATTERN, timezone = "America/Guayaquil")
    @JsonAlias("fechaProyeccion")
    LocalDate fechaProyectada;

    @NonNull
    @JsonFormat(pattern = AppConstants.DATE_PATTERN, timezone = "America/Guayaquil")
    LocalDate fechaEstimacion;

    @NonNull
    Boolean usaTasaRotacionPorCentroDeCosto;

    @NonNull
    String versionTablaMortalidadDesahucio = AppConstants.DEFAULT_VALUE_VERSION_TABLA_MORTALIDAD_DESAHUCIO;

    @NonNull
    @JsonAlias("ajusteTFact")
    Integer tFactCalculoPorFlujos;

    @NonNull
    @JsonAlias("ajusteQxRotacion")
    BigDecimal ajusteQxRotacionCalculoPorFlujos;

    @NonNull
    String tipoTraspaso;
}
