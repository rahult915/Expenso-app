package com.rahulism.expenso

import android.app.ActionBar
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var deleteTransaction : Transaction

    private lateinit var transactions : List<Transaction>
    private lateinit var oldTransactions : List<Transaction>
    private lateinit var transactionAdapter : TransactionAdapter
    private lateinit var layout_manager : LinearLayoutManager

    private lateinit var totalbalance : TextView
    private lateinit var expense : TextView
    private lateinit var budget : TextView

    private lateinit var floatingActionButton : FloatingActionButton

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       //window.statusBarColor = ContextCompat.getColor(this,R.color.white)

        totalbalance = findViewById(R.id.balance)
        budget = findViewById(R.id.budget)
        expense = findViewById(R.id.expense)
        floatingActionButton = findViewById(R.id.floatingActionButton)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        layout_manager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,AppDatabase::class.java,"transactions").build()

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        recyclerview.apply {
            adapter = transactionAdapter
            layoutManager = layout_manager
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }

        }
        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerview)

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this,AddTransactionActivity::class.java))
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        deleteTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id != transaction.id }
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackBar()

            }
        }
    }

    private fun showSnackBar() {
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view,"Transaction deleted!!",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()

        }.setActionTextColor(ContextCompat.getColor(this,R.color.red))
        .setTextColor(ContextCompat.getColor(this,R.color.white)).show()
    }

    private fun undoDelete() {
        GlobalScope.launch {
            db.transactionDao().insertAll(deleteTransaction)
            transactions = oldTransactions
            runOnUiThread{
                transactionAdapter.setData(transactions)
                updateDashboard()

            }
        }
    }

    private fun fetchAll()
    {
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }


    }
    private fun updateDashboard()
    {
        val totalAmount = transactions.map{it.amount}.sum()
        val budgetAmount = transactions.filter{it.amount>0}.map { it.amount }.sum()
        val expenseAmount = totalAmount - budgetAmount

        totalbalance.text="₹%.2f".format(totalAmount)
        budget.text="₹%.2f".format(budgetAmount)
        expense.text="₹%.2f".format(expenseAmount)

        if(totalAmount < 0)
        {
            totalbalance.setTextColor(ContextCompat.getColor(this,R.color.red))

        }
        else if(totalAmount > 0)
        {
            totalbalance.setTextColor(ContextCompat.getColor(this,R.color.green))

        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}