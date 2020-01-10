package com.example.apparisan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.apparisan.R
import com.example.apparisan.model.Histori

class HistoriAdapter (val historiList: ArrayList<Histori>, val context: Context) : RecyclerView.Adapter<HistoriAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_histori, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return historiList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tinggi.text = historiList[position].tinggi
        holder.berat.text = historiList[position].berat
        holder.tgl.text = historiList[position].tgl

        val meter = historiList[position].tinggi.toFloat()
        val hitung1 = meter/100
        val hitung2 = hitung1*hitung1
        val hitung3 = historiList[position].berat.toFloat() / hitung2

        if(hitung3 <=18.5){
            holder.kurang.visibility = View.VISIBLE

        }
        else if(hitung3 >= 18.5 && hitung3 <= 22.9) {
            holder.normal.visibility = View.VISIBLE
        }
        else if(hitung3 >= 23 && hitung3 <= 29.9) {
            holder.berlebih.visibility = View.VISIBLE
        }
        else{
            holder.obesitas.visibility = View.VISIBLE
        }

    }

    // holder class
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tinggi = itemView.findViewById(R.id.Tinggi) as TextView
        val berat = itemView.findViewById(R.id.Berat) as TextView
        val tgl = itemView.findViewById(R.id.Tgl) as TextView
        val normal = itemView.findViewById(R.id.normal) as Button
        val kurang = itemView.findViewById(R.id.kurang) as Button
        val berlebih = itemView.findViewById(R.id.berlebih) as Button
        val obesitas = itemView.findViewById(R.id.obesitas) as Button
    }
}