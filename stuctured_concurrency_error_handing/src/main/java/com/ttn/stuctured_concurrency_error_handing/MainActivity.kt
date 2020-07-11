package com.ttn.stuctured_concurrency_error_handing

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

/**
 * case 1: If there are 3 child jobs A,B,C of parent job and B throws an exception then job A and C will
 *          also be failed automatically. and the parent job will display failed message.
 * case 2: If job B is cancelled then job A and C will also be complete successfully.
 *          and the parent job will display success message. <ON THE JOB ITSELF>
 * case 3: If cancellation Exception is thrown then job A and C will also be complete successfully.
 *          and the parent job will display success message. <INSIDE THE WORK METHOD>
 *
 *              Alternative way
 *              Use -----------> SupervisorScope
 */
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main()
    }

    private val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Exception thrown in one of the children: $exception")
    }

    private fun main() {
        val parentJob = CoroutineScope(IO).launch(handler) {
            supervisorScope {
                // ----------JOB A ----------------
                val jobA = launch {
                    val resultA = getResult(1)
                    println("resultA: $resultA")
                }
                jobA.invokeOnCompletion { throwable ->
                    if (throwable != null)
                        println("Error getting resultA: $throwable")
                }

                // ----------JOB B ----------------
                val jobB = launch {
                    val resultB = getResult(2)
                    println("resultB: $resultB")
                }

                /*
                 this will cancel the job B
                 */
//            jobB.cancel()

                jobB.invokeOnCompletion { throwable ->
                    if (throwable != null)
                        println("Error getting resultB: $throwable")
                }

                // ----------JOB C ----------------
                val jobC = launch {
                    val resultC = getResult(3)
                    println("resultC: $resultC")
                }
                jobC.invokeOnCompletion { throwable ->
                    if (throwable != null)
                        println("Error getting resultC: $throwable")
                }
            }
            /*    // ----------JOB A ----------------
                val jobA = launch {
                    val resultA = getResult(1)
                    println("resultA: $resultA")
                }
                jobA.invokeOnCompletion { throwable ->
                    if(throwable != null)
                    println("Error getting resultA: $throwable")
                }

                // ----------JOB B ----------------
                val jobB = launch {
                    val resultB = getResult(2)
                    println("resultB: $resultB")
                }

                *//*
             this will cancel the job B
             *//*
//            jobB.cancel()

            jobB.invokeOnCompletion { throwable ->
                if(throwable != null)
                println("Error getting resultB: $throwable")
            }

            // ----------JOB C ----------------
            val jobC = launch {
                val resultC = getResult(3)
                println("resultC: $resultC")
            }
            jobC.invokeOnCompletion { throwable ->
                if(throwable != null)
                println("Error getting resultC: $throwable")
            }*/
        }
        parentJob.invokeOnCompletion { throwable ->
            if (throwable != null)
                println("Parent job failed: $throwable")
            else
                println("Parent job Success")
        }
    }

    private suspend fun getResult(number: Int): Int {
        delay(500L)
        if (number == 2) {
            /**
             * case 1: exception is thrown
             */
            throw Exception("Error in getting the result for number $number")

            /**
             * case 2: cancelling the job B
             *
             * it will not cancel the job...As for cancelling the job.
             *  we have to call the cancel method on the job itself.
             */

//            cancel(CancellationException("Error getting result for number $number"))

            /**
             * case 3: cancellation exception is thrown
             */
//            throw CancellationException("Error getting result for number $number")

        }
        return number * 2
    }

    private fun println(message: String) {
        Log.d(TAG, message)
    }
}
