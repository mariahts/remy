# Defensa de la entrega 4 — guion, código que hay que saber y preguntas probables

Documento de apoyo para la exposición oral. No sustituye a la memoria Word: sirve para
practicar lo que hay que decir y para saber qué código hay que tener abierto.

---

## 1. Cómo se evalúa la defensa

| Dato | Valor |
|---|---|
| Tiempo de exposición | 10 minutos por pareja |
| Preguntas del profesor | 5 minutos |
| Peso en la nota de cada RA | 16 % |
| Reparto dentro de los 10 minutos | 1 min problema · 4 min demo · 2 min arquitectura · 1 min multimedia · 1 min pruebas · 1 min cierre |
| Participaciones | **Los dos tienen que intervenir.** Si uno solo habla, baja la nota de ambos. |
| Defensa individual | 35 % de la nota de presentación: cada persona localiza y explica **su** código, responde preguntas y hace o propone un cambio pequeño. |

Los tres bloques que más se valoran: **demostración del producto** (25 %), **explicación
técnica** (25 %) y **defensa individual** (35 %).

---

## 2. Guion minuto a minuto

| Min | Quién | Qué decir | Qué enseñar |
|---|---|---|---|
| 0:00–1:00 | **María** | El problema: se compra por impulso y luego no se cocina lo que hay en la nevera. REMY es una app de cocina que cruza tu despensa con un catálogo de recetas y te dice qué puedes cocinar hoy. Personas usuarias: quien vive solo o en pareja y no quiere tirar comida. Alcance de la v1: despensa, recetas, sugerencias, cocinar con foto, plan semanal de comidas y cuenta con registro. | La pantalla de Inicio en el emulador, sin tocar nada. |
| 1:00–5:00 | **María** y **Mario** | La demostración (4 minutos, el bloque más largo). **Reparto sugerido**: María demuestra el flujo de datos (despensa → sugerencias → detalle → cocinar con foto) y Mario demuestra el diseño y el plan semanal. | Ver el guion de demo del punto 3. |
| 5:00–7:00 | **María** | Arquitectura y datos: Compose → ViewModel → Repositorio → Room/DataStore. Decidir por qué: Room para lo estructurado y con relaciones (recetas + ingredientes + despensa), DataStore para las preferencias (los filtros por ración). Los ViewModel exponen `StateFlow<EstadoCarga>` y la interfaz nunca toca la base de datos. | Diagrama en `docs/ModeloE_R_Remy.drawio` o el modelo E-R del tablero. |
| 7:00–8:00 | **Mario** (o María, si prefieres) | La función multimedia: la foto del plato con CameraX. | Ver el guion de multimedia del punto 4. |
| 8:00–9:00 | **María** | Pruebas, accesibilidad y limitaciones: 54 unitarias y 22 instrumentadas en verde, la prueba en móvil real, el contraste WCAG medido y lo que queda pendiente. | `docs/pruebas.md` y `docs/revision-diseno-usabilidad.md`. |
| 9:00–10:00 | **Los dos** | Cierre: qué se termina antes de la entrega definitiva. | — |

---

## 3. Guion de demostración (4 minutos)

No enseñéis el recorrido entero: enseñar **un caso de uso completo** con sus datos es lo que
suma. Elegid este y no lo improviséis:

1. **Despensa → Sugerencias** (30 s). Abre la despensa con 13 alimentos de la cuenta demo.
   Entra en **Sugerencias**: la app dice «Con tu despensa puedes cocinar 3 recetas hoy».
   Explica en una frase: el algoritmo de sugerencias (`logica/AlgoritmoSugerencias.kt`)
   cruza los alimentos que tienes con los ingredientes que pide cada receta y separa las
   que puedes cocinar ya de las que «casi están».
2. **Filtros por ración** (30 s). Abre el botón de filtros y marca «Alta en proteína».
   La lista se reduce a 3 recetas. Cierra la app y vuelve a abrirla: **el filtro sigue
   puesto**, porque se guarda en DataStore (`data/PreferenciasRepository.kt`).
   *Aquí está el punto fuerte: datos + preferencia real + persistencia.*
3. **Detalle de receta** (30 s). Abre «Pollo al curry»: los macros por ración (proteína,
   hidratos y grasas) se **calculan** a partir de los ingredientes, no están escritos a
   mano (`data/MacrosAlimento.kt`, función pura `macrosDeReceta`, llamada desde
   `data/PrecargaRecetas.kt`). Cambia las raciones y los macros se reescalan.
4. **Cocinar con foto** (1 min). Pulsa «Marcar como cocinada». Elige **cámara**, haz una
   foto, confirma. Se **descuentan los ingredientes de la despensa** según las raciones
   elegidas y la app avisa si algún ingrediente no se ha podido descontar. En la ficha de
   «Recetas cocinadas» sale tu foto con la fecha.
   *Este es el recorrido multimedia + el recorrido de datos en la misma demo.*
