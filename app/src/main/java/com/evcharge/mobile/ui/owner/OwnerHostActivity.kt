package com.evcharge.mobile.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.evcharge.mobile.R
import com.evcharge.mobile.security.AuthSession
import com.evcharge.mobile.security.TokenStore
import com.evcharge.mobile.ui.auth.AuthActivity
import com.evcharge.mobile.ui.owner.fragments.MyBookingsFragment
import com.evcharge.mobile.ui.owner.fragments.OwnerHomeFragment
import com.evcharge.mobile.ui.owner.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class OwnerHostActivity : AppCompatActivity() {
    
    private lateinit var authSession: AuthSession
    private lateinit var bottomNavigation: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_host)
        
        authSession = AuthSession(TokenStore(this), com.evcharge.mobile.App.instance.dbHelper)
        
        // Check if logged in
        if (!authSession.isLoggedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        
        setupBottomNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(OwnerHomeFragment())
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(OwnerHomeFragment())
                    true
                }
                R.id.nav_bookings -> {
                    loadFragment(MyBookingsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.owner_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authSession.logout()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
