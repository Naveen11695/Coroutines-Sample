package com.local.runblocking_coroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            main()
        }
    }

    private fun main() {

        // Job #1
        CoroutineScope(Main).launch {
            println("Starting job in thread: ${Thread.currentThread().name}")

            val result1 = getResult()
            println("result1: $result1")

            val result2 = getResult()
            println("result2: $result2")

            val result3 = getResult()
            println("result3: $result3")

            val result4 = getResult()
            println("result4: $result4")

            val result5 = getResult()
            println("result5: $result5")
        }

        // Job #2
        CoroutineScope(Main).launch {
            delay(1000)

            /**
             * block the whole thread <Main thread> --- use for the prioritising the job inside the thread
             */
            runBlocking {
                println("Blocking Thread: ${Thread.currentThread().name}")
                delay(4000)
                println("Done blocking Thread: ${Thread.currentThread().name}")
            }
        }
    }

    /**
     * Return the random number
     */
    private suspend fun getResult(): Int {
        delay(1000)
        return Random.nextInt(0,100)
    }
}
