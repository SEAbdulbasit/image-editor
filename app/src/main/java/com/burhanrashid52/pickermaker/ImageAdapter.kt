package com.burhanrashid52.pickermaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.burhanrashid52.photoediting.R
import java.io.File

class ImageAdapter(var imageFiles: List<File>) :
    RecyclerView.Adapter<ImageAdapter.ImageItemViewHolder>() {
    class ImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(imageFile: File) {
            Glide.with(itemView)
                .load(imageFile)
                .into(imageView)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_items, parent, false)
        return ImageItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val imageFile = imageFiles[position]
        holder.bind(imageFile)
    }

    override fun getItemCount(): Int {
        return imageFiles.size
    }
}