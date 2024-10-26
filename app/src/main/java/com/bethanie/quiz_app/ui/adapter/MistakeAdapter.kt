package com.bethanie.quiz_app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.core.Constants
import com.bethanie.quiz_app.core.di.ResourceProvider
import com.bethanie.quiz_app.data.model.Mistake
import com.bethanie.quiz_app.databinding.LayoutMistakeBinding

class MistakeAdapter(
    private var mistakes: List<Mistake>,
    private val resourceProvider: ResourceProvider
) : RecyclerView.Adapter<MistakeAdapter.MistakeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeViewHolder {
        val binding =
            LayoutMistakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MistakeViewHolder(binding)
    }

    override fun getItemCount() = mistakes.size

    override fun onBindViewHolder(holder: MistakeViewHolder, position: Int) {
        holder.bind(mistakes[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMistakes(mistakes: List<Mistake>) {
        this.mistakes = mistakes
        notifyDataSetChanged()
    }

    inner class MistakeViewHolder(
        private val binding: LayoutMistakeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mistake: Mistake) {
            binding.run {
                tvQuesTitle.text = resourceProvider.getString(
                    R.string.ques_title,
                    mistake.questionNumber,
                    mistake.title
                )
                btnChoice1.text = mistake.choices?.getOrNull(0) ?: ""
                btnChoice2.text = mistake.choices?.getOrNull(1) ?: ""
                btnChoice3.text = mistake.choices?.getOrNull(2) ?: ""
                btnChoice4.text = mistake.choices?.getOrNull(3) ?: ""
                changeColorsForAnswers(mistake, binding)
            }
        }
    }

    private fun changeColorsForAnswers(mistake: Mistake, binding: LayoutMistakeBinding) {
        binding.run {
            val choiceViews = listOf(btnChoice1, btnChoice2, btnChoice3, btnChoice4)
            val correctChoiceButton = choiceViews.find { it.text == mistake.correctAnswer }

            choiceViews.forEach { choice ->
                if (mistake.chosenAnswer == Constants.UNANSWERED) {
                    tvUnanswered.isVisible = true

                    correctChoiceButton?.backgroundTintList =
                        resourceProvider.getColorStateList(R.color.coral)

                } else {
                    tvUnanswered.isVisible = false
                    choiceViews.forEach { _ ->
                        when (choice.text) {
                            mistake.chosenAnswer -> {
                                choice.backgroundTintList =
                                    resourceProvider.getColorStateList(R.color.coral)
                            }

                            mistake.correctAnswer -> {
                                choice.backgroundTintList =
                                    resourceProvider.getColorStateList(R.color.green)
                            }
                        }
                    }
                }
            }
        }
    }
}
