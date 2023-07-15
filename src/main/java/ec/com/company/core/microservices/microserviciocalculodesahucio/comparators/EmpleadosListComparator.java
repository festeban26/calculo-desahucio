package ec.com.company.core.microservices.microserviciocalculodesahucio.comparators;

import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.EmpleadoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class EmpleadosListComparator implements Comparator<List<EmpleadoResponse>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadosListComparator.class);

    @Override
    public int compare(List<EmpleadoResponse> empleados1, List<EmpleadoResponse> empleados2) {
        Objects.requireNonNull(empleados1, "empleados1 cannot be null");
        Objects.requireNonNull(empleados2, "empleados2 cannot be null");

        LOGGER.debug("Comparing two lists of EmpleadoResponse with {} and {} elements", empleados1.size(), empleados2.size());

        // Iterate over the elements of the first list of empleados
        for (EmpleadoResponse emp1 : empleados1) {
            // Find the corresponding element in the other list based on the identificacion
            EmpleadoResponse emp2 = empleados2.stream()
                    .filter(e2 -> e2.getIdentificacion().equalsIgnoreCase(emp1.getIdentificacion()))
                    .findFirst()
                    .orElse(null);
            if (emp2 == null) {
                LOGGER.error("EmpleadoResponse {} is not present in the other list", emp1.getIdentificacion());
                return 1;
            }
            // Compare the two elements
            else if (!emp1.equals(emp2)) {
                // The two lists have different EmpleadoResponse objects
                return 1;
            }
        }
        // If we reach this point, the two lists have the same elements
        return 0;
    }
}
