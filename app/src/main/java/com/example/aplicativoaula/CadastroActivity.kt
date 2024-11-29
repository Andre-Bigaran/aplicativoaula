package com.example.aplicativoaula

import com.example.aplicativoaula.models.User
import com.example.aplicativoaula.api.ApiService
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.view.View

class CadastroActivity : AppCompatActivity() {
    private val TAG = "CadastroActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_cadastro)
        Log.d(TAG, "Tela de cadastro carregada")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://6749bd448680202966326cb8.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            Log.d(TAG, "Botão de voltar clicado. Finalizando a atividade.")
            finish()
        }

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)

        btnCadastrar.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            Log.d(TAG, "Botão de cadastrar clicado. Dados inseridos: Nome=$name, Email=$email")

            if (name.isEmpty()) {
                Log.w(TAG, "Campo Nome está vazio.")
                Toast.makeText(this, "Preencha o campo Nome!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Log.w(TAG, "Campo Email está vazio.")
                Toast.makeText(this, "Preencha o campo Email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUser = User(id = null, name = name, email = email)
            Log.d(TAG, "Enviando dados para a API: $newUser")

            val call = apiService.createUser(newUser)
            call.enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
                    Log.d(TAG, "Resposta recebida: ${response.raw()}")
                    if (response.isSuccessful) {
                        Log.d(TAG, "Cadastro bem-sucedido: ${response.body()}")
                        Toast.makeText(
                            this@CadastroActivity,
                            "Usuário cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        edtName.text.clear()
                        edtEmail.text.clear()
                    } else {
                        Log.e(
                            TAG,
                            "Erro no cadastro: Código=${response.code()}, Mensagem=${response.message()}"
                        )
                        Toast.makeText(
                            this@CadastroActivity,
                            "Erro ao cadastrar: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(TAG, "Erro na comunicação com a API: ${t.message}", t)
                    Toast.makeText(
                        this@CadastroActivity,
                        "Erro na comunicação: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
