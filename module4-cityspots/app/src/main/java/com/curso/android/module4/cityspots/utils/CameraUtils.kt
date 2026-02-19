package com.curso.android.module4.cityspots.utils

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * =============================================================================
 * CameraUtils - Helper para operaciones de cámara y archivos
 * =============================================================================
 *
 * CONCEPTO: Almacenamiento de Archivos en Android
 * Hay varias opciones para guardar archivos:
 *
 * 1. Internal Storage (context.filesDir)
 *    - Privado a la app, no accesible por otras apps
 *    - Se elimina cuando se desinstala la app
 *    - No requiere permisos
 *    ✓ USAMOS ESTA OPCIÓN para fotos de la app
 *
 * 2. External Storage (Environment.getExternalStorageDirectory())
 *    - Accesible por otras apps (con permisos)
 *    - Persiste después de desinstalar
 *    - Requiere READ/WRITE_EXTERNAL_STORAGE (pre-Android 10)
 *
 * 3. Scoped Storage (MediaStore) - Android 10+
 *    - Acceso estructurado a media del sistema
 *    - No requiere permisos para media propia
 *    - Bueno para fotos compartidas con otras apps
 *
 * =============================================================================
 */
class CameraUtils(private val context: Context) {

    // Formato para nombres de archivo únicos basados en timestamp
    private val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    /**
     * Crea un archivo temporal para guardar una imagen
     *
     * El nombre del archivo incluye un timestamp para evitar colisiones:
     * Ejemplo: spot_20241206_143052.jpg
     *
     * @return File en el directorio interno de la app
     */
    fun createImageFile(): File {
        // Usar filesDir para almacenamiento interno privado
        val storageDir = context.filesDir

        // Generar nombre único basado en timestamp
        val timeStamp = fileNameFormat.format(Date())
        val fileName = "spot_$timeStamp.jpg"

        return File(storageDir, fileName)
    }

    /**
     * Captura una foto usando CameraX y la guarda en un archivo
     *
     * CONCEPTO: ImageCapture Use Case de CameraX
     * CameraX usa "Use Cases" para definir qué hacer con la cámara:
     * - Preview: Mostrar vista previa en pantalla
     * - ImageCapture: Capturar fotos
     * - VideoCapture: Grabar video
     * - ImageAnalysis: Procesar frames en tiempo real (ML, QR, etc.)
     *
     * FLUJO DE CAPTURA:
     * 1. Crear archivo de destino
     * 2. Configurar OutputFileOptions con el archivo
     * 3. Llamar takePicture() con un callback
     * 4. Convertir callback a coroutine con suspendCancellableCoroutine
     *
     * @param imageCapture Use case de ImageCapture configurado
     * @return URI del archivo guardado
     * @throws ImageCaptureException si falla la captura
     */
    suspend fun capturePhoto(imageCapture: ImageCapture): Result<Uri> {
        return suspendCancellableCoroutine { continuation ->
            // Crear archivo de destino
            val photoFile = createImageFile()

            // Configurar opciones de salida
            // OutputFileOptions define dónde y cómo guardar la imagen
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            // Ejecutar captura en el executor principal
            // NOTA: La captura real es asíncrona, no bloquea el main thread
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Éxito: retornar URI del archivo
                        val savedUri = Uri.fromFile(photoFile)

                        // Error de IO
                        /*
                        continuation.resume(
                            Result.failure(
                                CaptureErrorException(CaptureError.FileIOError)
                            )
                        )
                         */

                        //Error, se cerro la camara inesperadamente
                        /*
                        continuation.resume(
                            Result.failure(
                                CaptureErrorException(CaptureError.CameraClosed)
                            )
                        )
                         */

                        //Error, la captura de la foto fallo
                        /*
                        continuation.resume(
                            Result.failure(
                                CaptureErrorException(CaptureError.CaptureFailed)
                            )
                        )
                         */

                        //Error, camara invalida
                        /*
                        continuation.resume(
                            Result.failure(
                                CaptureErrorException(CaptureError.InvalidCamera)
                            )
                        )
                         */

                        //Error desconocido
                        /*
                        continuation.resume(
                            Result.failure(
                                CaptureErrorException(CaptureError.Unknown(ImageCaptureException(
                                    ImageCapture.ERROR_UNKNOWN,
                                    "Test unknown error",
                                    null
                                )))
                            )
                        )

                         */

                        // Continuation normal cuando hay exito
                        continuation.resume(Result.success(savedUri))
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Error: propagar excepción
                        // Limpiar archivo si se creó pero falló la escritura
                        photoFile.delete()

                        val captureError = if (
                            exception.imageCaptureError == ImageCapture.ERROR_CAMERA_CLOSED
                            ) {
                            CaptureError.CameraClosed
                        } else if (
                            exception.imageCaptureError == ImageCapture.ERROR_CAPTURE_FAILED
                        ) {
                            CaptureError.CaptureFailed
                        } else if (
                            exception.imageCaptureError == ImageCapture.ERROR_FILE_IO
                        ) {
                            CaptureError.FileIOError
                        } else if (
                            exception.imageCaptureError == ImageCapture.ERROR_INVALID_CAMERA
                        ) {
                            CaptureError.InvalidCamera
                        } else {
                            CaptureError.Unknown(exception)
                        }

                        continuation.resume(
                            Result.failure(
                                CaptureErrorException(captureError)
                            )
                        )
                    }
                }
            )

            // Limpieza si la coroutine se cancela antes de completar
            continuation.invokeOnCancellation {
                // Nota: No podemos cancelar takePicture una vez iniciado
                // pero podemos limpiar el archivo si existe
                if (photoFile.exists()) {
                    photoFile.delete()
                }
            }
        }
    }

    //Clase para agarrar los errores de capture error y resumir con continuation
    class CaptureErrorException(
        val error: CaptureError
    ) : Exception()


    /**
     * Elimina un archivo de imagen dado su URI
     *
     * Útil para limpiar cuando se elimina un Spot de la base de datos
     *
     * @param uri URI del archivo a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    fun deleteImage(uri: Uri): Boolean {
        return try {
            val file = File(uri.path ?: return false)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtiene el tamaño de una imagen en bytes
     *
     * @param uri URI del archivo
     * @return Tamaño en bytes o -1 si no existe
     */
    fun getImageSize(uri: Uri): Long {
        return try {
            val file = File(uri.path ?: return -1)
            file.length()
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Verifica si un archivo de imagen existe
     *
     * @param uri URI del archivo a verificar
     * @return true si existe, false en caso contrario
     */
    fun imageExists(uri: Uri): Boolean {
        return try {
            val file = File(uri.path ?: return false)
            file.exists()
        } catch (e: Exception) {
            false
        }
    }
}
