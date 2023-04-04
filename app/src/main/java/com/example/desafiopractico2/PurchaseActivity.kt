package com.example.desafiopractico2

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.desafiopractico2.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat

class PurchaseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("historial")
    private lateinit var mProgressBar: ProgressDialog

    data class Producto(val nombre: String?, val precio: Float?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        mProgressBar = ProgressDialog(this)
        auth = FirebaseAuth.getInstance()
        addCar()

    }

    fun addCar() {
        var user = auth.currentUser!!
        var cardLayout = findViewById<LinearLayout>(R.id.card_layout)
        var total = 0F
        val carrito = intent.extras?.getSerializable("carrito") as? ArrayList<Item> ?: ArrayList()
        Log.d("CARRITO", carrito.toString())
        carrito.forEach { item ->
            val cardView = layoutInflater.inflate(R.layout.card_item, null) as CardView

            val nombre = cardView.findViewById<TextView>(R.id.card_name)
            val precio = cardView.findViewById<TextView>(R.id.card_price)

            total += item.precio!!

            nombre.text = item.nombre
            precio.text = "$" + item.precio.toString()

            cardLayout.addView(cardView)
        }
        val df = DecimalFormat("#.##")
        val cardIVA = layoutInflater.inflate(R.layout.card_item, null) as CardView
        val nombreIVA = cardIVA.findViewById<TextView>(R.id.card_name)
        val precioIVA = cardIVA.findViewById<TextView>(R.id.card_price)
        val IVA = total * 0.13
        val IVAFormat = df.format(IVA)
        nombreIVA.text = "IVA"
        precioIVA.text = "$$IVAFormat"
        cardLayout.addView(cardIVA)

        val cardTotal = layoutInflater.inflate(R.layout.card_item, null) as CardView
        val nombreTotal = cardTotal.findViewById<TextView>(R.id.card_name)
        val precioTotal = cardTotal.findViewById<TextView>(R.id.card_price)
        val Total = total + IVA
        val totalFormat = df.format(Total)

        nombreTotal.text = "Total"
        precioTotal.text = "$$totalFormat"
        cardLayout.addView(cardTotal)


        //Vamos a agregar dos EditText para la tarjeta y el CVV
        val etTarjeta = EditText(this@PurchaseActivity)
        etTarjeta.hint = "Ingrese el numero de tarjeta"
        etTarjeta.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 16
            rightMargin = 16
            topMargin = 5
            bottomMargin = 5
        }
        etTarjeta.inputType = InputType.TYPE_CLASS_NUMBER
        etTarjeta.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
        cardLayout.addView(etTarjeta)

        val etCVV = EditText(this@PurchaseActivity)
        etCVV.hint = "Ingrese el CVV"
        etCVV.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 16
            rightMargin = 16
            topMargin = 5
            bottomMargin = 5
        }
        etCVV.inputType = InputType.TYPE_CLASS_NUMBER
        etCVV.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
        cardLayout.addView(etCVV)

        val eTDireccion = EditText(this@PurchaseActivity)
        eTDireccion.hint = "Dirección de entrega"
        eTDireccion.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            leftMargin = 16
            rightMargin = 16
            topMargin = 5
            bottomMargin = 5
        }
        cardLayout.addView(eTDireccion)
        val productos = mutableListOf<Producto>()
        val nuevoBoton = Button(this)
        nuevoBoton.text = "Comprar"
        nuevoBoton.setOnClickListener {
           if(etTarjeta.text.isNotEmpty() && etCVV.text.isNotEmpty()){
               if (carrito.isNotEmpty()) {
                   mProgressBar.setMessage("Realizando compra...")
                   mProgressBar.show()
                   carrito.forEach { item ->
                       val precio: Float? = item.precio
                       val precioF: Float? = (Math.round(precio?.times(100) ?: 0.0F) / 100.0).toFloat()
                       val producto = Producto(item.nombre, precioF)
                       productos.add(producto)
                   }
                   val totalHistorial = mapOf(
                       "user" to user.uid,
                       "total" to totalFormat.toFloat(),
                       "productos" to productos
                   )
                   val historialRef = databaseRef.push()
                   historialRef.setValue(totalHistorial)
                       .addOnCompleteListener { task ->
                           if (task.isSuccessful) {
                               Toast.makeText(this, "Compra realizada con exito", Toast.LENGTH_SHORT)
                                   .show()
                               goHome()
                           } else {
                               Log.e(ContentValues.TAG, "Error al agregar registro: ${task.exception}")
                           }

                       }
               } else {
                   Toast.makeText(this, "No hay productos en el carrito!", Toast.LENGTH_SHORT).show()
               }
           }else{
               if(etTarjeta.text.isEmpty()){
                   etTarjeta.error = "Campo requerido"
               }
               if(etCVV.text.isEmpty()){
                   etCVV.error = "Campo requerido"
               }
               if(eTDireccion.text.isEmpty()){
                   eTDireccion.error = "Campo requerido"
               }
           }
        }
        cardLayout.addView(nuevoBoton)

    }

    private fun goHome() {
        mProgressBar.hide()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}

