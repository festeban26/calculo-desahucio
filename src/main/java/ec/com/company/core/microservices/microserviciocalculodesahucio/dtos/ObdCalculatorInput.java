package ec.com.company.core.microservices.microserviciocalculodesahucio.dtos;

import ec.com.company.core.microservices.microserviciocalculodesahucio.models.rotacion.QxsRotacionPorTs;
import ec.com.company.core.microservices.microserviciocalculodesahucio.utils.TablaMortalidadUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class ObdCalculatorInput {
    @NonNull
    String nombrePersona;
    @NonNull
    BigDecimal tasaDescuento;
    @NonNull
    BigDecimal tasaIncrementoSalarial_cortoPlazo;
    @NonNull
    BigDecimal tasaIncrementoSalarial_largoPlazo;
    @NonNull
    Integer tFact;
    @NonNull
    QxsRotacionPorTs qxsRotacionPorTs;
    @NonNull
    TablaMortalidadUtil tablaMortalidad;
    @NonNull
    int tipoPersona;
    @NonNull
    String genero;
    @NonNull
    BigDecimal sueldoPersona;
    @NonNull
    BigDecimal tsPersona;
    @NonNull
    Integer edadPersona;
}