5. **Plan semanal** (30 s, Mario). En Inicio, «Tu semana»: se ve la semana con el mes, se
   toca otro día y el contenido **no parpadea** (esto se corrigió por un defecto encontrado
   en el móvil real). Añade una receta al desayuno.

> Si te preguntan por algo que falla en la demo, no lo maquilles: dilo. El profesor valora
> más «lo detectamos en el móvil real y lo corregimos» que una demo perfecta.

---

## 4. Guion de multimedia (1 minuto)

La función multimedia es **la foto del plato al cocinar**. Tres preguntas que seguro te hacen:

**¿Por qué CameraX y no la cámara del sistema?**
Porque CameraX permite tener la vista previa dentro de la propia pantalla de REMY, con el
disparo en el mismo sitio, y controlar la calidad y la orientación del JPEG. Con la cámara
del sistema tendrías que saltar a otra app y volver.

**¿Cómo recorre el flujo?**
`ui/components/CapturaFoto.kt` → `ProcessCameraProvider` levanta el `Preview` y el
`ImageCapture`; `ui/screens/CapturaPlato.kt` ofrece elegir **cámara** o **galería**
(`ActivityResultContracts.GetContent()`); `data/AlmacenamientoFotos.kt` escribe el JPEG en
el **almacenamiento interno** de la app (`fotos_plato/`, calidad 90) y devuelve la ruta;
`ui/viewmodel/RecetaDetalleViewModel.kt` la guarda y avisa si no se pudo escribir. Para
mostrarla se usa **Coil** (`AsyncImage` en `ui/components/RemyComponents.kt`).

**¿Y los permisos y los errores?**
El permiso de cámara se pide con `ActivityResultContracts.RequestPermission()` y se
comprueba antes con `ContextCompat.checkSelfPermission`. Si se deniega, se ofrece la
galería. Si la foto no se puede guardar, **la cocción se registra igualmente** y se avisa
al usuario: el registro no se pierde por un fallo de la foto.

---

## 5. Mapa de tu código (lo que tienes que saber abrir)

Esto es tu parte defendible. Si te dicen «explica qué hay detrás de esta pantalla», tienes
que saber abrir el fichero en segundos.

| Bloque | Fichero | Qué tienes que saber decir |
|---|---|---|
| Descuento al cocinar | `logica/DescuentoDespensa.kt` | Función ** pura**: recibe la receta, la despensa y las raciones, y devuelve las cantidades nuevas y los nombres de los ingredientes que no se han podido descontar. Al ser pura se testea sin Android, y por eso hay 5 casos en `DescuentoDespensaTest`. |
| Macros calculados | `data/MacrosAlimento.kt` | `macrosDeReceta` suma el aporte de cada ingrediente (`macro × cantidad / 100`). Se calcula **en la precarga**, así que los macros de la app y los del catálogo no pueden desincronizarse, y **no hace falta migración de base de datos**. |
| Catálogo | `assets/recetas.json` + `data/PrecargaRecetas.kt` | 31 alimentos y 20 recetas. La precarga inserta los alimentos que faltan y **actualiza las recetas existentes por nombre**, sin duplicar ni borrar la fecha ni la foto de las cocciones del usuario. |
| Fotos del catálogo | `ui/components/ImagenesReceta.kt` | Mapa de nombre de receta → `R.drawable`. Se usa un mapa en código y **no** `getIdentifier`, porque con `isShrinkResources` los recursos no referenciados se borran de la APK. |
| Preferencia real | `data/PreferenciasRepository.kt` | DataStore para los filtros por ración (alta proteína, baja hidratos, baja grasa). Persisten entre sesiones. |
| Filtro de macros | `logica/FiltroRecetas.kt` | Aplica los umbrales por ración: alta proteína ≥ 25 g, baja hidratos ≤ 30 g, baja grasa ≤ 15 g. |
| Pantallas | `ui/screens/` | `InicioScreen`, `DespensaScreen`, `RecetasScreen`, `SugerenciaScreen`, `RecetaDetalleScreen`, `PerfilScreen`, `LoginScreen`, `AnadirPlanSheet`, `CapturaPlato`, `BienvenidaScreen`. |
| Navegación | `ui/navigation/AppNavigation.kt` + `Routes.kt` | Barra inferior con **cuatro** destinos: Inicio, Despensa, Recetas y Sugerencias. |
| Base de datos | `data/RemyDatabase.kt` | Room **v8** con 7 migraciones encadenadas (1→2 … 7→8) y el esquema exportado en `app/schemas/`, para poder probar las migraciones. |
| Autenticación | `ui/screens/LoginScreen.kt` + `data/SupabaseClientProvider.kt` | Supabase con PKCE y esquema `remy://login-callback`. El detalle importante: el proyecto tiene `mailer_autoconfirm = false`, así que una cuenta nueva **no abre sesión** hasta confirmar el correo; por eso, tras el alta se intenta iniciar sesión y, si falla, se explica que hay que confirmar el correo y se ofrece reenviarlo. |
| Defecto del calendario | `ui/viewmodel/PlanViewModel.kt` | El estado se construía con `flatMapLatest` y el «Cargando» se emitía en **cada** cambio de día, así que la pantalla se sustituía entera y se perdía el scroll. Ahora se mantiene el contenido hasta que llegan los datos del día y el día resaltado se mueve de inmediato. |

