package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MejorasCertificadoTest {

    private final SolicitudCertificado solicitud =
        new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

    private ServicioCertificados crearServicioBase() {
        return new ServicioCertificadosImpl(
            new ValidadorAsistencia(), new GeneradorCertificadoPDF(),
            new FirmaDigitalService(), new EnvioCorreoService());
    }

    @Test
    void emiteSinNingunaMejoraActivada() {
        ServicioCertificados base = crearServicioBase();
        assertDoesNotThrow(() -> base.emitir(solicitud));
    }

    @Test
    void combinaLasTresMejorasSinCrearUnaClaseNueva() {
        ServicioCertificados conTodo = new MejoraTraduccionIngles(
            new MejoraCodigoQR(
                new MejoraMarcaDeAgua(crearServicioBase(), "UDES 2026"),
                "https://confudes.udes.edu.co/verificar"));
        assertDoesNotThrow(() -> conTodo.emitir(solicitud));
    }

    @Test
    void unaSolaMejoraFuncionaDeFormaIndependiente() {
        ServicioCertificados soloMarcaDeAgua = new MejoraMarcaDeAgua(crearServicioBase(), "UDES 2026");
        assertDoesNotThrow(() -> soloMarcaDeAgua.emitir(solicitud));
    }
}