<p align="center">
  <img src="docs/diseno/remy-logo-gris.png" alt="Logotipo de REMY" width="170">
</p>

<h1 align="center">REMY</h1>

<p align="center"><strong>Cocina con lo que ya tienes en casa.</strong></p>

<p align="center">
  Aplicación Android que sugiere recetas a partir de los ingredientes de tu despensa,<br>
  te ayuda a planificar y guarda lo que cocinas.
</p>

<p align="center">
  <a href="#qué-hace">Qué hace</a> ·
  <a href="#capturas">Capturas</a> ·
  <a href="#probar-la-app">Probar la app</a> ·
  <a href="#ejecutar-desde-el-código">Ejecutar desde el código</a> ·
  <a href="#tecnologías">Tecnologías</a> ·
  <a href="#estructura-del-proyecto">Estructura</a>
</p>

---

## Qué hace

REMY parte de la despensa real para resolver una pregunta cotidiana: *¿qué puedo cocinar con lo que ya tengo?*

- **Despensa:** guarda alimentos con cantidad y unidad, con alta, edición y borrado.
- **Recetas y sugerencias:** catálogo de recetas con ingredientes, pasos y valores nutricionales. Sugiere qué puedes cocinar ya y qué te queda casi listo, indicando lo que falta.
- **Filtros:** por tipo de plato, tiempo, calorías, proteínas, región, faltantes y macros por ración. Los filtros se guardan y se pueden restablecer.
- **Planificación:** organiza comidas por día (desayuno, comida y cena) en la semana.
- **Cocinar:** marca una receta como cocinada, añade una foto (cámara o galería) y descuenta de la despensa lo utilizado.
- **Cuenta:** inicio de sesión y registro.

## Capturas

<table>
  <tr>
    <td align="center"><img src="docs/imagenes/inicio.png" width="220" alt="Inicio"><br><sub>Inicio</sub></td>
    <td align="center"><img src="docs/imagenes/recetas.png" width="220" alt="Recetas"><br><sub>Recetas</sub></td>
    <td align="center"><img src="docs/imagenes/detalle.png" width="220" alt="Detalle de receta"><br><sub>Detalle</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/imagenes/sugerencias.png" width="220" alt="Sugerencias"><br><sub>Sugerencias</sub></td>
    <td align="center"><img src="docs/imagenes/despensa.png" width="220" alt="Despensa"><br><sub>Despensa</sub></td>
    <td align="center"><img src="docs/imagenes/perfil.png" width="220" alt="Perfil"><br><sub>Perfil</sub></td>
  </tr>
</table>

## Probar la app

### APK

En la carpeta [`entrega/`](entrega/) hay una APK de release lista para instalar:

```bash
adb install -r entrega/REMY_MARIA_ENTREGA4.apk
```

También puedes copiar el archivo al móvil e instalarlo desde el explorador de archivos.

> Cuenta de demostración (si está disponible en el entorno configurado): `demo@remy.app` / `Demo1234!`

### Ejecutar desde el código

**Requisitos:** Android Studio reciente, Android SDK, JDK (el JBR incluido con Android Studio funciona) y un emulador o dispositivo con `minSdk` 26+.

```bash
git clone https://github.com/mariahts/remy.git
cd remy
```

Abre la carpeta en Android Studio, espera a la sincronización de Gradle y ejecuta el módulo `app`, o compila desde terminal:

```bash
# Windows (PowerShell)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat :app:assembleDebug

# macOS/Linux
./gradlew :app:assembleDebug
```

La app incluye la configuración necesaria para compilar y ejecutarse con el backend configurado por el proyecto (clave pública de cliente). Si quieres usar tu propio backend, puedes sobreescribir `SUPABASE_URL` y `SUPABASE_ANON_KEY` en `local.properties` (no versionado).

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje y UI | Kotlin, Jetpack Compose (Material 3) |
| Navegación | Navigation Compose |
| Datos locales | Room, DataStore |
| Imágenes y cámara | CameraX, Coil |
| Autenticación | Supabase Auth |
| Tipografía | Nunito (Google Fonts) |

## Estructura del proyecto

Código principal en `app/src/main/java/com/remy/app/`:

- **`data/`**: acceso a datos (Room: entidades, DAO, base de datos y migraciones), repositorios, precarga/local y cliente remoto.
- **`domain/`**: lógica de negocio (sugerencias, filtros, descuento de despensa, utilidades) y modelos de dominio.
- **`ui/`**: pantallas (`screens`), componentes (`components`), navegación (`navigation`), estado (`state`), ViewModels (`viewmodel`) y tema (`theme`).

Otros recursos relevantes:

- `app/src/main/assets/recetas.json`: catálogo de recetas e ingredientes.
- `app/schemas/`: esquemas exportados de Room.
- `docs/`: documentación del proyecto y material de diseño.

## Créditos y recursos

- Las fotografías del catálogo de recetas y sus licencias/atribuciones están documentadas en [`docs/fotos-recetas.md`](docs/fotos-recetas.md).
- Proyecto desarrollado en el contexto del módulo de PMDM (2.º DAM).

## Estado

Repositorio activo. Para detalles de entregas, pruebas y documentación adicional, consulta la carpeta [`docs/`](docs/).
