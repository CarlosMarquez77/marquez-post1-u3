package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// Refactorizado: ahora depende de un unico colaborador (el Facade),
// que encapsula toda la orquestacion de los cuatro servicios.
@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {
    private final ServicioCertificadosImpl servicioCertificados;

    public ControladorCertificados(ServicioCertificadosImpl servicioCertificados) {
        this.servicioCertificados = servicioCertificados;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                          @RequestParam String nombre, @RequestParam String correoDestino) {
        servicioCertificados.emitir(eventoId, participanteId, nombre, correoDestino);
        return ResponseEntity.ok("Certificado emitido y enviado");
    }
}