package com.burhanrashid52.pickermaker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.burhanrashid52.photoediting.EditImageActivity
import com.burhanrashid52.photoediting.databinding.ActivityImageEditorBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ImageEditorActivity : AppCompatActivity() {


    private lateinit var binding: ActivityImageEditorBinding
    private lateinit var imageUri: Uri

    private lateinit var originalBitmap: Bitmap
    private var isFlippedVertically = false
    private var isFlipped = false
    private var isCropping = false
    private var rotationAngle = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = Uri.parse(intent.getStringExtra(IMAGE_BITMAP))

        originalBitmap =
            MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, imageUri)
                .rotate(90f)
        binding.imageView.setImageBitmap(originalBitmap)


        binding.flipRightLeft.setOnClickListener {
            originalBitmap = originalBitmap.flipHorizontally()
            binding.imageView.setImageBitmap(originalBitmap)
        }

        binding.flipUpDown.setOnClickListener {
            originalBitmap = originalBitmap.flipVertically()
            binding.imageView.setImageBitmap(originalBitmap)
        }

        binding.rotate.setOnClickListener {
            rotationAngle += 90.0F
            binding.imageView.rotation = rotationAngle

        }

        binding.goNext.setOnClickListener {
            val editImageIntent = Intent(this, EditImageActivity::class.java)
            editImageIntent.putExtra(IMAGE_BITMAP, imageUri.toString())
            startActivity(editImageIntent)


        }
    }

    private fun getVisibleRect(imageView: ImageView): Rect {
        val rect = Rect()
        imageView.getGlobalVisibleRect(rect)
        val imageViewLocation = IntArray(2)
        imageView.getLocationOnScreen(imageViewLocation)
        rect.offset(-imageViewLocation[0], -imageViewLocation[1])
        return rect
    }


    private fun getBitmapFromFile(filePath: String): Bitmap {
        val file = File(filePath)
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveImageToFile(bitmap: Bitmap, filePath: String) {
        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    // To flip horizontally:
    fun Bitmap.flipHorizontally(): Bitmap {
        val matrix = Matrix().apply { postScale(-1f, 1f, width / 2f, height / 2f) }
        val os: OutputStream? = applicationContext.contentResolver.openOutputStream(imageUri)
        val bmp = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
        return bmp
    }

    // To flip vertically:
    fun Bitmap.flipVertically(): Bitmap {
        val matrix = Matrix().apply { postScale(1f, -1f, width / 2f, height / 2f) }
        val os: OutputStream? = applicationContext.contentResolver.openOutputStream(imageUri)
        val bmp = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
        return bmp
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        val bmp = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        val os: OutputStream? = applicationContext.contentResolver.openOutputStream(imageUri)
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
        return bmp
    }
}


