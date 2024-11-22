package br.com.dominiodaaplicao.ecosmart.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import br.com.dominiodaaplicao.ecosmart.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var entrarButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var signupTextView: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.senha)
        entrarButton = findViewById(R.id.entrarButton)
        backButton = findViewById(R.id.backButton)
        signupTextView = findViewById(R.id.signupTextView)

        backButton.setOnClickListener {
            finish()
        }

        entrarButton.setOnClickListener {
            signInWithEmailAndPassword()
        }

        setupSignupTextView()
    }

    private fun signInWithEmailAndPassword() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty()) {
            emailEditText.error = "Por favor, insira seu email"
            return
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Por favor, insira sua senha"
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao fazer login. Verifique seus dados.", Toast.LENGTH_SHORT).show()
                    passwordEditText.text.clear()
                }
            }
    }

    private fun setupSignupTextView() {
        val text = "Não possui cadastro? Cadastre-se"
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, CadastroActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(
            clickableSpan,
            text.indexOf("Cadastre-se"),
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            text.indexOf("Cadastre-se"),
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        signupTextView.text = spannableString
        signupTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}
