package unicon.code.plugin

import com.squareup.duktape.Duktape
import java.io.File

class Plugin(val name: String, val file: File, var isLoaded: Boolean, var error: String, val duktape: Duktape) {
    fun initPlugin() {
        duktape.evaluate("initPlugin();")
    }

    fun openFile(file: File) : Boolean {
        return (duktape.evaluate("openFile('${file.path}');") as Boolean)
    }
}