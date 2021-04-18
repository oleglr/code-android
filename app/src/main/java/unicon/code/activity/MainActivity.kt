package unicon.code.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import unicon.code.R

class MainActivity : AppCompatActivity() {
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var code_content: FrameLayout
    private lateinit var menu_btn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)

        drawer_layout = findViewById(R.id.drawer_layout)
        code_content = findViewById(R.id.code_content)
        menu_btn = findViewById(R.id.menu_btn)

        setupDrawer()
    }

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

            }

        })

        menu_btn.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }
}