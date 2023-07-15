package ec.com.company.core.microservices.microserviciocalculodesahucio.procesos.blueprint;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;

public interface ProcesoStrategy<T> {
    T perform() throws coreException;
}
