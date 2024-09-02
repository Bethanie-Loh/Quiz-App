package com.bethanie.quizApp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bethanie.quizApp.R
import com.bethanie.quizApp.data.model.Student
import com.bethanie.quizApp.databinding.LayoutQuizItemBinding
import com.bethanie.quizApp.databinding.LayoutStudentItemBinding

class ResultAdapter(
    private var sortedStudents: List<Student>,
    private var attemptedStudentId: String,
    var context: Context
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding =
            LayoutStudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ResultViewHolder(binding)
    }

    override fun getItemCount() = sortedStudents.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(sortedStudents[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setResults(sortedStudents: List<Student>) {
        this.sortedStudents = sortedStudents
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAttemptedStudentId(newAttemptedStudentId: String) {
        attemptedStudentId = newAttemptedStudentId
        notifyDataSetChanged()
    }

    inner class ResultViewHolder(
        private val binding: LayoutStudentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sortedStudent: Student) {
            binding.run {
                tvFirst.text = sortedStudent.studentName
                tvSecond.text = showUserOrScore(sortedStudent)
                tvThird.text = assignStudentRanks(adapterPosition + 1)

                if (attemptedStudentId == sortedStudent.studentId) {
                    rlCardQuizItem.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.yellow
                        )
                    )
                }
            }
        }
    }

    private fun showUserOrScore(sortedStudent: Student): String {
        return if (attemptedStudentId == sortedStudent.studentId) {
            ContextCompat.getString(context, R.string.you)

        } else {
            String.format(ContextCompat.getString(context, R.string.score), sortedStudent.score)
        }
    }

    private fun assignStudentRanks(rank: Int): String {
        return when (rank) {
            1 -> String.format(ContextCompat.getString(context, R.string.firstPlace), rank)
            2 -> String.format(ContextCompat.getString(context, R.string.secondPlace), rank)
            3 -> String.format(ContextCompat.getString(context, R.string.thirdPlace), rank)
            else -> String.format(ContextCompat.getString(context, R.string.otherPlaces), rank)
        }
    }
}