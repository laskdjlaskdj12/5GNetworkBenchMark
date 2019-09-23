package com.example.a5gnetworkbenchmark.utils

object BenchmarkUtils {

    fun bitToKiloBit(bit:Double):Double {
        return bit / (1024)
    }

    fun bitToMegaBit(bit:Double):Double{
        return bit / (1024 * 1024)
    }
}