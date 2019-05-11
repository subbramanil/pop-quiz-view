package com.chaturaloka.popquizview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chaturaloka.quizview.QuizView

class MainActivity : AppCompatActivity(), QuizView.QuizCallback {

    private var mQuizQuestion1: QuizView? = null
    private var mQuizQuestion2: QuizView? = null

    override fun onAnswerChanged(view : QuizView) {
        Log.d("MainActivity", "Is Answered Correctly: " + view.isAnsweredCorrectly())
        Log.d("MainActivity", "Weight: " + view.getQuestionWeight())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mQuizQuestion1 = findViewById(R.id.quiz_question_1)
        mQuizQuestion2 = findViewById(R.id.quiz_question_2)

        mQuizQuestion1?.callback = this
        mQuizQuestion2?.callback = this
    }
}
