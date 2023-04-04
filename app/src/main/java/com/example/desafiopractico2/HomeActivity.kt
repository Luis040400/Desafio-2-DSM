package com.example.desafiopractico2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiopractico2.adapter.MyAdapter
import com.example.desafiopractico2.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity(), MyAdapter.OnRegistroClickListener {

    private lateinit var database: DatabaseReference
    private lateinit var dataRef: DatabaseReference
    lateinit var recycler_view: RecyclerView
    lateinit var myAdapter: MyAdapter
    private val carrito = mutableListOf<Item>()

    private lateinit var tvUsuario: TextView

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recycler_view = findViewById(R.id.recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference()
        dataRef = database.child("Medicamentos")
        tvUsuario = findViewById(R.id.tvUsuario)
        tvUsuario.text = "Bienvenido " + auth.currentUser!!.email
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.realizar_compra -> {
                val intent = Intent(this,PurchaseActivity::class.java)
                intent.putExtra("carrito",ArrayList(carrito))
                startActivity(intent)
                true
            }
            R.id.historial_compra -> {
                val intent = Intent(this,HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.cerrar_sesion -> {
                auth.signOut().also {
                    Toast.makeText(this,"Sesión cerrada",Toast.LENGTH_SHORT).show()

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val dataList = mutableListOf<Item>()
            for (h in snapshot.children) {
                val nombre = h.child("nombre").getValue(String::class.java)
                val precio = h.child("precio").getValue(Float::class.java)
                val cantidad = h.child("cantidad").getValue(Int::class.java)
                val contra_indicaciones =
                    h.child("contra_indicaciones").getValue(String::class.java)
                val indicaciones_uso = h.child("indicaciones_uso").getValue(String::class.java)
                val imagen = h.child("imagen").getValue(String::class.java)

                if (nombre != null && precio != null) {
                    val data = Item(
                        cantidad,
                        contra_indicaciones,
                        imagen,
                        indicaciones_uso,
                        nombre,
                        precio
                    )
                    dataList.add(data)
                }
            }
            myAdapter = MyAdapter(baseContext, dataList, this@HomeActivity)
            recycler_view.adapter = myAdapter
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(TAG, "Error al leer los datos", error.toException())
        }
    }

    override fun onStart() {
        super.onStart()
        dataRef.addValueEventListener(valueEventListener)
    }

    override fun onStop() {
        super.onStop()

        dataRef.removeEventListener(valueEventListener)
    }

    override fun onAgregarClick(registro: Item) {
        if(registro.cantidad == 0){
            Toast.makeText(this,"No hay producto existente",Toast.LENGTH_SHORT).show()
        }else{
            registro.cantidad = registro.cantidad!! - 1
            carrito.add(registro)
            myAdapter.notifyDataSetChanged()

            dataRef.child(registro.nombre).setValue(registro)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("RESULTADO", carrito.toString())
                    } else {
                        Log.e(TAG, "Error al agregar registro: ${task.exception}")
                    }

                }
        }
    }
}