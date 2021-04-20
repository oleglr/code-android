package unicon.code.plugin

import android.content.Context
import com.squareup.duktape.Duktape
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset

class PluginsLoader(ctx: Context) {
    private val duktape = Duktape.create()
    public val plugins = ArrayList<Plugin>()

    init {
        duktape.set("API", API::class.java, object: API {
            override fun outln(msg: String) {
                out("$msg\n")
            }

            override fun out(msg: String) {
                println(msg)
            }

        })
    }

    private var pluginLoadedListener: ((pl: Plugin) -> Unit)? = null

    fun loadPlugins(dir: File) {
        dir.listFiles().forEach { // проходимся по всем файлам
            var isLoaded = false
            var error = ""

            try {
                val initOutput = duktape.evaluate(it.readText(Charset.defaultCharset()))
                var base = duktape.get(dir.name.replace(".js", ""), PluginBase::class.java) as PluginBase
                base.initPlugin()

                isLoaded = true
            } catch (e: Exception) {
                error = e.toString()
                println("Exception in PluginLoader: $e")
            }

            val pl = Plugin(it.name, it, isLoaded, error)

            plugins.add(pl)
            if(pluginLoadedListener != null) pluginLoadedListener!!.invoke(pl)
        }
    }

    fun setOnPluginLoadedListener(lam: ((pl: Plugin) -> Unit)?) {
        pluginLoadedListener = lam
    }
}

interface API {
    open fun outln(msg: String)
    open fun out(msg: String)
}

interface PluginBase {
    fun initPlugin()
}