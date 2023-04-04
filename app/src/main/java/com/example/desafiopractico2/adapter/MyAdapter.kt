package com.example.desafiopractico2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.desafiopractico2.R
import com.example.desafiopractico2.model.Item

class MyAdapter(var con: Context, var list: List<Item>, private val onRegistroClickListener: OnRegistroClickListener) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    interface OnRegistroClickListener{
        fun onAgregarClick(registro : Item)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var nombre = v.findViewById<TextView>(R.id.tvNombre)
        var precio = v.findViewById<TextView>(R.id.tvPrecio)
        var indicaciones_uso = v.findViewById<TextView>(R.id.tvIndicaciones)
        var contra_indicaciones = v.findViewById<TextView>(R.id.tvContraIndicaciones)
        var image = v.findViewById<ImageView>(R.id.cImageView)
        var cantidad = v.findViewById<TextView>(R.id.tvCantidad)
        var btnAdd = v.findViewById<Button>(R.id.btnAdd)

        fun bind(item: Item) {
            Glide.with(con).load(item.imagen).into(image)
            nombre.text = "Nombre: " + item.nombre
            precio.text = "Precio: $" + item.precio.toString()
            cantidad.text = "Cantidad: " + item.cantidad.toString()
            indicaciones_uso.text = "Indicaciones: " + item.indicaciones_uso
            contra_indicaciones.text = "Contra-indicaciones: " + item.contra_indicaciones

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(con).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

        holder.btnAdd.setOnClickListener{
            onRegistroClickListener.onAgregarClick(item)
        }
    }

}