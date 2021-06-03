package com.aaxena.takenotes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.splash_screen.*

class LaunchScreen : AppCompatActivity() {
    var name: String? = null
    var acc_status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences(SplashScreen.UI_MODE, Context.MODE_PRIVATE)
        name = prefs.getString("uiMode", "System")
        applyUI()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        val prefsmanager = getSharedPreferences(SignUp.STATUS, Context.MODE_PRIVATE)
        acc_status = prefsmanager.getString("acc_status", "okay")
        if (acc_status == "suspended") {
            Toast.makeText(this@LaunchScreen, "Your account is temporarily suspended due to proactive use, contact the developer", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Handler().postDelayed({
                check()
            }, 690)
/*

            motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                   check()
                }
                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) { }

                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) { }

                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) { }

            })

 */
        }
    }
    private fun applyUI() {
        when (name) {
            "Dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            "Light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
    private fun hideSystemUIAndNavigation(activity: Activity) {
        val decorView: View = activity.window.decorView
        decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUIAndNavigation(this)
        }
    }
    private fun check() {
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) { //User Signed In, Proceeding to Landing
            val i = Intent(this@LaunchScreen, BottomHandler::class.java)
            startActivity(i)
            overridePendingTransition(0,0)
            finish()
        } else { //Newbie
            val i = Intent(this@LaunchScreen, WelcomeActivity::class.java)
            startActivity(i)
            overridePendingTransition(0,0)
            finish()
        }
    }
}
