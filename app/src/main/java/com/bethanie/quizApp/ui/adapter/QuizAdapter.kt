package com.bethanie.quizApp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.di.ResourceProvider
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.databinding.LayoutQuizItemBinding

class QuizAdapter(
    private var quiz: List<Quiz>,
    private val resourceProvider: ResourceProvider
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {
    var listener: Listener? = null
    private var onCopyClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding =
            LayoutQuizItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return QuizViewHolder(binding)
    }

    override fun getItemCount() = quiz.size

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quiz[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setQuizzes(quiz: List<Quiz>) {
        this.quiz = quiz
        notifyDataSetChanged()
    }

    inner class QuizViewHolder(
        private val binding: LayoutQuizItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Quiz) {
            binding.run {
                tvFirst.text = quiz.title
                ibCopy.setOnClickListener { copyQuizId(quiz) }
                cardQuizItem.setOnClickListener { listener?.onClick(quiz) }

                if (quiz.status) {
                    rlCardQuizItem.setBackgroundColor(
                        resourceProvider.getColor(R.color.purple)
                    )
                }
            }
        }
    }

    private fun copyQuizId(quiz: Quiz) {
        resourceProvider.copyToClipboard("CLIPBOARD_LABEL", quiz.quizId)
        onCopyClick?.invoke(quiz.quizId)
    }

    interface Listener {
        fun onClick(quiz: Quiz)
    }

}