package com.example.aplicativoaula

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativoaula.api.ApiService
import com.example.aplicativoaula.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListagemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://6749bd448680202966326cb8.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getUsers()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(
                call: Call<List<User>>,
                response: Response<List<User>>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null) {
                        atualizarInterface(users)
                    } else {
                        mostrarErro("Nenhum dado encontrado.")
                    }
                } else {
                    val errorMessage = "Erro na API: ${response.code()} - ${response.message()}"
                    mostrarErro(errorMessage)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                t.printStackTrace()
                mostrarErro("Erro ao carregar dados: ${t.localizedMessage}")
            }
        })
    }

    private fun atualizarInterface(users: List<User>) {
        val container = findViewById<LinearLayout>(R.id.containerUsers)
        container.removeAllViews()
        for (user in users) {
            val userView = TextView(this).apply {
                text = "Nome: ${user.name}\nEmail: ${user.email}\n"
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }
            container.addView(userView)
        }
    }

    private fun mostrarErro(mensagem: String) {
        val container = findViewById<LinearLayout>(R.id.containerUsers)
        container.removeAllViews()
        val erroView = TextView(this).apply {
            text = mensagem
            textSize = 16f
            setPadding(8, 8, 8, 8)
        }
        container.addView(erroView)
    }
}
