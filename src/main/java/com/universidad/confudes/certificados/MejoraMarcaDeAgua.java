package com.universidad.confudes.certificados;

/**
 * Decorator que aplica una marca de agua institucional sobre el PDF
 * ya emitido por el ServicioCertificados envuelto. Puede combinarse
 * con cualquier otra mejora, en cualquier orden.
 */
public class MejoraMarcaDeAgua implements ServicioCertificados {

    private final ServicioCertificados envuelto;
    private final String textoMarcaDeAgua;

    public MejoraMarcaDeAgua(ServicioCertificados envuelto, String textoMarcaDeAgua) {
        this.envuelto = envuelto;
        this.textoMarcaDeAgua = textoMarcaDeAgua;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documento = envuelto.emitir(solicitud);
        return UtilidadesPDF.aplicarMarcaDeAgua(documento, textoMarcaDeAgua);
    }
}