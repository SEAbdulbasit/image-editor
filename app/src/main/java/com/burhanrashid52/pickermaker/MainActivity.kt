package com.example.stickermaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.burhanrashid52.photoediting.R
import com.burhanrashid52.photoediting.databinding.ActivityMainBinding
import com.burhanrashid52.pickermaker.DirectoryActivity
import com.burhanrashid52.pickermaker.PARENT_DIRECTORY
import java.io.File


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var storageManager: FilesManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageManager = FilesManager()

        val parentDir = storageManager.getParentDirectory(this)

        binding.saveButton.setOnClickListener {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_layout, null)
        builder.setView(dialogView)

        val editText1 = dialogView.findViewById(R.id.editText1) as EditText
        val editText2 = dialogView.findViewById(R.id.editText2) as EditText

        builder.setPositiveButton("Ok") { _, _ ->
            val text1 = editText1.text.toString()
            val text2 = editText2.text.toString()
            // Do something with the input
            val directoryName = "$text1 - $text2"
            storageManager.createDirectory(directoryName, parentDir)

            // Update the UI to display the new directory
            updateDirectoryList()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        }

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val selectedDirectory = binding.listView.getItemAtPosition(position) as String
            // Do something with the selected directory, such as opening it in a new activity
            val intent = Intent(this, DirectoryActivity::class.java)
            intent.putExtra(PARENT_DIRECTORY, File(parentDir, selectedDirectory))
            startActivity(intent)
        }
        updateDirectoryList()

    }
    private fun updateDirectoryList() {
        // Get a reference to the directory list view
        val listView = binding.listView

        // Get the list of directories from your storage manager
        val directories = storageManager.getDirectories(this)

        // Create an adapter to display the directories in the list view
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, directories)

        // Set the adapter on the list view
        listView.adapter = adapter
    }
}