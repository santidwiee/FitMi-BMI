package com.example.apparisan.view


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.apparisan.R
import com.example.apparisan.adapter.HistoriAdapter
import com.example.apparisan.model.Histori
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_add_data.view.*
import java.util.*

class HistoriFragment : Fragment() {

    private lateinit var add : ImageButton
    private lateinit var list : RecyclerView
    lateinit var databaseRef : DatabaseReference
    lateinit var Auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_histori, container, false)

        //referense firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Histori Diri")
        Auth = FirebaseAuth.getInstance()
        val user_id = Auth.currentUser!!.uid


        //menambahkan anggota
        add = view.findViewById(R.id.addData)
        add.setOnClickListener {
            val mDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_add_data, null)


            //AlertDialogBuilder
            val mBuilder = activity?.let { it1 ->
                AlertDialog.Builder(it1)
                    .setView(mDialogView)
            }
            val  mAlertDialog = mBuilder!!.show()

            //deklarasi kalender terlebih dahulu
            val date = Calendar.getInstance()
            val year = date.get(Calendar.YEAR)
            val month = date.get(Calendar.MONTH)
            val day = date.get(Calendar.DAY_OF_MONTH)

            val btndate = mDialogView.findViewById(R.id.btn_date) as ImageView
            btndate.setOnClickListener {
                val dpd = DatePickerDialog(mDialogView.context,
                    DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                        mDialogView.txt_date.text = mYear.toString() + "-" + (mMonth + 1) + "-" + mDay

                    }, year, month, day
                )
                dpd.show()
            }


            mDialogView.btn_simpan.setOnClickListener {

                mAlertDialog.dismiss()

                //deklarasikan edittext
                val tinggi = mDialogView.edt_tinggi.text.toString()
                val berat = mDialogView.edt_berat.text.toString()
                val tgl = mDialogView.txt_date.text.toString()


                if (tinggi.isEmpty() || berat.isEmpty() || tgl.isEmpty()){
                    Toasty.warning(mDialogView.context, "Wajib Diisi", Toast.LENGTH_SHORT, true).show()
                    mDialogView.edt_tinggi.setText("")
                    mDialogView.edt_berat.setText("")
                    mDialogView.txt_date.setText("")
                }
                else{
                    val DataMap = HashMap<String, String>()
                    DataMap["tinggi"] = tinggi
                    DataMap["berat"] = berat
                    DataMap["tgl"] = tgl

                    databaseRef.child(user_id).child(tgl).setValue(DataMap).addOnCompleteListener {
                        Toasty.success(mDialogView.context, "Berhasil tambah data", Toast.LENGTH_SHORT, true).show()
                        mDialogView.edt_tinggi.setText("")
                        mDialogView.edt_berat.setText("")
                        mDialogView.txt_date.setText("")
                    }

                }
            }
            mDialogView.btn_batal.setOnClickListener {

                mAlertDialog.dismiss()
            }
        }


        //menampilkan list anggota
        val histori: ArrayList<Histori> = ArrayList()
        list = view.findViewById(R.id.List)
        list.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        databaseRef.child(user_id).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println("Info: ${p0.message}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    histori.clear()
                    for(data in p0.children){
                        val getValue = data.getValue(Histori::class.java) as Histori
                        histori.add(getValue)
                    }
                }
                val historiAdapter = activity?.let { HistoriAdapter(histori, it) }
                list.adapter = historiAdapter
                historiAdapter?.notifyDataSetChanged()
            }
        })

        return view
    }
}
