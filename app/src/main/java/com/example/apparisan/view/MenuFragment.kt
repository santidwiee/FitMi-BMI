package com.example.apparisan.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.example.apparisan.R
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu.view.*

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val kurang = view.findViewById<Button>(R.id.btn_kurang)

        kurang.setOnClickListener {
                menu_kurang.visibility = View.VISIBLE

        }

        view.btn_normal.setOnClickListener {
            menu_normal.visibility = View.VISIBLE
        }

        view.btn_berlebih.setOnClickListener {
            menu_berlebih.visibility = View.VISIBLE
        }

        view.btn_obesitas.setOnClickListener {
            menu_obesitas.visibility = View.VISIBLE
        }

        return view
    }

}
