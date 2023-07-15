package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;

import jakarta.validation.Valid;
import lombok.NonNull;

public record EstudioAnterior(
        @Valid
        @NonNull
        HipotesiscompanylAnterior hipotesiscompanyl,
        @Valid
        @NonNull
        ResultadoDesahucio resultadoDesahucio
) {
}
