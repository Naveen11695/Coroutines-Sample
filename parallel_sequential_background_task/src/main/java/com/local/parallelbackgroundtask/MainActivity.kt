package com.local.parallelbackgroundtask

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {

                val result1 = async {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name} ms.")
                    getResult1FromApi()
                }.await()

                setTextOnMainThread(result1)

                val result2 = async {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name} ms.")
                    try {
                        getResult2FromApi("result1")
                    } catch (e: CancellationException) {
                        Log.e("Exception", e.message.toString())
                        cancel()
                    }
                }
                if (!result2.isCancelled)
                    setTextOnMainThread(result2.await().toString())
            }
            println("debug: total elapsed time: $executionTime")
        }
    }


    /**
     * Parallel job schedule in background thread -- using async/wait
     */
/*    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name} ms.")
                    val result1 = getResult1FromApi()
                    result1
                }
                val result2: Deferred<String> = async {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name} ms.")
                    val result2 = getResult2FromApi()
                    result2
                }

                // call the results by awaiting for the result
                setTextOnMainThread(result1.await())
                setTextOnMainThread(result2.await())
            }
            println("debug: total elapsed time: $executionTime")
        }
    }*/

    /**
     * Parallel job schedule in background thread
     */
/*    private fun fakeApiRequest() {

        val startTime = System.currentTimeMillis()

        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name} ms.")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread(result1)
                }
                println("debug: complete job1 in $time1 ms.")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name} ms.")
                    val result2 = getResult2FromApi()
                    setTextOnMainThread(result2)
                }
                println("debug: complete job2 in $time2 ms.")
            }
        }
        parentJob.invokeOnCompletion {
            println("debug: total elapsed time: ${System.currentTimeMillis() - startTime}")
        }
    }*/

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

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(result1: String): String {
        logThread("getResult2FromApi")
        delay(1700)
        if (result1 == "RESULT #1")
            return " -> $RESULT_2 \n"
        throw CancellationException("Job cancelled ! Result #1 was incorrect...")
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}
