package br.com.dominiodaaplicao.ecosmart.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.dominiodaaplicao.ecosmart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        auth = Firebase.auth

        val nomeEditText = findViewById<EditText>(R.id.nome)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val registerButton = findViewById<Button>(R.id.registerButton)

        val backButton: ImageButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            val nome = nomeEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (nome.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        if (user != null) {
                            val uid = user.uid
                            Toast.makeText(this, "Conta criada com sucesso! UID: $uid", Toast.LENGTH_SHORT).show()
                        }

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        Toast.makeText(this, "Erro ao criar a conta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
