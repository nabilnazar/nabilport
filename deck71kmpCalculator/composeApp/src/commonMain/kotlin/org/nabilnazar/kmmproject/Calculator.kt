package org.nabilnazar.kmmproject

class Calculator {
    fun add(a: Double, b: Double): Double = a + b
    fun subtract(a: Double, b: Double): Double = a - b
    fun multiply(a: Double, b: Double): Double = a * b
    fun divide(a: Double, b: Double): Double {
        require(b != 0.0) { "Division by zero is not allowed." }
        return a / b
    }
}