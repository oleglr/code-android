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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import unicon.code.R
import unicon.code.hideKeyboardFrom
import unicon.code.widget.CodeEditor
import unicon.code.widget.FileManagerView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var code_editor: CodeEditor
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var code_content: FrameLayout
    private lateinit var menu_btn: ImageView
    private lateinit var more_btn: ImageView
    private lateinit var file_manger: FileManagerView
    private lateinit var mark_path: TextView
    private lateinit var btn_newfile: LinearLayout
    private lateinit var btn_newdir: LinearLayout
    private lateinit var btn_backdir: LinearLayout

    private val REQUEST_CODE_PERMISSIONS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)

        code_editor = findViewById(R.id.code_editor)
        drawer_layout = findViewById(R.id.drawer_layout)
        code_content = findViewById(R.id.code_content)
        menu_btn = findViewById(R.id.menu_btn)
        more_btn = findViewById(R.id.more_btn)
        file_manger = findViewById(R.id.file_manger)
        mark_path = findViewById(R.id.mark_path)
        btn_newfile = findViewById(R.id.btn_newfile)
        btn_newdir = findViewById(R.id.btn_newdir)
        btn_backdir = findViewById(R.id.btn_backdir)

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
        if(!drawer_layout.isDrawerOpen(GravityCompat.START)) super.onBackPressed()

        val adapter = file_manger.getFileManagerAdapter()

        if(adapter.currentDir != File(Environment.getExternalStorageDirectory().path)) {
            file_manger.prevDir()
        } else {
            if(code_editor.isSaved()) super.onBackPressed()
            else {
                showSaveWarningDialog {
                    when(it) {
                        true -> {
                            code_editor.saveFile()
                            super.onBackPressed()
                        }
                    }
                }
            }
        }
    }

    /* продолжение onCreate */
    fun startApp() {
        setupFileManager()
        setupDrawer()
        setupFileManagerButtons()
        setupCodeEditor()
    }

    /* настроить редактор кода */
    fun setupCodeEditor() {
        code_editor.openFile(File(Environment.getExternalStorageDirectory().path + "/temp.txt"))
        code_editor.setOnOpenFileListener {
        }
        code_editor.setOnSaveFileListener {
            file_manger.reopen()
        }
    }

    /* настройка кнопок в менеджере файлов */
    fun setupFileManagerButtons() {
        val adapter = file_manger.getFileManagerAdapter()

        btn_newfile.setOnClickListener {
            MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(-1, "Создать новый файл")
                input(hint = "Введите имя файла")

                positiveButton(-1, "Создать")
                positiveButton {
                    File(file_manger.getCurrentDir().path + "/" + getInputField().text)
                        .createNewFile()

                    file_manger.reopen()
                }

                negativeButton(-1, "Отмена")
            }
        }

        btn_newdir.setOnClickListener {
            MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(-1, "Создать новую папку")
                input(hint = "Введите имя папки")

                positiveButton(-1, "Создать")
                positiveButton {
                    File(file_manger.getCurrentDir().path + "/" + getInputField().text)
                        .mkdir()

                    file_manger.reopen()
                }

                negativeButton(-1, "Отмена")
            }
        }

        btn_backdir.setOnClickListener {
            if(adapter.currentDir != File(Environment.getExternalStorageDirectory().path)) {
                file_manger.prevDir()
            }
        }
    }

    /* настройка менеджера файлов */
    fun setupFileManager() {
        file_manger.setOnOpenFileListener { file ->
            drawer_layout.closeDrawer(GravityCompat.START)

            if(code_editor.getCurrentFile() != null)
                if(file.path == code_editor.getCurrentFile()!!.path)
                    return@setOnOpenFileListener

            if(code_editor.isSaved()) {
                code_editor.openFile(file)
            } else {
                showSaveWarningDialog {
                    if(it) code_editor.saveFile()

                    code_editor.openFile(file)
                }
            }
        }

        file_manger.setOnChangeDirListener {
            mark_path.text = it.path
        }

        file_manger.openDir(File(Environment.getExternalStorageDirectory().path))
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

    fun showSaveWarningDialog(lam: (isSave: Boolean) -> Unit) {
        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(-1, "Изменения не сохранены!")
            message(-1, "Вы хотите сохранить файл?")

            positiveButton(-1, "Сохранить")
            positiveButton {
                lam(true)
            }

            negativeButton(-1, "Не сохранять")
            negativeButton {
                lam(false)
            }
        }
    }
}