package com.universidad.confudes.certificados;

import org.springframework.stereotype.Service;

/**
 * Facade que agrupa la orquestacion de los cuatro servicios de emision
 * de certificados (validacion, generacion de PDF, firma digital y
 * envio de correo) detras de una unica operacion simple. El cliente
 * (ControladorCertificados) ya no necesita conocer ni coordinar los
 * cuatro colaboradores por separado.
 */
@Service
public class ServicioCertificadosImpl {

    private final ValidadorAsistencia validador;
    private final GeneradorCertificadoPDF generador;
    private final FirmaDigitalService firma;
    private final EnvioCorreoService correo;

    public ServicioCertificadosImpl(ValidadorAsistencia validador, GeneradorCertificadoPDF generador,
                                     FirmaDigitalService firma, EnvioCorreoService correo) {
        this.validador = validador;
        this.generador = generador;
        this.firma = firma;
        this.correo = correo;
    }

    public byte[] emitir(String eventoId, String participanteId, String nombre, String correoDestino) {
        if (!validador.tieneAsistenciaMinima(participanteId, eventoId, 0.8)) {
            throw new IllegalStateException("Asistencia insuficiente");
        }

        byte[] doc = generador.iniciarDocumento("plantilla-2026");
        generador.insertarDatosParticipante(doc, nombre, eventoId, "2026-08-06");
        byte[] documentoFinal = generador.finalizarDocumento();

        FirmaDigitalService.Sesion sesion = firma.abrirSesion("cert-udes-2026.pfx");
        byte[] documentoFirmado = firma.firmar(sesion, documentoFinal);
        firma.cerrarSesion(sesion);

        correo.adjuntarArchivo(correoDestino, documentoFirmado, "certificado-" + participanteId + ".pdf");
        correo.enviar("Su certificado de participación", "Adjunto encontrará su certificado.");

        return documentoFirmado;
    }
}