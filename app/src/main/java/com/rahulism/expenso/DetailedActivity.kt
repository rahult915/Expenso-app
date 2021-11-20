package com.rahulism.expenso

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction:Transaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        transaction = intent.getSerializableExtra("transaction") as Transaction


        var updateTransactionBtn = findViewById<Button>(R.id.updateTransactionBtn)

        var labelInput = findViewById<EditText>(R.id.labelInput)
        var labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)

        var amountInput = findViewById<EditText>(R.id.amountInput)
        var amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)

        var descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        var descriptionLayout = findViewById<TextInputLayout>(R.id.descriptionLayout)

        var closeBtn = findViewById<ImageButton>(R.id.closeBtn)

        var rootView = findViewById<View>(R.id.rootView)

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }

        labelInput.addTextChangedListener {
            updateTransactionBtn.visibility = View.VISIBLE
            if (it!!.count() > 0) {
                labelLayout.error = null

            }
        }
        amountInput.addTextChangedListener {
            updateTransactionBtn.visibility = View.VISIBLE
            if (it!!.count() > 0) {
                amountLayout.error = null

            }
        }
        descriptionInput.addTextChangedListener {
            updateTransactionBtn.visibility = View.VISIBLE

        }

        updateTransactionBtn.setOnClickListener {
            var label = labelInput.text.toString()
            var amount = amountInput.text.toString().toDoubleOrNull()
            var desc = descriptionInput.text.toString()

            if (label.isEmpty()) {
                labelLayout.error = "Please enter a label"
            } else if (amount == null) {
                amountLayout.error = "Please enter a amount"
            } else {
                val transaction = Transaction(transaction.id, label, amount, desc)
                update(transaction)

            }
        }
        closeBtn.setOnClickListener {
            finish()
        }
    }
    private  fun update(transaction: Transaction)
    {
        var db = Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }

    }

}
