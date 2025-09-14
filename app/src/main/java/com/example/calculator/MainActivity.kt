package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView

    private var currentNumber = "0"
    private var operator = ""
    private var previousNumber = ""
    private var isNewOperation = true
    private var hasDecimalPoint = false

    private val decimalFormat = DecimalFormat("#.#########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.tvDisplay)

        setupNumberButtons()
        setupOperatorButtons()
        setupFunctionButtons()
    }

    private fun setupNumberButtons() {
        val numberButtons = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtons.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                onNumberClick(index.toString())
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {
            onDecimalClick()
        }
    }

    private fun setupOperatorButtons() {
        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperatorClick("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperatorClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorClick("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorClick("÷") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
    }

    private fun setupFunctionButtons() {
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { onPlusMinusClick() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentClick() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspaceClick() }
    }

    private fun onNumberClick(number: String) {
        if (isNewOperation) {
            currentNumber = number
            isNewOperation = false
            hasDecimalPoint = false
        } else {
            if (currentNumber == "0" && number != "0") {
                currentNumber = number
            } else if (currentNumber.length < 15) { // Limite de dígitos
                currentNumber += number
            }
        }
        updateDisplay()
    }

    private fun onDecimalClick() {
        if (isNewOperation) {
            currentNumber = "0."
            isNewOperation = false
            hasDecimalPoint = true
        } else if (!hasDecimalPoint && currentNumber.length < 14) {
            currentNumber += "."
            hasDecimalPoint = true
        }
        updateDisplay()
    }

    private fun onOperatorClick(newOperator: String) {
        if (operator.isNotEmpty() && !isNewOperation) {
            calculate()
        }

        previousNumber = currentNumber
        operator = newOperator
        isNewOperation = true
        hasDecimalPoint = false
    }

    private fun onEqualsClick() {
        if (operator.isNotEmpty() && previousNumber.isNotEmpty()) {
            calculate()
            operator = ""
            previousNumber = ""
            isNewOperation = true
            hasDecimalPoint = currentNumber.contains(".")
        }
    }

    private fun calculate() {
        try {
            val prev = previousNumber.toDouble()
            val current = currentNumber.toDouble()
            val result = when (operator) {
                "+" -> prev + current
                "-" -> prev - current
                "×" -> prev * current
                "÷" -> {
                    if (current == 0.0) {
                        showError("Erro")
                        return
                    }
                    prev / current
                }
                else -> current
            }

            // Formatar o resultado
            currentNumber = if (result == result.toLong().toDouble()) {
                result.toLong().toString()
            } else {
                decimalFormat.format(result)
            }

        } catch (e: Exception) {
            showError("Erro")
            return
        }

        updateDisplay()
    }

    private fun onClearClick() {
        currentNumber = "0"
        operator = ""
        previousNumber = ""
        isNewOperation = true
        hasDecimalPoint = false
        updateDisplay()
    }

    private fun onPlusMinusClick() {
        if (currentNumber != "0") {
            currentNumber = if (currentNumber.startsWith("-")) {
                currentNumber.substring(1)
            } else {
                "-$currentNumber"
            }
            updateDisplay()
        }
    }

    private fun onPercentClick() {
        try {
            val value = currentNumber.toDouble()
            currentNumber = decimalFormat.format(value / 100)
            hasDecimalPoint = currentNumber.contains(".")
            updateDisplay()
        } catch (e: Exception) {
            showError("Erro")
        }
    }

    private fun onBackspaceClick() {
        if (!isNewOperation && currentNumber.length > 1) {
            val lastChar = currentNumber.last()
            if (lastChar == '.') {
                hasDecimalPoint = false
            }
            currentNumber = currentNumber.dropLast(1)
        } else {
            currentNumber = "0"
            isNewOperation = true
            hasDecimalPoint = false
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        // Limitar o número de caracteres exibidos
        val displayText = if (currentNumber.length > 12) {
            formatLargeNumber(currentNumber.toDoubleOrNull() ?: 0.0)
        } else {
            currentNumber
        }
        display.text = displayText
    }

    private fun formatLargeNumber(number: Double): String {
        return when {
            abs(number) >= 1_000_000_000 -> String.format("%.2e", number)
            abs(number) >= 1_000_000 -> decimalFormat.format(number / 1_000_000) + "M"
            abs(number) >= 1_000 -> decimalFormat.format(number / 1_000) + "K"
            else -> decimalFormat.format(number)
        }
    }

    private fun showError(message: String) {
        display.text = message
        currentNumber = "0"
        operator = ""
        previousNumber = ""
        isNewOperation = true
        hasDecimalPoint = false
    }
}