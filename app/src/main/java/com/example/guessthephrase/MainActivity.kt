package com.example.guessthephrase

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var myLayout: ConstraintLayout
    lateinit var tvPhrase: TextView
    lateinit var tvLetters: TextView
    lateinit var editText: EditText
    lateinit var button: Button
    private lateinit var messages: ArrayList<String>
    val phrase = "All that glitters is not gold".uppercase()
    var counter = 0
    val answerDictionary = mutableMapOf<Int,Char>()
    var answer = ""
    var guessedLetters = ""
    var guessPhrase = true

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myLayout = findViewById(R.id.clMain)
        tvPhrase = findViewById(R.id.tvPhrase)
        tvLetters = findViewById(R.id.tvLetters)
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        messages = ArrayList()

        val rvMessages = findViewById<RecyclerView>(R.id.rvMessages)
        rvMessages.adapter = Adapter(this, messages)
        rvMessages.layoutManager = LinearLayoutManager(this)
        tvPhrase.text = "Guess: $answer"
        button.setOnClickListener { message() }
        updateText()

        for (i in phrase.indices) {
            if (phrase[i] != ' ') {
                answerDictionary[i] = '*'
                answer += '*'
            }else{
                answerDictionary[i] = ' '
                answer += ' '
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun message(){
            val msg = editText.text.toString().uppercase()

            if(guessPhrase){
                if(msg == phrase){
                    disableEntry()
                    showAlertDialog("You win!\n\nPlay again?")
                }else{
                    messages.add("Wrong guess: $msg")
                    guessPhrase = false
                    updateText()
                }
            }else{
                if(msg.isNotEmpty() && msg.length==1){
                    answer = ""
                    guessPhrase = true
                    checkLetters(msg[0])
                }else{
                    Snackbar.make(myLayout, "Please enter one letter only", Snackbar.LENGTH_LONG).show()
                }
            }
        editText.text.clear()
        editText.clearFocus()
        rvMessages.adapter?.notifyDataSetChanged()
    }

    private fun disableEntry(){
        button.isEnabled = false
        button.isClickable = false
        editText.isEnabled = false
        editText.isClickable = false
    }

        @SuppressLint("SetTextI18n")
        private fun updateText(){
            tvPhrase.text = "Phrase: $answer"
            tvLetters.text = "Guessed Letters: " + guessedLetters
            if(guessPhrase){
                editText.hint = "Guess the full phrase"
            }else{
                editText.hint = "Guess a letter"
            }
        }

        private fun checkLetters(guessedLetter: Char){
            var found = 0
            for(i in phrase.indices){
                if(phrase[i] == guessedLetter.uppercaseChar()){
                    answerDictionary[i] = guessedLetter
                    found++
                }
            }
            for(i in answerDictionary){answer += answerDictionary[i.key]}
            if(answer==phrase){
                disableEntry()
                showAlertDialog("You win!\n\nPlay again?")
            }
            if(guessedLetters.isEmpty()){guessedLetters+=guessedLetter}else{guessedLetters+=", "+guessedLetter}
            if(found>0){
                messages.add("Found $found ${guessedLetter.uppercase()}(s)")
            }else{
                messages.add("No ${guessedLetter.uppercase()}s found")
            }
            counter++
            val guessesLeft = 10 - counter
            if(counter<10){messages.add("$guessesLeft guesses remaining")}else{showAlertDialog("Want to try again?")}
            updateText()
            rvMessages.scrollToPosition(messages.size - 1)
        }

    private fun showAlertDialog(title: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(title)
            .setCancelable(false)

            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id -> this.recreate()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })


        val alert = dialogBuilder.create()
        alert.setTitle("Game Over")
        alert.show()
    }
}