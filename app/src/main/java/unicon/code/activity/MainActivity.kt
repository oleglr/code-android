package unicon.code.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import unicon.code.R
import unicon.code.hideKeyboardFrom
import unicon.code.widget.FileManagerView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var code_content: FrameLayout
    private lateinit var menu_btn: ImageView
    private lateinit var file_manger: FileManagerView

    private val REQUEST_CODE_PERMISSIONS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)

        drawer_layout = findViewById(R.id.drawer_layout)
        code_content = findViewById(R.id.code_content)
        menu_btn = findViewById(R.id.menu_btn)
        file_manger = findViewById(R.id.file_manger)

        setupFileManager()
        setupDrawer()

        // проверка разрешений
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startApp()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSIONS
            )

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_PERMISSIONS -> { // если выданы права
                startApp()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        var adapter = file_manger.getFileManagerAdapter()

        if(adapter.currentDir != File(Environment.getExternalStorageDirectory().path)) {
            var parent = File(file_manger.getFileManagerAdapter().currentDir.absoluteFile.parent)

            adapter.openDir(parent)
        } else {
            super.onBackPressed()
        }
    }

    /* продолжение onCreate */
    fun startApp() {

    }

    /* настройка менеджера файлов */
    fun setupFileManager() {
        file_manger.openDir(File(Environment.getExternalStorageDirectory().path))
        file_manger.setOnOpenFileListener {

        }
    }

    /* настройка меню */
    fun setupDrawer() {
        drawer_layout.setScrimColor(Color.TRANSPARENT)
        drawer_layout.drawerElevation = 0f
        drawer_layout.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                code_content.translationX = (drawerView.width * slideOffset)
            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerOpened(drawerView: View) {
                hideKeyboardFrom(drawerView)
            }

        })

        menu_btn.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }
}