# Spot Deletion Feature
## Parte 2 del laboratorio
#### Link del video de explicación: https://drive.google.com/file/d/1VsIWcwQbAR56l52SY5W28FOBl6oOHzDk/view?usp=drive_link

En esta parte se añadio una opcion para eliminar un spot de forma de un icono de basura, el cual al confirmar de que se quiere eliminar el spot, se elimina tanto de la aplicación, de la
base de datos, y su imagen se elimina del dispositivo, eliminando por completo todo el rastro del spot eliminado.

## Definition of Done

| Criterio | Descripción |
|----------|------------|
| Método DAO existente | Existe el método `deleteSpot(id: Long)` definido como query en SpotDao. |
| Coordinación del repositorio | Rlimina el registro de la base de datos Y también elimina el archivo de la foto. |
| Pulsación prolongada activa diálogo | La ventana de información del marcador responde a una pulsación prolongada. |
| Diálogo de confirmación | Un AlertDialog pregunta "¿Estás seguro?" antes de realizar la eliminación. |
| Limpieza de archivo | El archivo de la foto se elimina del `filesDir`. |
| Actualización automática de la UI | El mapa elimina el marcador después de la eliminación (reactividad mediante Flow). |
| Casos límite manejados | Se maneja correctamente el caso en que el archivo no exista. |
