package com.example.apparisan.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.apparisan.R
import com.example.apparisan.adapter.HistoriAdapter
import com.example.apparisan.model.Histori
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profil.*

class ProfilActivity : AppCompatActivity() {

    lateinit var databaseref :DatabaseReference
    lateinit var Auth : FirebaseAuth
    private var ImagePath: Uri? = null

    private var TAG = "ProfilActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val Auth = FirebaseAuth.getInstance()
        val user_id = Auth.currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(user_id)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println("Info: ${p0.message}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val email = p0.child("email").value.toString()
                    val nama = p0.child("nama").value.toString()
                    val tlp = p0.child("tlp").value.toString()
                    val alamat = p0.child("alamat").value.toString()
                    val image = p0.child("image").value.toString()

//                    Glide.with(this@ProfilActivity).load("image")

                    Glide.with(this@ProfilActivity)
                        .asBitmap()
                        .load(image)
                        .into(fotoAkun)


                    txt_email.text = email
                    txt_nama.text = nama
                    txt_tlp.text = tlp
                    txt_alamat.text = alamat

            }
        }
        })
    }

    //perintah Edit Profil
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_edit -> {
                    startActivity(Intent(this, EditProfilActivity::class.java))
                }
            }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profil, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
