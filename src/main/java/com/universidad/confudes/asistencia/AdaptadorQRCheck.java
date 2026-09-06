package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;
import org.springframework.stereotype.Service;

/**
 * Adapta el SDK del proveedor externo QRCheckAPI (QRCheckClient) al
 * contrato interno ServicioAsistencia que el resto de ConfUDES ya
 * espera. Traduce tipos (String eventoId -> long idEvento), formato
 * de payload (credencialQR -> "QR-" + credencialQR) y codigo de
 * respuesta del proveedor (200/401) a ResultadoCheckIn.
 */
@Service
public class AdaptadorQRCheck implements ServicioAsistencia {

    private final QRCheckClient qrCheckClient;

    public AdaptadorQRCheck(QRCheckClient qrCheckClient) {
        this.qrCheckClient = qrCheckClient;
    }

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        long idEvento = Long.parseLong(eventoId.replaceAll("\\D", ""));
        String payload = credencialQR;

        QRCheckRequest request = new QRCheckRequest(payload, idEvento);
        QRCheckResponse response = qrCheckClient.validar(request);

        boolean exitoso = response.getCodigoRespuesta() == 200;
        return new ResultadoCheckIn(exitoso, response.getDetalle());
    }
}