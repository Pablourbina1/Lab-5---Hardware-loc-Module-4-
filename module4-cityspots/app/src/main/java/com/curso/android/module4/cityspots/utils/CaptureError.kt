package com.curso.android.module4.cityspots.utils

import androidx.camera.core.ImageCaptureException

sealed class CaptureError {
    //Camera was closed unexpectedly
    data object CameraClosed : CaptureError()
    //Capture failed (hardware issue)
    data object CaptureFailed : CaptureError()
    //File I/O error (storage full or permissions)
    data object FileIOError : CaptureError()
    //Invalid Camera Error
    data object InvalidCamera : CaptureError()
    //Error desconocido
    data class Unknown(val exception: ImageCaptureException) : CaptureError()
}