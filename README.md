# marquez-post1-u3

Post-contenido — Patrones Estructurales aplicados al backend de ConfUDES

## Decisiones de diseño

### Necesidad 1 — Registro de asistencia
**Patrón elegido:** Adapter (`AdaptadorQRCheck`).

**Justificación:** El sistema depende de un único colaborador externo,
`QRCheckClient`, cuyo contrato (`validar(QRCheckRequest)`, con payload
y códigos de respuesta 200/401) no coincide con el contrato interno
`ServicioAsistencia` que ya usa `ControladorCheckIn` y el módulo de
reportes. No hay múltiples colaboradores que coordinar — hay uno solo,
con una interfaz incompatible que traducir: `eventoId` como `String`
frente al `long` que exige `QRCheckRequest`, y el código de respuesta
del proveedor frente al `ResultadoCheckIn` que el resto del sistema
espera. Adapter resuelve exactamente ese problema, envolviendo
`QRCheckClient` detrás del contrato `ServicioAsistencia` sin modificar
ninguna de las clases dadas.

**Por qué no aplicaría Facade:** Facade resuelve el problema de
demasiados colaboradores conocidos por el cliente, pero aquí solo hay
un colaborador externo. Envolver `QRCheckClient` con un Facade no
resolvería el problema real, que es de incompatibilidad de contratos,
no de exceso de coordinación.

### Necesidad 2 — Emisión de certificados
**Patrón elegido:** Facade (`ServicioCertificadosImpl`).

**Justificación:** Los cuatro servicios (`ValidadorAsistencia`,
`GeneradorCertificadoPDF`, `FirmaDigitalService`, `EnvioCorreoService`)
no tienen ningún contrato incompatible — todos funcionan bien tal como
están. El problema real es que `ControladorCertificados` tenía que
conocer y orquestar los cuatro directamente, lo que lo volvía frágil
ante cualquier cambio en uno de ellos. Facade agrupa esa orquestación
(validar → generar → firmar → enviar) detrás de una única operación
simple (`emitir(...)`), reduciendo el controlador a un solo colaborador
inyectado y un método de 2 líneas de cuerpo.

**Por qué no aplicaría Adapter:** Adapter resuelve un problema de
contratos incompatibles entre un cliente y un único colaborador. Aquí
no hay ningún contrato que traducir — los cuatro servicios ya exponen
APIs perfectamente utilizables. Usar Adapter no reduciría en nada el
número de colaboradores que `ControladorCertificados` necesita conocer,
que es el problema real que había que resolver.