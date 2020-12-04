package hr.foi.air2003.menzapp.assistants

import android.graphics.Bitmap
import android.graphics.BitmapFactory

object ImageConverter {
    fun convertBytesToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}