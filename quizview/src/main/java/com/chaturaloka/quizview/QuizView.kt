package com.chaturaloka.quizview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.iterator

class QuizView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var mAnswerWeightsArray: Array<out CharSequence>
    private var mChosenAnswerIndex: Int = 0
    private lateinit var mAnswerWeights: MutableList<Int>
    private var mEqualWeight: Boolean
    private var mRevealAnswer: Boolean
    private var mCorrectAnswerIndex: Int
    private val TAG: String = QuizView::javaClass.name
    private var mAnswersSet: Array<out CharSequence>? = null
    private var mQuizQuestion: String? = null
    private var mNumAnswers: Int = 0
    var callback: QuizCallback? = null

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.QuizView,
            0, 0
        ).apply {

            try {
                mNumAnswers = getInteger(R.styleable.QuizView_numAnswers, 0)
                mCorrectAnswerIndex = getInteger(R.styleable.QuizView_correctAnswer, 0)
                mQuizQuestion = getString(R.styleable.QuizView_quizQuestion)
                mAnswersSet = getTextArray(R.styleable.QuizView_answerSet)
                mRevealAnswer = getBoolean(R.styleable.QuizView_revealAnswer, false)
                mEqualWeight = getBoolean(R.styleable.QuizView_equalWeight, false)
                if (!mEqualWeight) {
                    mAnswerWeightsArray = getTextArray(R.styleable.QuizView_answerWeights)
                }

            } finally {
                recycle()
            }
            if (!mEqualWeight) {
                Log.d(TAG, "List of Weights: ${mAnswerWeightsArray.asList()}")
                mAnswerWeights = mutableListOf()
                for (answerText in mAnswerWeightsArray.asList()) {
                    Log.d(TAG, "Adding: $answerText")
                    mAnswerWeights.add(answerText.toString().toInt())
                }
                Log.d(TAG, "List of Weights: $mAnswerWeights")
            }
        }

        addQuestion()
        addAnswers()
    }

    private fun addQuestion() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val quizQuestionView = inflater
            .inflate(R.layout.quiz_ques_view_layout, this, false) as AppCompatTextView
        quizQuestionView.text = mQuizQuestion
        addView(quizQuestionView)
    }

    private fun addAnswers() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ansGroupView = inflater
            .inflate(R.layout.quiz_ans_group_view_layout, this, false) as RadioGroup
        var ansIndex = 1
        do {
            Log.d("QuizView", "Adding Radio button: $ansIndex")
            val ansView = inflater
                .inflate(R.layout.quiz_ans_view_layout, this, false) as RadioButton
            ansView.text = mAnswersSet?.get(ansIndex - 1)
            ansView.id = ansIndex
            ansGroupView.addView(ansView)
        } while (ansIndex++ < mNumAnswers)

        ansGroupView.setOnCheckedChangeListener { group, checkedId ->
            run {
                Log.d(TAG, "Checked Radio Button: $checkedId")
                if (mRevealAnswer) {
                    mChosenAnswerIndex = checkedId
                    if (mCorrectAnswerIndex == checkedId) {
                        val chosenAnswer = group.findViewById<RadioButton>(mCorrectAnswerIndex)
                        chosenAnswer.setBackgroundColor(resources.getColor(R.color.material_deep_teal_200))
                    } else {
                        clearBackgrounds(group)
                    }
                    callback?.onAnswerChanged(this@QuizView)
                }
            }
        }

        addView(ansGroupView)
    }

    private fun clearBackgrounds(group: RadioGroup?) {
        group?.iterator()?.forEach { radioButton ->
            radioButton.setBackgroundColor(resources.getColor(R.color.white))
        }
    }

    fun getQuestionWeight(): Int {
        return if (mEqualWeight) {
            if (isAnsweredCorrectly()) 1 else 0
        } else {
            mAnswerWeights[mChosenAnswerIndex - 1]
        }
    }

    fun isAnsweredCorrectly(): Boolean {
        return mChosenAnswerIndex == mCorrectAnswerIndex
    }

    interface QuizCallback {
        fun onAnswerChanged(view: QuizView)
    }
}