package br.com.dominiodaaplicao.ecosmart.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.dominiodaaplicao.ecosmart.util.AtitudesAdapter
import br.com.dominiodaaplicao.ecosmart.R
import br.com.dominiodaaplicao.ecosmart.element.Atitudes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var atitudesList: MutableList<Atitudes>
    lateinit var adapter: AtitudesAdapter

    private lateinit var atitudesRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var descricaoEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        atitudesRecyclerView = findViewById(R.id.atitudesRecyclerView)
        addButton = findViewById(R.id.addButton)
        descricaoEditText = findViewById(R.id.descricaoEditText)

        databaseReference = FirebaseDatabase.getInstance().getReference("Atitudes")
        atitudesList = mutableListOf()
        adapter = AtitudesAdapter(atitudesList, databaseReference, this)

        atitudesRecyclerView.layoutManager = LinearLayoutManager(this)
        atitudesRecyclerView.adapter = adapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedList = mutableListOf<Atitudes>()
                for (dataSnapshot in snapshot.children) {
                    val atitude = dataSnapshot.getValue(Atitudes::class.java)
                    atitude?.let {
                        val atitudeWithId = it.copy(id = dataSnapshot.key ?: "")
                        updatedList.add(atitudeWithId)
                    }
                }

                atitudesList.clear()
                atitudesList.addAll(updatedList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error reading data: ${error.message}")
                Toast.makeText(baseContext, "Erro ao carregar dados do Firebase", Toast.LENGTH_SHORT).show()
            }
        })


        addButton.setOnClickListener {
            val descricao = descricaoEditText.text.toString().trim()
            if (descricao.isNotEmpty()) {
                val novaAtitude = Atitudes(descricao = descricao)
                val newAtitudeRef = databaseReference.push()

                newAtitudeRef.setValue(novaAtitude).addOnSuccessListener {
                    descricaoEditText.text.clear()
                    Toast.makeText(this, "Atitude adicionada com sucesso", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.e("FirebaseError", "Erro ao adicionar atitude: ${it.message}")
                    Toast.makeText(this, "Erro ao adicionar atitude", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Digite uma descrição válida", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
