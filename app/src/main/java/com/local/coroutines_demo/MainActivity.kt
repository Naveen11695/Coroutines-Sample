package com.local.coroutines_demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    val JOB_TIMEOUT = 1900L
    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            // IO <Input/Output -- network request, local database>,
            // Main <On main thread>,
            // Default <heavy computational work>
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        // setting up timeout for requests
        withContext(IO) {
            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                val result1 = getResult1FromApi()
                setTextOnMainThread(result1)

                val result2 = getResult2FromApi(result1)
                setTextOnMainThread(result2)
            }

            //check if job is completed or not
            withContext(Main) {
                if (job == null) {
                    val cancelMessage = "Cancelling job...Job took longer than ${JOB_TIMEOUT}ms"
                    Toast.makeText(this@MainActivity, cancelMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + input
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        // work in context of main thread
        withContext(Main) {
            setNewText(input)
        }
    }


    /*   private suspend fun fakeApiRequest() {
           val result1 = getResult1FromApi()
           println("debug: $result1")
           setTextOnMainThread(result1)

           val result2 = getResult2FromApi(result1)
           setTextOnMainThread(result2)
       }*/

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(result1: String): String {
        logThread("getResult2FromApi")
        delay(1000)
        return " -> $RESULT_2 \n"
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}

