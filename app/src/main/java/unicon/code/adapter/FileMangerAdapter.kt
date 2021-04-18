package unicon.code.adapter

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import unicon.code.R
import unicon.code.widget.FileManagerView
import java.io.File
import java.nio.file.Path

class FileMangerAdapter(var fmview: FileManagerView) : RecyclerView.Adapter<FileMangerAdapter.FileManagerViewHolder>(){

    class FileManagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var icon: ImageView? = null
        var name: TextView? = null

        init {
            icon = itemView.findViewById(R.id.icon)
            name = itemView.findViewById(R.id.name)
        }
    }

    var currentDir = File(Environment.getExternalStorageDirectory().path)
    private var fileList = ArrayList<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileManagerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_file, parent, false)
        return FileManagerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return fileList.count()
    }

    override fun onBindViewHolder(holder: FileManagerViewHolder, position: Int) {
        val file = fileList[position]

        holder.name!!.text = file.name

        holder.itemView.setOnClickListener {
            val pos = fmview.getChildLayoutPosition(it)

            fmview.onItemClick(pos)
        }
    }

    fun getItem(pos: Int) : File {
        return fileList[pos]
    }

    fun openDir(path: File) {
        currentDir = path
        fileList = path.listFiles().toCollection(ArrayList())

        notifyDataSetChanged()
    }
}