---

## 6. Preguntas probables (con respuesta)

**«¿Por qué Room y no Retrofit?»**
El catálogo de recetas es un dato que cambia poco y necesita relaciones (receta → ingredientes
→ alimento) y consultas con filtros, que es justo lo que Room hace bien con SQL. La API
externa (Supabase) la usamos solo para autenticación, donde sí hace falta un servidor.

**«¿Qué pasa si se pierde la conexión?»**
La app sigue funcionando con los datos locales: despensa, recetas y sugerencias son Room.
Lo que necesita conexión es el registro y el inicio de sesión. El resto de la app no hace
llamadas de red en el uso diario.

**«¿Cómo has comprobado que la base de datos se migra bien?»**
Con `Migration7To8Test`, una prueba instrumentada que crea la base en la versión 7, mete
datos, la migra a la 8 y comprueba que **los datos siguen ahí** y que las columnas nuevas
existen. El esquema está exportado en `app/schemas/` para que la migración se pueda
revisar.

**«¿Qué parte te ha costado más?»**
Prepara una respuesta real y corta: por ejemplo, el registro (descubrir que el proyecto de
Supabase exige confirmar el correo y que el alta «no hacía nada»), o el parpadeo del
calendario (un `onStart` mal colocado dentro de un `flatMapLatest`). Las dos son defectos
reales que aparecieron probando en el móvil.

**«¿Y la accesibilidad?»**
Se midió el contraste WCAG de todas las combinaciones de texto y fondo: el texto principal
sobre crema da 15,49 (AAA), el secundario 7,35 (AAA), blanco sobre terracota 5,18 (AA) y
blanco sobre azul tinta 15,97 (AAA). Las imágenes llevan `contentDescription` con el nombre
de la receta y los iconos tienen etiqueta. Lo pendiente: las zonas táctiles están a 40 dp y
lo ideal serían 48 dp, y no se ha comprobado la fuente ampliada ni un equipo de gama baja.

**«¿Qué queda pendiente?»**
Memoria Word y presentación, la comprobación con letra grande y gama baja, la prueba con
personas ajenas al equipo, contrastar los macros con una fuente nutricional oficial, la
caducidad de los alimentos de la despensa (que necesitaría una migración) y el modo oscuro
(que no está en la maqueta).

**«¿Por qué hay código generado con IA?»**
La regla del proyecto es que el código generado con IA cuenta como aportación propia
cuando la persona lo revisa, adapta, prueba e integra, y sobre todo cuando puede
explicarlo. Todo el uso relevante está registrado en `docs/uso-ia.md`, con qué se usó, qué
se incorporó, qué se cambió y cómo se comprobó.

**«¿Cuánto pesa la APK y por qué?»**
La de entrega es la build de `release` con R8 y firma: 6,7 MB. Sin las fotos eran 3,3 MB;
las 20 fotografías del catálogo a 1080×1080 quality 82 añaden unos 3,4 MB. La versión de
`debug` pesa 27,5 MB porque lleva las herramientas de Compose y no ofusca.

---

## 7. Checklist 30 minutos antes

- [ ] `./gradlew :app:testDebugUnitTest :app:assembleRelease` en verde (o usar la APK ya generada).
- [ ] `REMY_MARIA_ENTREGA4.apk` instalada y funcionando en el móvil o emulador.
- [ ] Cuenta de la demo a mano: `demo@remy.app` / `Demo1234!`.
- [ ] El emulador con los datos ya sembrados (13 alimentos) para que las sugerencias salgan.
- [ ] Las 7 capturas de `capturas-pantallas/` abiertas por si hay que enseñar alguna.
- [ ] `docs/pruebas.md`, `docs/revision-diseno-usabilidad.md` y `docs/entrega-4.md` a mano por si te piden el detalle.
- [ ] Reparto de los 10 minutos apuntado y los dos con el mismo criterio de quién entra en cada minuto.
- [ ] Cada uno con el suyo: María el descuento, los macros, las fotos, la autenticación y el calendario. Mario el diseño, el modelo E-R y el plan semanal.

---

## 8. Tres cosas que no conviene hacer

1. **No digas que algo funciona sin haberlo mirado.** Si te preguntan por una pantalla que no has abierto hoy, es mejor que lo digas y lo compruebes delante que improvisar una respuesta.
2. **No vendas por encima de lo implementado.** El enunciado avisa de que no se añadan funcionalidades grandes: lo que suma es que lo que hay **funcione y se pueda explicar**.
3. **No repartas el turno por turnos de palabra.** Repartid por bloques (una persona lleva la demo de datos, la otra la de diseño) y compartid las preguntas: si te hacen una pregunta a tu compañero, déjasele contestarla tú.
