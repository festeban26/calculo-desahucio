package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmpleadoRequest(
        @NonNull
        String identificacion,
        @NonNull
        String nombreCompleto,
        @NonNull
        Integer tipo,
        @NonNull
        String sexo,
        @NonNull
        @JsonFormat(pattern = AppConstants.DATE_PATTERN, timezone = "America/Guayaquil")
        LocalDate fechaNacimiento,
        @NonNull
        @JsonFormat(pattern = AppConstants.DATE_PATTERN, timezone = "America/Guayaquil")
        LocalDate fechaIngresoJubilacion,
        @NonNull
        @JsonFormat(pattern = AppConstants.DATE_PATTERN, timezone = "America/Guayaquil")
        LocalDate fechaIngresoDesahucio,
        @NonNull
        BigDecimal remuneracionPromedioDesahucio,
        BigDecimal reservaDesahucio,
        BigDecimal costoLaboralDesahucio,
        BigDecimal interesNetoDesahucio,
        String nombreCentroDeCosto
) {
}
