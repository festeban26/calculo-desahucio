package ec.com.company.core.microservices.microserviciocalculodesahucio;


import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EstudioRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoCalculo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.MicroserviceResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.utils.EjecucionEstudioUtil;
import ec.com.company.core.microservices.microserviciocalculodesahucio.validators.EstudioRequestValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/")
class MicroservicioCalculoDesahucioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroservicioCalculoDesahucioController.class);
    private final EstudioRequestValidator estudioRequestValidator;

    public MicroservicioCalculoDesahucioController(EstudioRequestValidator estudioRequestValidator) {
        this.estudioRequestValidator = estudioRequestValidator;
    }

    @CrossOrigin()
    @PostMapping(
            value = "/v1",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> compute(@Valid @RequestBody EstudioRequest estudioRequest) {

        String numeroProceso = estudioRequest.numeroProceso();
        LOGGER.info("Received POST request for estudio with numero proceso: {}", numeroProceso);
        final long startTime = System.nanoTime();

        Errors errors = new BeanPropertyBindingResult(estudioRequest, "estudioRequest");
        // Validar estudioRequest. Esto implica, por ejemplo, que si la empresa es antigua, debe tener datos de empresa antigua.
        estudioRequestValidator.validate(estudioRequest, errors);
        if (errors.hasErrors()) {
            return EstudioRequestValidator.returnErrors(errors);
        }

        try (var ignored = MDC.putCloseable("numeroProceso", estudioRequest.numeroProceso())) {
            LOGGER.info("Iniciando calculo de desahucio");
            ResultadoCalculo resultadoCalculo = EjecucionEstudioUtil.ejecutar(estudioRequest);
            MicroserviceResponse<ResultadoCalculo> response = createSuccessfulResponse(resultadoCalculo);
            logOperationTime(startTime, estudioRequest.empleados().size());
            return ResponseEntity.ok(response);
        } catch (coreException e) {
            MicroserviceResponse<String> errorResponse = createErrorResponse(e);
            logOperationTime(startTime, estudioRequest.empleados().size());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private static void logOperationTime(long startTime, int numEmployees) {
        final long estimatedTime = System.nanoTime() - startTime;
        long estimatedTimeInSeconds = Duration.ofNanos(estimatedTime).getSeconds();
        LOGGER.info("Finaliza calculo de desahucio para " + numEmployees + " empleados. " +
                "Operation took " + estimatedTimeInSeconds + " seconds");
    }

    private static MicroserviceResponse<ResultadoCalculo> createSuccessfulResponse(
            ResultadoCalculo resultadoCalculo) {
        int statusCode = 0; // Blame coworker for returning 0 instead of HttpStatus.OK.value() XD
        String statusMessage = HttpStatus.OK.getReasonPhrase();
        LOGGER.info("Sending the response back with status code: {}", statusCode);
        return MicroserviceResponse.<ResultadoCalculo>builder()
                .status(statusCode)
                .message(statusMessage)
                .content(resultadoCalculo)
                .build();
    }

    private static MicroserviceResponse<String> createErrorResponse(coreException coreException) {
        int statusCode = coreException.getErrorCode().value();
        String statusMessage = coreException.getErrorCode().getReasonPhrase();
        String errorMessage = coreException.getMessage();
        LOGGER.error("Sending the response back with status code: {}", statusCode);
        return MicroserviceResponse.<String>builder()
                .status(statusCode)
                .message(statusMessage)
                .content(errorMessage)
                .build();
    }
}

