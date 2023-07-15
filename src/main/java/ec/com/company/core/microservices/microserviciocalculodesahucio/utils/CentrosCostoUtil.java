package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import ec.com.company.core.microservices.microserviciocalculodesahucio.constants.AppConstants;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.CentrosDeCostoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.request.EmpleadoRequest;
import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.CentroDeCostoVo;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

public class CentrosCostoUtil {

    public static List<CentroDeCostoVo> getCentrosDeCosto(
            List<CentrosDeCostoRequest> centrosDeCostoEstudio,
            List<EmpleadoRequest> empleados,
            BigDecimal tasaDeRotacionPromedio)
            throws coreException {


        // Iniciar teniendo una lista de los centros de costos
        Set<String> nombresCentrosDeCostos = new HashSet<>();
        for (EmpleadoRequest empleado : empleados) {
            if (empleado.nombreCentroDeCosto() != null && !empleado.nombreCentroDeCosto().trim().isEmpty()) {
                nombresCentrosDeCostos.add(empleado.nombreCentroDeCosto());
            } else nombresCentrosDeCostos.add(AppConstants.TEXTO_CENTRO_DE_COSTO_NINGUNO);
        }

        Map<String, CentroDeCostoVo> centroDeCostosMap = new HashMap<>();
        // Para cada centro de costo. Crear el objeto de centro de costo con su respectivo nombre y tasa de rotacion
        for (String nombreCentroDeCosto : nombresCentrosDeCostos) {
            BigDecimal tasaDeRotacion = buscarTasaDeRotacion(nombreCentroDeCosto,
                    centrosDeCostoEstudio,
                    tasaDeRotacionPromedio);
            CentroDeCostoVo centroDeCostoVO = CentroDeCostoVo.builder()
                    .nombre(nombreCentroDeCosto)
                    .tasaRotacion(tasaDeRotacion)
                    .build();
            centroDeCostosMap.put(nombreCentroDeCosto, centroDeCostoVO);
        }

        // Luego, agregar a las personas a sus correspondientes centros de costos
        for (EmpleadoRequest empleado : empleados) {
            String nombreCentroCosto;
            if (empleado.nombreCentroDeCosto() != null && !empleado.nombreCentroDeCosto().trim().isEmpty()) {
                nombreCentroCosto = empleado.nombreCentroDeCosto();
            }
            // Persona no pertenece a ningun centro de costo
            else {
                nombreCentroCosto = AppConstants.TEXTO_CENTRO_DE_COSTO_NINGUNO;
            }
            CentroDeCostoVo centroDeCostoVO = centroDeCostosMap.get(nombreCentroCosto);
            centroDeCostoVO.getEmpleados().add(empleado);
        }

        return new ArrayList<>(centroDeCostosMap.values());
    }

    public static BigDecimal buscarTasaDeRotacion(
            String nombreCentroDeCosto,
            List<CentrosDeCostoRequest> centrosDeCostoEstudio,
            BigDecimal tasaDeRotacionPromedio)
            throws coreException {
        //Si no usa rotacion por centro de costo
        if (nombreCentroDeCosto.equalsIgnoreCase(AppConstants.TEXTO_CENTRO_DE_COSTO_NINGUNO)) {
            return tasaDeRotacionPromedio.multiply(BigDecimal.valueOf(0.01));
        }

        // Recuperar la tasa de rotacion por centro de costo
        for (CentrosDeCostoRequest centroDeCosto : centrosDeCostoEstudio) {
            if (nombreCentroDeCosto.equalsIgnoreCase(centroDeCosto.nombre())) {
                // tiene que ser expresado como porcentaje
                return centroDeCosto.rotacionPromedio().multiply(BigDecimal.valueOf(0.01));
            }
        }
        throw new coreException("No se encuentra una tasa de rotacion para el centro de costo '" + nombreCentroDeCosto + "'",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
