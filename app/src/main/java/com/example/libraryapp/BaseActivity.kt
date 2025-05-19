package com.example.libraryapp

import android.content.Intent
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.models.User
import com.example.user.UserService
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.example.enums.UserRole

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userService: UserService
    protected var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userService = UserService(this)
    }

    override fun setContentView(layoutResID: Int) {
        val fullLayout = layoutInflater.inflate(R.layout.activity_base, findViewById(android.R.id.content), false) as DrawerLayout
        val container = fullLayout.findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(layoutResID, container, true)

        super.setContentView(fullLayout)
        drawerLayout = fullLayout

        initToolbar(fullLayout)
        getUserData()
    }

    private fun getUserData(){
        lifecycleScope.launch {
            user = userService.getCurrentUserData()

            user?.let { currentUser ->
                val navigationView = findViewById<NavigationView>(R.id.navigationView)
                val headerView = navigationView.getHeaderView(0)

                val nicknameText = headerView.findViewById<TextView>(R.id.headerNickname)
                val email = headerView.findViewById<TextView>(R.id.headerEmail)

                nicknameText.text = currentUser.nickname
                email.text = currentUser.email

                setupNavigation(drawerLayout)
            }
        }
    }


    private fun initToolbar(fullLayout: DrawerLayout) {
        val toolbar = fullLayout.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupNavigation(fullLayout: DrawerLayout) {
        val navigationView = fullLayout.findViewById<NavigationView>(R.id.navigationView)
        val menu = navigationView.menu


        if (user?.role ==UserRole.ADMIN) {
            menu.add(0, R.id.nav_add_book, 0, R.string.add_book)
        }

        val logoutItem = menu.findItem(R.id.nav_logout)
        val spanString = SpannableString(logoutItem.title)
        spanString.setSpan(ForegroundColorSpan(Color.RED), 0, spanString.length, 0)
        logoutItem.title = spanString

        navigationView.setBackgroundColor(Color.WHITE)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_books -> navigateTo(BooksActivity::class.java)
                R.id.nav_add_book -> navigateTo(AddBookActivity::class.java)
                R.id.nav_logout -> handleLogout()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun handleLogout() {
        val loggedOut = userService.logOut()
        if (loggedOut) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun navigateTo(target: Class<*>) {
        if (this::class.java != target) {
            val intent = Intent(this, target)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
