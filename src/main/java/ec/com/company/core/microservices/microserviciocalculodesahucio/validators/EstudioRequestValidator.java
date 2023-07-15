package ec.com.company.core.microservices.microserviciocalculodesahucio.validators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.enums.TipoEmpresaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudioRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EstudioRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        EstudioRequest request = (EstudioRequest) target;
        TipoEmpresaEnum antigua = TipoEmpresaEnum.ANTIGUA;

        if (isEmpty(request.empresa().estado())) {
            errors.rejectValue("empresa.estado", "field.required", "Estado is required");
            return;
        }

        // La validacion solo continua si la empresa es antigua
        if (!esEmpresaAntigua(request.empresa().estado())) {
            return;
        }

        if (request.estudioAnterior() == null) {
            String msg = String.format("field.required: estudioRequest.estudioAnterior is required when empresa.estado is %s", antigua.getCodigo());
            errors.rejectValue("estudioAnterior", "field.required", msg);
        }
    }

    private boolean isEmpty(String value) {
        return value.isEmpty();
    }

    private boolean esEmpresaAntigua(String estado) {
        return estado.equalsIgnoreCase(TipoEmpresaEnum.ANTIGUA.getCodigo());
    }

    public static ResponseEntity<Object> returnErrors(Errors errors) {

        List<String> errorsMessages = errors.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        String error = "Bad Request";
        String message = "Validation failed";
        return buildResponseEntity(new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), error, errorsMessages, message));
    }

    private static ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Getter
    @AllArgsConstructor
    static class ApiError {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private List<String> errors;
        private String message;
    }
}
