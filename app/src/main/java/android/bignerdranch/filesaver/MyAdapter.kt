package android.bignerdranch.filesaver

import android.annotation.SuppressLint
import android.bignerdranch.filesaver.databinding.FileItemBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File

@SuppressLint("SdCardPath")
private const val HOME_PATH = "/data/data/android.bignerdranch.filesaver/SavedFiles"

class MyAdapter: RecyclerView.Adapter<MyAdapter.FileHolder>() {
    private val fileList = ArrayList<SavedFile>()
    private val directory = File(HOME_PATH)

    class FileHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = FileItemBinding.bind(item)
        fun bind(file: SavedFile) {
            binding.fileNameTV.text = file.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.file_item, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        holder.bind(fileList[position])
    }

    override fun getItemCount(): Int = fileList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addFile(filePath: String) {
        val file = SavedFile(filePath
            .split('/')
            .last())
        var check = false

        fileList.forEach {
            if (it.name == file.name) check = true
        }

        if (!check) {
            fileList.add(file)
            try {
                File(filePath).copyTo(File(HOME_PATH, file.name), true)
            } catch (e: Exception) {
                println("Wops, I've catch an exception... $e")
            }
            notifyDataSetChanged()
        }
    }

    fun generateList() {
        try {
            directory.walk().forEach {
                fileList
                    .add(SavedFile(it
                        .path
                        .split('/')
                        .last()))
            }
            fileList.removeAt(0)
        } catch(e: Exception) {
            println(e)
        }
    }

    fun deleteFile(position: Int) {
        val name = fileList[position].name
        fileList.removeAt(position)
        File("$HOME_PATH/$name").delete()
    }

    fun isEmpty() = fileList.isEmpty()
}