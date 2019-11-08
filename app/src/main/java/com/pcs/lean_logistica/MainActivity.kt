package com.pcs.lean_logistica

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.pcs.lean_logistica.fragment.*
import com.pcs.lean_logistica.model.Download
import com.pcs.lean_logistica.model.Upload
import com.pcs.lean_logistica.tools.Cache
import com.pcs.lean_logistica.tools.Prefs
import com.pcs.lean_logistica.tools.Utils

const val ACTION_BUTTON_MENU: Int = 0
const val ACTION_BUTTON_ARROW: Int = 1

const val EMPTY_FRAGMENT: Int = 0
const val FRAGMENT_DOWN_LIST: Int = 1
const val FRAGMENT_UP_LIST: Int  = 2
const val FRAGMENT_SETTINGS: Int = 3
const val FRAGMENT_DOWNLOAD_FORM: Int = 4
const val FRAGMENT_SELECT_PROVIDER: Int = 5
const val FRAGMENT_UPLOAD_FORM: Int = 6


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var idApp: Int = 0

    private var currentFragment: Int = FRAGMENT_DOWN_LIST
    private var altCurrentFragment: Int = EMPTY_FRAGMENT

    private lateinit var drawerLayout: DrawerLayout

    /**SHARE PARAMS*********************/
    lateinit var download: Download
    lateinit var listDownload: MutableList<Download>

    lateinit var upload: Upload
    lateinit var listUpload: MutableList<Upload>

    lateinit var currentAdapter: Any

    var cache: Cache = Cache(flushInterval = 5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ONLY PORTRAIT OR LANDSCAPE
        this.requestedOrientation = when(resources.getBoolean(R.bool.portrait_only)){
            true -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            false -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        createID()

        initView()

        listDownload = ArrayList()
        listUpload = ArrayList()

    }

    private fun createID(){
        val prefs = Prefs(this)
        idApp = prefs.idApp
        if(idApp==0){
            idApp =  (1..1000000).shuffled().first()
            prefs.idApp = idApp
        }
        Log.d("ID_APP", idApp.toString())
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
            if(!Utils.isDoubleFragment(this)){
                //Solo se dispone de un fragmento
            }
            else{
                if(altCurrentFragment == FRAGMENT_SELECT_PROVIDER)
                    navigateToNewDownload()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_down -> {
                this.listDownload.clear() //Obligo a recargarse
                navigateToDown()
            }
            R.id.nav_up -> {
                this.listUpload.clear() //Obligo a recargarse
                navigateToUp()
            }
            R.id.nav_settings -> {
                navigateToSettings()
            }
        }
        cleanFragment()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigateToHome(){
        navigateToDown()
    }

    private fun navigateToDown(){
        changeActionBarButton(ACTION_BUTTON_MENU)
        this.title = "Descargas"
        this.currentFragment = FRAGMENT_DOWN_LIST
        navigateToFragment(DownloadFragment(), R.id.fragment_container1)
    }

    private fun navigateToUp(){
        changeActionBarButton(ACTION_BUTTON_MENU)
        this.title = "Cargas"
        this.currentFragment = FRAGMENT_UP_LIST
        navigateToFragment(UploadFragment(), R.id.fragment_container1)
    }

    private fun navigateToSettings(){
        changeActionBarButton(ACTION_BUTTON_MENU)
        this.title = "Configuración"
        this.currentFragment = FRAGMENT_SETTINGS
        navigateToFragment(SettingFragment(), R.id.fragment_container1)
    }

    fun navigateToNewDownload(){
        if(!Utils.isDoubleFragment(this)){
            //Solo se dispone de un fragmento
        }
        else{
            this.altCurrentFragment = FRAGMENT_DOWNLOAD_FORM
            navigateToFragment(DownloadFormFragment(), R.id.fragment_container2)
        }
    }

    fun navigateToNewUpload(){
        if(!Utils.isDoubleFragment(this)){
            //Solo se dispone de un fragmento
        }
        else{
            this.altCurrentFragment = FRAGMENT_UPLOAD_FORM
            navigateToFragment(UploadFormFragment(), R.id.fragment_container2)
        }
    }

    fun navigateToSelectProvider(){
        if(!Utils.isDoubleFragment(this)){

        }
        else{
            this.altCurrentFragment =  FRAGMENT_SELECT_PROVIDER
            navigateToFragment(SelectProviderFragment(), R.id.fragment_container2)
        }
    }

    fun cleanFragment(){
        if(Utils.isDoubleFragment(this)) {
            val layout: LinearLayout = findViewById(R.id.fragment_container2)
            layout.removeAllViews()
        }
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
            0 -> supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
            1 -> supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

    }

}
