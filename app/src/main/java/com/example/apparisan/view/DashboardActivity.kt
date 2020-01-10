package com.example.apparisan.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.apparisan.R
import com.example.apparisan.auth.AuthActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions

    lateinit var mAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var databaseref : DatabaseReference

    private var content: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        //referensi firebase
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser!!
        databaseref = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

        //berhubungan dengan google sign in untuk logout
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        //bottom navigation
        navbottom.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fragment = HistoriFragment()
        addFragment(fragment)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.histori -> {
                val fragment = HistoriFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menu -> {
                val fragment = MenuFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }


    //perintah keluar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_keluar -> {
                mAuth.signOut()

                //google signOut
                mGoogleSignInClient.signOut().addOnCompleteListener(this){
                    updateUI(null)
                }
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

            R.id.menu_profil -> {
                startActivity(Intent(this, ProfilActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    //perintah backpressed agar button kembali pada handphone jika ditekan maka akan keluar secara otomatis dari keluar
    //bukan kembali ke laman sebelumnya
    override fun onBackPressed() {
        super.onBackPressed()
        val keluar = Intent(Intent.ACTION_MAIN)
        keluar.addCategory(Intent.CATEGORY_HOME)
        keluar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(keluar)
        finish()
    }

    private fun updateUI(user: FirebaseUser?){
        if (user != null){
            startActivity(Intent(this, AuthActivity::class.java))
            Toasty.success(this, "Anda berhasil masuk", Toast.LENGTH_SHORT, true).show()
            finish()
        }
    }
}
