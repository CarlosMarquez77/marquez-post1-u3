package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmisionCertificadoTest {

    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();

        ServicioCertificados colaborador = new ServicioCertificadosImpl(validador, generador, firma, correo);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

        assertDoesNotThrow(() -> colaborador.emitir(solicitud));
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}