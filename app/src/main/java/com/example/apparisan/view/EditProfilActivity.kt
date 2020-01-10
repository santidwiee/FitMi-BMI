package com.example.apparisan.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.apparisan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.android.synthetic.main.dialog_edit_profil.*
import java.io.IOException

class EditProfilActivity : AppCompatActivity() {

    lateinit var databaseref: DatabaseReference
    lateinit var Auth: FirebaseAuth
    private var ImagePath: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_profil)

        val Auth = FirebaseAuth.getInstance()
        val user_id = Auth.currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(user_id)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println("Info: ${p0.message}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val nama1 = p0.child("nama").value.toString()
                    val tlp1 = p0.child("tlp").value.toString()
                    val alamat1 = p0.child("alamat").value.toString()
                    val image = p0.child("image").value.toString()

                    Glide.with(this@EditProfilActivity)
                        .asBitmap()
                        .load(image)
                        .into(FotoProfil)


                    nama.setText(nama1)
                    tlp.setText(tlp1)
                    alamat.setText(alamat1)

                }else{
                    Glide.with(this@EditProfilActivity)
                        .asBitmap()
                        .load(R.drawable.account)
                        .into(FotoProfil)
                    nama.setText("")
                    tlp.setText("")
                    alamat.setText("")
                }
            }
        })

        tambahFoto.setOnClickListener {
            PilihFoto()
        }

        addprofil.setOnClickListener {
            UploadProfil()
        }

        btn_batal.setOnClickListener {
            finish()
        }
    }

    fun PilihFoto(){
        val intentImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intentImg, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            ImagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, ImagePath)
                FotoProfil.setImageBitmap(bitmap)

                tambahFoto.alpha = 0f
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun UploadProfil(){
        val Nama = nama.text.toString()
        val Tlp = tlp.text.toString()
        val Alamat = alamat.text.toString()

        if(Nama.isEmpty() || Tlp.isEmpty() || Alamat.isEmpty()){
            Toasty.warning(this, "Isi detail akun", Toast.LENGTH_SHORT, true).show()
        }
        else{

            Auth = FirebaseAuth.getInstance()
            val user_id = Auth.currentUser!!.uid
            val storageRef = FirebaseStorage.getInstance().getReference("Users").child(user_id)
            databaseref = FirebaseDatabase.getInstance().getReference("Users").child(user_id)


            if (ImagePath != null) {
                storageRef.putFile(ImagePath!!)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener {
                            val image = it.toString()

                            val ProfilMap = HashMap<String, Any>()
                            ProfilMap["nama"] = Nama
                            ProfilMap["tlp"] = Tlp
                            ProfilMap["alamat"] = Alamat
                            ProfilMap["image"] = image

                            databaseref.updateChildren(ProfilMap)

                        }
                    }
            }
            Toasty.success(this, "Berhasil tambah data Akun", Toast.LENGTH_SHORT, true).show()
            finish()
        }
    }
}