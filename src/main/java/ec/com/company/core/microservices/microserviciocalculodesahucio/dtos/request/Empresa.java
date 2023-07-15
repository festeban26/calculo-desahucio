package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request;

import lombok.NonNull;

public record Empresa(
        @NonNull
        String estado) {
}
