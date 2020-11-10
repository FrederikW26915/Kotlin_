package com.example.hello_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.mainLabel).apply {
            text = message
        }
    }


    fun sendSearch(view: View){
        try {
            Log.e("hej","try")
            Network.search("hej")
        }
        catch (e: Exception) {
            //e.message?.let { errorMessage(it) }
        }
    }
    //
    // search builder med token og search ord

    private fun update_list(){}

    private fun proon_data(){}
}
