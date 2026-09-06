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

### Reflexión — Composite y Flyweight
La agenda de cada congreso (tracks → sesiones → actividades) es una
estructura árbol-de-partes donde Composite encajaría naturalmente,
permitiendo tratar una actividad individual y una sesión completa de
forma uniforme. Flyweight, en cambio, no aplica a las credenciales QR:
cada una tiene datos únicos e irrepetibles por participante y evento,
sin estado compartible entre instancias que valga la pena extraer.

### Necesidad 3 — Mejoras opcionales del certificado
**Patrón elegido:** Decorator (`MejoraMarcaDeAgua`, `MejoraCodigoQR`, `MejoraTraduccionIngles`).

**Justificación:** Se descartó la herencia (una subclase por combinación)
porque con 3 mejoras ya hay 8 combinaciones posibles, y agregar una
cuarta mejora futura duplicaría ese número a 16 subclases — no escala.
También se descartaron los parámetros booleanos en `emitir(...)`
porque cada mejora nueva agregaría un parámetro más y un `if` más
dentro del método, además de que el orden de aplicación de las mejoras
quedaría fijo en el código en vez de ser decidido por quien arma el
servicio. Decorator resuelve ambos problemas: cada mejora es una clase
que implementa `ServicioCertificados` y envuelve otro
`ServicioCertificados` (el Facade base, o ya otro decorator), así que
se apilan en cualquier orden sin que el número de clases crezca con el
número de combinaciones — a lo sumo una clase por mejora.

**Por qué no serviría el patrón de la Necesidad 4 (Proxy) aquí:** un
Proxy decide si delega o no en el objeto real; nunca agrega
comportamiento al resultado ni se combina naturalmente con otro Proxy
para acumular efectos. Usar Proxy para las mejoras dejaría sin resolver
el problema de combinarlas libremente — necesitaríamos que cada "mejora"
tanto decida como transforme, mezclando responsabilidades que Decorator
mantiene separadas.

### Necesidad 4 — Control de acceso a la descarga masiva
**Patrón elegido:** Proxy de protección (`ProxyControlAccesoCertificados`).

**Justificación:** El resto del sistema debe seguir inyectando
`ServicioCertificados` sin saber que existe una verificación de rol.
Proxy implementa la misma interfaz que el objeto real y controla el
acceso a él: si `ContextoUsuario.rolActual()` no es `ORGANIZADOR` ni
`ADMIN`, lanza `SecurityException` **sin invocar** `real.emitir(...)`,
evitando gastar llamadas costosas al proveedor de firma en usuarios sin
permiso.

**Por qué no serviría el patrón de la Necesidad 3 (Decorator) aquí:**
un Decorator siempre delega en el objeto envuelto — esa es su garantía
estructural, nunca corta la llamada. Si usáramos un Decorator para el
control de acceso, la operación costosa de emisión se ejecutaría
siempre, y solo después se podría "reaccionar" al resultado — para
cuando eso pasa, el proveedor de firma ya gastó una llamada con un
usuario sin permiso. Proxy es el único de los dos que puede decidir
*antes* de ejecutar.

### Reflexión — Composite y Flyweight (opcional)
La agenda de cada congreso (tracks → sesiones → actividades) es una
estructura árbol-de-partes donde Composite encajaría naturalmente,
permitiendo tratar una actividad individual y una sesión completa de
forma uniforme. Flyweight, en cambio, no aplica a las credenciales QR:
cada una tiene datos únicos e irrepetibles por participante y evento,
sin estado compartible entre instancias que valga la pena extraer.

## Herramientas utilizadas
- Java 17, Spring Boot 3.2, Apache Maven, JUnit 5
- VS Code, Git, GitHub

## Conclusiones
La parte más difícil de este post-contenido fue distinguir Decorator de
Proxy en las Necesidades 3 y 4, precisamente porque estructuralmente
son casi idénticos: ambos implementan la misma interfaz que envuelven
y reciben ese objeto por constructor. La diferencia solo se nota cuando
uno se pregunta por la intención de cada envoltura, no por su forma:
Decorator siempre termina delegando en el objeto real y le agrega algo
al resultado, mientras que Proxy puede decidir si esa llamada llega a
ejecutarse o no —en este caso, para controlar el acceso según el rol
del usuario—. Ese matiz también deja una lección más general: dos
soluciones pueden verse igual de bien escritas en código y aun así
resolver problemas distintos, así que el patrón correcto no se elige
por cómo se ve la clase, sino por qué se supone que debe pasar cuando
se usa. Ese fue el mismo hilo conductor de todo el laboratorio: en cada
necesidad hubo más de una alternativa que "funcionaba", y la parte que
realmente se evaluó fue poder explicar por qué la alternativa cercana
no encajaba igual de bien.