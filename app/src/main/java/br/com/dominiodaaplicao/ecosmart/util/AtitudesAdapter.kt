package br.com.dominiodaaplicao.ecosmart.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import br.com.dominiodaaplicao.ecosmart.R
import br.com.dominiodaaplicao.ecosmart.element.Atitudes
import com.google.firebase.database.DatabaseReference

class AtitudesAdapter(
    private val atitudesList: MutableList<Atitudes>,
    private val databaseReference: DatabaseReference,
    private val context: Context
) : RecyclerView.Adapter<AtitudesAdapter.AtitudeViewHolder>() {

    class AtitudeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descricaoTextView: TextView = itemView.findViewById(R.id.descricaoTextView)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtitudeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_atitudes, parent, false)
        return AtitudeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AtitudeViewHolder, position: Int) {
        val atitude = atitudesList[position]
        holder.descricaoTextView.text = atitude.descricao

        // Ação do botão editar
        holder.editButton.setOnClickListener {
            exibirDialogEditar(atitude, position)
        }

        // Ação do botão excluir
        holder.deleteButton.setOnClickListener {
            exibirDialogConfirmacaoExclusao(atitude, position)
        }
    }

    private fun exibirDialogEditar(atitude: Atitudes, position: Int) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_editar_atitude, null)
        val editText = view.findViewById<EditText>(R.id.editDescricaoEditText)
        editText.setText(atitude.descricao)

        builder.setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val novaDescricao = editText.text.toString().trim()
                if (novaDescricao.isNotEmpty()) {
                    editarAtitude(atitude, novaDescricao, position)
                } else {
                    Toast.makeText(context, "Descrição não pode ser vazia", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun exibirDialogConfirmacaoExclusao(atitude: Atitudes, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja excluir esta atitude?")
            .setPositiveButton("Excluir") { _, _ ->
                removerAtitude(atitude, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun editarAtitude(atitude: Atitudes, novaDescricao: String, position: Int) {
        val atitudeAtualizada = atitude.copy(descricao = novaDescricao)
        atitude.id?.let {
            databaseReference.child(it).setValue(atitudeAtualizada).addOnSuccessListener {
                atitudesList[position] = atitudeAtualizada
                notifyItemChanged(position)
                Toast.makeText(context, "Atitude editada com sucesso", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Erro ao editar atitude: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removerAtitude(atitude: Atitudes, position: Int) {
        atitude.id?.let {
            databaseReference.child(it).removeValue().addOnSuccessListener {

                atitudesList.remove(atitude)

                notifyItemRemoved(position)

                notifyItemRangeChanged(position, atitudesList.size - 1)

                Toast.makeText(context, "Atitude removida com sucesso", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Erro ao remover atitude: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun getItemCount() = atitudesList.size
}
