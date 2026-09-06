package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {
    private final ServicioCertificados servicioCertificados;

    public ControladorCertificados(ServicioCertificados servicioCertificados) {
        this.servicioCertificados = servicioCertificados;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                          @RequestParam String nombre, @RequestParam String correoDestino) {
        servicioCertificados.emitir(new SolicitudCertificado(eventoId, participanteId, nombre, correoDestino));
        return ResponseEntity.ok("Certificado emitido y enviado");
    }
}