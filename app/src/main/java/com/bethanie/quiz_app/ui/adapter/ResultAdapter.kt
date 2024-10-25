package com.bethanie.quiz_app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.core.di.ResourceProvider
import com.bethanie.quiz_app.data.model.Student
import com.bethanie.quiz_app.databinding.LayoutStudentItemBinding

class ResultAdapter(
    private var sortedStudents: List<Student>,
    private var attemptedStudentId: String,
    private val resourceProvider: ResourceProvider
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
                        resourceProvider.getColor(R.color.yellow)
                    )
                }
            }
        }
    }

    private fun showUserOrScore(sortedStudent: Student): String {
        return if (attemptedStudentId == sortedStudent.studentId) {
            resourceProvider.getString(R.string.you)
        } else {
            resourceProvider.getString(R.string.score, sortedStudent.score)
        }
    }

    private fun assignStudentRanks(rank: Int): String {
        return when (rank) {
            1 -> resourceProvider.getString(R.string.firstPlace, rank)
            2 -> resourceProvider.getString(R.string.secondPlace, rank)
            3 -> resourceProvider.getString(R.string.thirdPlace, rank)
            else -> resourceProvider.getString(R.string.otherPlaces, rank)
        }
    }
}