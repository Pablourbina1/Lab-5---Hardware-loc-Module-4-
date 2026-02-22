# Granular Error Handling
## Parte 1 del laboratorio
#### Link del video de explicación: https://drive.google.com/file/d/1lvZWuOo3EVmQKhEGNSU_gEKLB38Z80AO/view?usp=sharing
En esta parte se modifico el proyecto para que al momento de que ocurra un error al capturar una foto, el programa de al usuario un mensaje descriptivo sobre el error, a la vez de que
la aplicación no se crashee en caso haya un error.

## Definition of Done

| Criterio | Descripción |
|----------|------------|
| Clase sellada existente | Existe una clase sellada `CaptureError` con 3 o más tipos de error. |
| Errores mapeados | Los códigos de `ImageCaptureException.imageCaptureError` están mapeados a la clase sellada. |
| Repositorio actualizado | `CreateSpotResult` incluye el nuevo caso `PhotoCaptureFailed`. |
| La UI muestra mensajes específicos | Cada tipo de error muestra un mensaje diferente y amigable para el usuario. |
| Sin crasheos | La aplicación maneja todos los casos de error correctamente. |
