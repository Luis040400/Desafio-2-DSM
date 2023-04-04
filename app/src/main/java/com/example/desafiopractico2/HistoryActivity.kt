package com.example.desafiopractico2

import android.content.ContentValues
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var dataRef: DatabaseReference
    private lateinit var card_history: LinearLayout

    data class Historial(val productos: List<Producto>?,val total: Float?) : java.io.Serializable
    data class Producto(val nombre: String?, val precio: Float?) : java.io.Serializable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("historial")
        card_history = findViewById(R.id.card_history)
    }

    private val valueEventListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val datalist = mutableListOf<Historial>()
            for (h in snapshot.children){
                Log.d("ARREGLO", h.toString())
               val productos = ArrayList<Producto>()
                val total = h.child("total").getValue(Float::class.java)

                for(productosSnaphot in h.child("productos").children){
                    val nombre = productosSnaphot.child("nombre").getValue(String::class.java)
                    val precio = productosSnaphot.child("precio").getValue(Float::class.java)
                    productos.add(Producto(nombre,precio))
                }
                val historial = Historial(productos,total)
                datalist.add(historial)

            }
            datalist.forEach{item->
                item.productos?.forEach { it->
                    val cardProduct = layoutInflater.inflate(R.layout.history_card,null) as CardView
                    val nombre = cardProduct.findViewById<TextView>(R.id.history_name)
                    val precio = cardProduct.findViewById<TextView>(R.id.history_price)
                    nombre.text = it.nombre
                    precio.text = it.precio.toString()
                    card_history.addView(cardProduct)
                }
                val cardTotal = layoutInflater.inflate(R.layout.history_card,null) as CardView
                val totalNombre = cardTotal.findViewById<TextView>(R.id.history_name)
                val totalPrice = cardTotal.findViewById<TextView>(R.id.history_price)
                totalNombre.text = "Total"
                totalPrice.text = item.total.toString()
                card_history.addView(cardTotal)

                val divider = View(this@HistoryActivity)
                divider.setBackgroundColor(Color.BLACK)

                val layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelOffset(R.dimen.divider_height)
                )
                divider.layoutParams = layoutParams

                card_history.addView(divider)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(ContentValues.TAG, "Error al leer los datos", error.toException())
        }
    }

    override fun onStart() {
        super.onStart()
        val usuarioQuery = database.orderByChild("user").equalTo(auth.currentUser!!.uid)
        Log.d("USUARIO", auth.currentUser!!.uid)
        Log.d("RESULTADO", usuarioQuery.toString())
        usuarioQuery.addValueEventListener(valueEventListener)
    }

    override fun onStop() {
        super.onStop()
        database.removeEventListener(valueEventListener)
    }
}