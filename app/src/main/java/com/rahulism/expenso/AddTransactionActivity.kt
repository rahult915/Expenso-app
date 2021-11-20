package com.rahulism.expenso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        var addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)

        var labelInput = findViewById<EditText>(R.id.labelInput)
        var labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)

        var amountInput = findViewById<EditText>(R.id.amountInput)
        var amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)

        var descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        var descriptionLayout = findViewById<TextInputLayout>(R.id.descriptionLayout)

        var closeBtn = findViewById<ImageButton>(R.id.closeBtn)

        var tran_type = findViewById<AutoCompleteTextView>(R.id.tran_type)

        val items = arrayOf("Income","Expense")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        tran_type?.setAdapter(adapter)

        labelInput.addTextChangedListener {
            if (it!!.count() > 0) {
                labelLayout.error = null

            }
        }
        amountInput.addTextChangedListener {
            if (it!!.count() > 0) {
                amountLayout.error = null

            }
        }
        descriptionInput.addTextChangedListener {
            if (it!!.count() > 0) {
                descriptionLayout.error = null

            }
        }

        addTransactionBtn.setOnClickListener {
            var label = labelInput.text.toString()
            var amount = amountInput.text.toString().toDoubleOrNull()
            var desc = descriptionInput.text.toString()

            if(tran_type.text.toString().equals("Expense")){
                amount = -amount!!
            }

            if (label.isEmpty()) {
                labelLayout.error = "Title is Required"
            } else if (amount == null) {
                amountLayout.error = "Amount is Required"
            } else {

                val transaction = Transaction(0, label, amount, desc)
                insert(transaction)

            }
        }
        closeBtn.setOnClickListener {
            finish()
        }
    }
        private  fun insert(transaction: Transaction)
        {
            var db = Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

            GlobalScope.launch {
                db.transactionDao().insertAll(transaction)
                finish()
            }

        }



}