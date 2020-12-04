package hr.foi.air2003.menzapp.assistants

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView

object ImageConverter {
    fun convertBytesToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun resizeBitmap(bitmap: Bitmap, view: ImageView) : Bitmap{
        return Bitmap.createScaledBitmap(bitmap, view.width, view.height, true)
    }
}