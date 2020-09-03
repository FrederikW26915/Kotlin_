package com.example.hello_login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.awaitAll

//import com.example.hello_login.spinner as spinner


const val EXTRA_MESSAGE = "com.example.Hello_login.MESSAGE"


class LoginActivity : AppCompatActivity() {


    private var emailText: EditText? = null
    private var passText: EditText? = null
    private var spinner: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        this.emailText = findViewById(R.id.loginEmail)
        this.passText = findViewById(R.id.loginPassword)

        this.spinner = findViewById(R.id.spinner)
        this.spinner?.let { it.visibility = View.GONE }
    }



    /** Called when the user taps the Login button */




    // view?
    fun sendMessage(view: View) {

        this.spinner?.let { it.visibility = View.VISIBLE }

        this.emailText?.isEnabled = false
        this.passText?.isEnabled = false
        findViewById<Button>(R.id.loginButton).isEnabled = false

        val emailString = this.emailText?.text.toString()
        val passString = this.passText?.text.toString()


        try {
            Network.login(emailString,passString,
                {token -> goToMainHelper(token)},
                {error -> errorHelper(error)})

            if(failed){
                errorMessage(this.message)
            } else {
                goToMain(this.message)
            }
        }
        catch (e: Exception) {
            e.message?.let { errorMessage(it) }
        }

    }

    var message = ""
    var failed = true

    private fun goToMainHelper(token: String){
        this.message = token
        this.failed = false
    }

    private fun errorHelper(error: String){
        this.message = error
        this.failed = true
    }

    private fun goToMain(token: String){
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, token)
        }

        resetInput()

        startActivity(intent)
    }

    private fun errorMessage(error: String){

        resetInput()

        Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
    }

    private fun resetInput(){
        this.spinner?.let { it.visibility = View.GONE }

        this.emailText?.isEnabled = true
        this.passText?.isEnabled = true
        this.passText?.text = null
        findViewById<Button>(R.id.loginButton).isEnabled = true

        this.emailText?.setSelectAllOnFocus(true)
        this.emailText?.requestFocus()

        this.failed = true
        this.message = ""
    }
}
