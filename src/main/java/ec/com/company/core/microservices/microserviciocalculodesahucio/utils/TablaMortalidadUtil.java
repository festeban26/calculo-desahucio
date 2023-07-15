package ec.com.company.core.microservices.microserviciocalculodesahucio.utils;

import ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions.coreException;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.mortalidad.QxsMortalidadPorEdad;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.mortalidad.RP2000;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.mortalidad.TMIESS2002;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class TablaMortalidadUtil {
    private QxsMortalidadPorEdad qxsMortalidadPorEdad_Hombres;
    private QxsMortalidadPorEdad qxsMortalidadPorEdad_Mujeres;

    public TablaMortalidadUtil(String versionTablaMortalidad)
            throws coreException {
        QxsMortalidadPorEdad.VersionTablaMortalidadEnum versionTablaMortalidadEnum
                = QxsMortalidadPorEdad.VersionTablaMortalidadEnum.fromString(versionTablaMortalidad);

        if (versionTablaMortalidadEnum == QxsMortalidadPorEdad.VersionTablaMortalidadEnum.TMIESS2002) {
            this.qxsMortalidadPorEdad_Hombres = TMIESS2002.getQxsMortalidadPorEdad_Hombres();
            this.qxsMortalidadPorEdad_Mujeres = TMIESS2002.getQxsMortalidadPorEdad_Mujeres();
        } else if (versionTablaMortalidadEnum == QxsMortalidadPorEdad.VersionTablaMortalidadEnum.RP2000) {
            this.qxsMortalidadPorEdad_Hombres = RP2000.getQxsMortalidadPorEdad_Hombres();
            this.qxsMortalidadPorEdad_Mujeres = RP2000.getQxsMortalidadPorEdad_Mujeres();
        }
    }

    public BigDecimal getQx(String genero, Integer edad) throws coreException {
        QxsMortalidadPorEdad.Genero generoEnum = QxsMortalidadPorEdad.Genero.getEnumByCode(genero);
        if(generoEnum == QxsMortalidadPorEdad.Genero.MASCULINO){
            return qxsMortalidadPorEdad_Hombres.getQx(generoEnum, edad);
        }
        else if (generoEnum == QxsMortalidadPorEdad.Genero.FEMENINO){
            return qxsMortalidadPorEdad_Mujeres.getQx(generoEnum, edad);
        }
        else throw new coreException("generoEnum no definido para TablaMortalidadUtil", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
