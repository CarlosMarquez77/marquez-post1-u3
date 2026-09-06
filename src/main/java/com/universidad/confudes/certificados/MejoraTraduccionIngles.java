package com.universidad.confudes.certificados;

/**
 * Decorator que traduce el texto fijo del certificado al ingles,
 * sobre el PDF ya emitido por el ServicioCertificados envuelto.
 */
public class MejoraTraduccionIngles implements ServicioCertificados {

    private final ServicioCertificados envuelto;

    public MejoraTraduccionIngles(ServicioCertificados envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documento = envuelto.emitir(solicitud);
        return UtilidadesPDF.traducirAIngles(documento);
    }
}