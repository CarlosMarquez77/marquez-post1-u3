package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;

/**
 * Proxy de proteccion: verifica el rol del usuario actual antes de
 * delegar en el ServicioCertificados real. Si el rol no es
 * ORGANIZADOR o ADMIN, rechaza con una excepcion SIN llegar a invocar
 * la operacion costosa (a diferencia de un Decorator, que siempre
 * delega y solo agrega algo al resultado).
 */
public class ProxyControlAccesoCertificados implements ServicioCertificados {

    private final ServicioCertificados real;

    public ProxyControlAccesoCertificados(ServicioCertificados real) {
        this.real = real;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        String rol = ContextoUsuario.rolActual();
        if (!rol.equals("ORGANIZADOR") && !rol.equals("ADMIN")) {
            throw new SecurityException(
                "Rol '" + rol + "' no autorizado para descarga masiva de certificados");
        }
        return real.emitir(solicitud);
    }
}