package io.github.gmathi.novellibrary.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import io.github.gmathi.novellibrary.BuildConfig
import io.github.gmathi.novellibrary.R
import io.github.gmathi.novellibrary.dataCenter
import io.github.gmathi.novellibrary.fragment.DownloadFragment
import io.github.gmathi.novellibrary.fragment.LibraryFragment
import io.github.gmathi.novellibrary.fragment.SearchFragment
import io.github.gmathi.novellibrary.util.Constants
import kotlinx.android.synthetic.main.activity_nav_drawer.*
import kotlinx.android.synthetic.main.app_bar_nav_drawer.*
import org.cryse.widget.persistentsearch.PersistentSearchView

class NavDrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    var snackBar: Snackbar? = null
    var currentNavId: Int = if (dataCenter.loadLibraryScreen) R.id.nav_library else R.id.nav_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)
        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState != null) {
            currentNavId = savedInstanceState.getInt("currentNavId")
        }


        loadFragment(currentNavId)
        if (dataCenter.appVersionCode < BuildConfig.VERSION_CODE) {
            MaterialDialog.Builder(this)
                .title("Change Log")
                .customView(R.layout.dialog_change_log, true)
                .show()
            dataCenter.appVersionCode = BuildConfig.VERSION_CODE
        }
        snackBar = Snackbar.make(navFragmentContainer, getString(R.string.app_exit), Snackbar.LENGTH_SHORT)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val existingSearchFrag = supportFragmentManager.findFragmentByTag(SearchFragment::class.toString())
            if (existingSearchFrag != null) {
                val searchView = existingSearchFrag.view?.findViewById<PersistentSearchView>(R.id.searchView)
                if (searchView != null && (searchView.isEditing || searchView.isSearching)) {
                    searchView.closeSearch()
                    return
                }
            }
            val existingDownloadFrag = supportFragmentManager.findFragmentByTag(DownloadFragment::class.toString())
            if (existingDownloadFrag != null) {
                loadFragment(R.id.nav_library)
                return
            }

            if (snackBar != null && snackBar!!.isShown)
                finish()
            else
                snackBar?.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        loadFragment(item.itemId)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun loadFragment(id: Int) {
        currentNavId = id
        when (id) {
            R.id.nav_library -> {
                replaceFragment(LibraryFragment(), LibraryFragment::class.toString())
            }
            R.id.nav_search -> {
                replaceFragment(SearchFragment(), SearchFragment::class.toString())
            }
            R.id.nav_downloads -> {
                replaceFragment(DownloadFragment(), DownloadFragment::class.toString())
            }
            R.id.nav_settings -> {
                startSettingsActivity()
            }
        }
    }


    fun replaceFragment(fragment: Fragment, tag: String) {
        val existingFrag = supportFragmentManager.findFragmentByTag(tag)
        var replaceFrag = fragment
        if (existingFrag != null) {
            replaceFrag = existingFrag
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.navFragmentContainer, replaceFrag, tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun setToolbar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_vector)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Constants.OPEN_DOWNLOADS_RES_CODE) {
            loadFragment(R.id.nav_downloads)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("currentNavId", currentNavId)
    }


}
