package com.burhanrashid52.pickermaker

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.burhanrashid52.photoediting.databinding.ActivityDirectoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


const val PARENT_DIRECTORY = "directory_name"
const val IMAGE_BITMAP = "image_bitmap"

class DirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDirectoryBinding

    private lateinit var currentDirectory: File
    lateinit var imageUri: Uri

    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        currentDirectory = intent.getSerializableExtra(PARENT_DIRECTORY) as File

        binding.textView.text = currentDirectory.name

        // Set onClickListener for textView to launch camera or gallery intent
        binding.addImage.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Add Photo")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
//
//                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
//                        val file = File(
//                            Environment.getExternalStorageDirectory(),
//                            "/dummy/photo_" + timeStamp + ".png"
//                        );
//                        imageUri = Uri.fromFile(file);
//
//                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                        openCamera()

                        // Launch camera intent
//                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)


                    }

                    options[item] == "Choose from Gallery" -> {
                        // Launch gallery intent
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY)
                    }

                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val imageFiles = currentDirectory.listFiles { file -> file.extension == "webp" }!!.toList()
        val adapter = ImageAdapter(imageFiles)
        adapter.notifyDataSetChanged()
        binding.recyclerView.adapter = adapter


    }

    lateinit var image_uri: Uri
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {

                REQUEST_IMAGE_CAPTURE -> {
                    binding.imageResult.setImageURI(image_uri);


                    openImageEditor(image_uri)


                }

                REQUEST_IMAGE_FROM_GALLERY -> {
                    // Image selected from gallery
                    //                   val imageUri = data?.data
//                    val imageBitmap =
//                        MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
//                    val stream = ByteArrayOutputStream()
//                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                    val byteArray = stream.toByteArray()
                    openImageEditor(data?.data!!)
                }
//                REQUEST_IMAGE_CAPTURE -> {
//                    // Image captured from camera
//                    val imageBitmap = data?.extras?.get("data") as Bitmap
//                    openImageEditor(imageBitmap)
//
//
//                }
//                REQUEST_IMAGE_FROM_GALLERY -> {
//                    // Image selected from gallery
//                    val imageUri = data?.data
//                    val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
//                    openImageEditor(imageBitmap)
//
//                }
            }
        }
    }

    private fun saveImageAsWebp(imageBitmap: Bitmap): String {
        val imageName = generateImageName()
        val imageFile = File(currentDirectory, imageName)
        coroutineScope.launch {
            FileOutputStream(imageFile).use { outputStream ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    imageBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)
                } else {
                    imageBitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
                }
            }
        }
        return imageFile.absolutePath
    }

    private fun generateImageName(): String {
        val currentTimeMillis = System.currentTimeMillis()
        return "image_$currentTimeMillis.webp"
    }

    private fun openImageEditor(bitmap: Uri) {
        val editImageIntent = Intent(this, ImageEditorActivity::class.java)
        editImageIntent.putExtra(IMAGE_BITMAP, bitmap.toString())
        startActivity(editImageIntent)
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_FROM_GALLERY = 2
    }
}
