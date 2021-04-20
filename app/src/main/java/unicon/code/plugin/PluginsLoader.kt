package unicon.code.plugin

import android.content.Context
import com.squareup.duktape.Duktape
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset

class PluginsLoader(var ctx: Context) {
    private var pluginLoadedListener: ((pl: Plugin) -> Unit)? = null

    private val plugins = ArrayList<Plugin>()
    private val apiObject = object: API {
        override fun outln(msg: String) {
            out("$msg\n")
        }

        override fun out(msg: String) {
            print(msg)
        }

    }

    fun loadPlugin(file: File) {
        val plugin = Plugin(
                file.name.replace(".js", ""),
                file,
                false,
                "",
                Duktape.create()
        )

        plugin.duktape.set("API", API::class.java, apiObject)

        try {
            plugin.duktape.evaluate(file.readText(Charset.defaultCharset()))
            plugin.initPlugin()

            plugin.isLoaded = true
        } catch (e: Exception) {
            println("PluginLoader: $e")
            plugin.error = e.toString()
        }

        plugins.add(plugin)

        if(pluginLoadedListener != null)
            pluginLoadedListener!!.invoke(plugin)
    }

    fun loadPluginsFromDir(dir: File) {
        dir.listFiles().forEach {
            if(it.name.endsWith(".js")) loadPlugin(it)
        }
    }

    fun getPlugins() : ArrayList<Plugin> {
        return plugins
    }

    fun setOnPluginLoadedListener(lam: (pl: Plugin) -> Unit) {
        pluginLoadedListener = lam
    }
}

interface API {
    fun outln(msg: String)
    fun out(msg: String)
}