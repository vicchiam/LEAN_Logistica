package com.pcs.lean_logistica

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.pcs.lean_logistica.fragment.SettingFragment

const val TAG = "MAIN ACTIVITY"

const val ACTION_BUTTON_MENU: Int = 0
const val ACTION_BUTTON_ARROW: Int = 1

const val FRAGMENT_DOWN_LIST: Int = 1
const val FRAGMENT_UP_LIST: Int  = 2
const val FRAGMENT_SETTINGS: Int = 3


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout

    private var currentFragment: Int = FRAGMENT_DOWN_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ONLY PORTRAIT OR LANDSCAPE
        when(resources.getBoolean(R.bool.portrait_only)){
            true -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            false -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        initView()

    }

    private fun initView(){
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        toolbar.setNavigationOnClickListener{
            if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START)
        }

        navigateToHome()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_down -> {
                navigateToDown()
            }
            R.id.nav_up -> {
                navigateToUp()
            }
            R.id.nav_settings -> {
                navigateToSettings()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun navigateToHome(){
        navigateToDown()
    }

    fun navigateToDown(){
        changeActionBarButton(ACTION_BUTTON_MENU)
        this.title = "Descargas"
        this.currentFragment = FRAGMENT_DOWN_LIST
    }

    fun navigateToUp(){
        changeActionBarButton(ACTION_BUTTON_MENU)
        this.title = "Cargas"
        this.currentFragment = FRAGMENT_UP_LIST
    }

    fun navigateToSettings(){
        changeActionBarButton(ACTION_BUTTON_MENU)
        this.title = "Configuración"
        this.currentFragment = FRAGMENT_SETTINGS
        navigateToFragment(SettingFragment(), R.id.fragment_container1)
    }

    private fun navigateToFragment(fragment: Fragment, @IdRes containerViewId: Int){
        val ft = supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment)
        if (!supportFragmentManager.isStateSaved ){
            ft.commit()
        }
    }

    private fun changeActionBarButton(type: Int){
        when(type){
            0 -> {
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
            }
            1 -> {
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            }
        }

    }

}
