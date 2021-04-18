package unicon.code.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unicon.code.adapter.FileMangerAdapter
import java.io.File

class FileManagerView(context: Context, var attrs: AttributeSet) : RecyclerView(context, attrs) {
    private var openFileListener: ((file: File) -> Unit)? = null

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = FileMangerAdapter(this)
    }

    fun getFileManagerAdapter() : FileMangerAdapter {
        return (adapter as FileMangerAdapter)
    }

    fun onItemClick(pos: Int) {
        val file = getFileManagerAdapter().getItem(pos)

        if(file.isDirectory)
            openDir(file)

        if(file.isFile && openFileListener != null)
            openFileListener!!.invoke(file)
    }

    fun openDir(path: File) {
        getFileManagerAdapter().openDir(path)
    }

    fun setOnOpenFileListener(lam: (file: File) -> Unit) {
        openFileListener = lam
    }
}