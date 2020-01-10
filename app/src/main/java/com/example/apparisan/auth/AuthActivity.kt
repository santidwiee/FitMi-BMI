package com.example.apparisan.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.apparisan.view.DashboardActivity
import com.example.apparisan.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.auth_activity.*

class AuthActivity : AppCompatActivity() {

    private val TAG = "AuthActivity"

    //Firebase references
    lateinit var databaseref : DatabaseReference
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null

    //google
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)


        //inisialisasi firebase
        databaseref = FirebaseDatabase.getInstance().getReference("Users")
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser

        //perintah gone atau visible auth
        txt_login.setOnClickListener {
            line_login.visibility = View.VISIBLE
            line_regist.visibility = View.GONE
        }
        txt_regist.setOnClickListener {
            line_login.visibility = View.GONE
            line_regist.visibility = View.VISIBLE
        }

        //setting google sign in
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //perintah button
        btn_masuk.setOnClickListener {
            MasukAkun()
        }
        btn_daftar.setOnClickListener {
            BuatAkun()
        }
        btn_google.setOnClickListener {
                view: View? -> MasukGoogle()
        }

        //perintah lupapassword
        txt_reset.setOnClickListener {
//            startActivity(Intent(this@AuthActivity, ResetActivity::class.java))
        }
    }



    fun MasukAkun(){
        val email = email_login.text.toString()
        val password = pass_login.text.toString()

        if(email.isEmpty() && password.isEmpty()){
            Toasty.warning(this, "Masukkan data akun", Toast.LENGTH_SHORT, true).show()
        }
        else {

            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if(task.isSuccessful){
                        //jika masuk sukses
                        Log.d(TAG, "signInWithEmail:success")
                        Toasty.success(this, "Anda berhasil masuk", Toast.LENGTH_SHORT, true).show()

                        MasukUser()

                    }else{
                        //jika gagal masuk, maka akan menampilkan pesan
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toasty.error(this, "Autentikasi Gagal", Toast.LENGTH_SHORT, true).show()
                    }
                }
        }
    }


    fun BuatAkun(){
        val email = email_daftar.text.toString()
        val password = pass_daftar.text.toString()

        if (email.isEmpty() && password.isEmpty()) {
            Toasty.warning(this, "Wajib isi seluruh kolom", Toast.LENGTH_SHORT, true).show()
        }
        else if(password.length < 6){
            Toasty.warning(this, "Minimal password 6 karakter", Toast.LENGTH_SHORT, true).show()
        }
        else {

            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        //jika daftar sukses
                        Log.d(TAG, "createUserWithEMail:success")
                        Toasty.success(this, "Berhasil mendaftar", Toast.LENGTH_SHORT, true).show()

                        mAuth = FirebaseAuth.getInstance()
                        user = mAuth!!.currentUser

                        //data akun
                        databaseref.child(user!!.uid).child("email").setValue(email).addOnCompleteListener {
                            Log.d(TAG, "Simpan ke database berhasil")
                            email_daftar.setText("")
                            pass_daftar.setText("")
                        }
                        finish()
                        MasukUser()
                    }
                    else {
                        //if sign in fails, display a message to tthe user
                        Log.e(TAG, "createUserWithEmail:failure", task.exception)
                        Toasty.error(this, "Gagal Mendaftar...", Toast.LENGTH_SHORT, true).show()
                    }
                }
        }
    }


    fun MasukGoogle(){
        val signInIntent : Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toasty.error(this, e.toString(), Toast.LENGTH_SHORT, true).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth!!.currentUser
                    val userID = user!!.uid
                    val email = user.email.toString()


                    //data akun
                    databaseref.child(userID).child("email").setValue(email)
                    updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toasty.error(this, "Gagal Autentikasi.", Toast.LENGTH_SHORT, true).show()
                    updateUI(null!!)
                }
            }
    }


    fun updateUI(user: FirebaseUser){
        if (user != null){
            startActivity(Intent(this@AuthActivity, DashboardActivity::class.java))
            Toasty.success(this, "Anda berhasil masuk", Toast.LENGTH_SHORT, true).show()
            finish()
        }
    }


    fun MasukUser(){
        startActivity(Intent(this@AuthActivity, DashboardActivity::class.java))
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

    //perintah mulai, setelah aplikasi dikeluarkan jika belum logout
    override fun onStart() {
        super.onStart()
        user = FirebaseAuth.getInstance().currentUser

        if(user != null){

            databaseref.child(user!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        MasukUser()
                        updateUI(user!!)
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    println("Info: ${p0.message}")
                }
            })
        }
    }

}

