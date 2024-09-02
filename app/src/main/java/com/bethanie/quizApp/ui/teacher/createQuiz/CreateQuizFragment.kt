package com.bethanie.quizApp.ui.teacher.createQuiz

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.utils.DateTimeUtil
import com.bethanie.quizApp.data.model.Question
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.databinding.FragmentCreateQuizBinding
import com.bethanie.quizApp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import kotlin.random.Random

@AndroidEntryPoint
class CreateQuizFragment : BaseFragment<FragmentCreateQuizBinding>() {
    override val viewModel: CreateQuizViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_create_quiz

    private var quiz: Quiz? = null
    private var fileName: String = ""
    private var quizId: String = ""
    private var userId: String = ""
    private var quizTitle: String = ""
    private var quizSubject: String = ""

    override fun onBindView(view: View) {
        super.onBindView(view)
        getTeacherUserId()
        chooseSubjectFromDropDown()

        binding?.run {
            navbar.ibBack.setOnClickListener { findNavController().popBackStack() }
            navbar.ibLogout.setOnClickListener { showLogOutAlertDialog() }
            navbar.tvTitle.text = getString(R.string.createQuiz)
            val randomNum = Random.nextInt(100000, 999999).toString()
            randomNum.let {
                tvRandomQuizId.text = it
                quizId = it
            }

            btnImportCSV.setOnClickListener { clickingBtnImportCSV(view) }
        }
    }

    private fun getTeacherUserId() {
        lifecycleScope.launch {
            userId = viewModel.authService.getUid().toString()
        }
    }

    private fun clickingBtnImportCSV(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(
                Intent.createChooser(intent, getString(R.string.selectCSVFile)),
                100
            )
        } catch (e: Exception) {
            showSnackBar(view, getString(R.string.installFileManager), true)
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        binding?.btnPublishQuiz?.setOnClickListener {
            quizTitle = binding?.etQuizTitle?.text.toString()

            try {
                viewModel.checkInputIfEmpty(fileName, quizTitle)
                handleUploadCSV(view)
            } catch (e: Exception) {
                showSnackBar(
                    view,
                    "Please ensure that you have done all 3 steps above 😌",
                    true
                )
            }
        }
    }

    private fun chooseSubjectFromDropDown() {
        val subjects = resources.getStringArray(R.array.subjects)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, subjects)
        binding?.autoCompleteTextView?.setAdapter(arrayAdapter)
        binding?.autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
            quizSubject = subjects[position]
        }
    }

    private fun handleUploadCSV(view: View) {
        lifecycleScope.launch {
            quiz?.let { quiz ->
                viewModel.uploadCSV(quiz.copy(title = quizTitle, subject = quizSubject))
                viewModel.success.collect {
                    findNavController().navigate(
                        CreateQuizFragmentDirections.createQuizToTeacherHome()
                    )
                    showSnackBar(view, getString(R.string.quizAddedSuccessfully))
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.data
            fileName =
                uri?.let { getFileNameFromUri(requireContext().contentResolver, it) }.toString()

            binding?.let { handleBtnImportCSVUI(it, fileName, requireContext()) }
            quiz = uri?.let {
                readCsv(
                    requireContext(),
                    requireContext().contentResolver.openInputStream(it)!!,
                    quizId, quizTitle, quizSubject, userId
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}

private fun handleBtnImportCSVUI(
    binding: FragmentCreateQuizBinding,
    fileName: String,
    context: Context
) {

    binding.btnImportCSV.text = fileName.substringBeforeLast(".")
    binding.btnImportCSV.icon =
        ContextCompat.getDrawable(context, R.drawable.ic_file_download_done)

    binding.btnImportCSV.setBackgroundColor(
        ContextCompat.getColor(context, R.color.purple)
    )
}

@SuppressLint("Range")
private fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String? {
    var fileName: String? = null
    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }
    return fileName
}

private fun readCsv(
    context: Context,
    inputStream: InputStream,
    quizId: String,
    quizTitle: String,
    quizSubject: String,
    userId: String
): Quiz {
    val csvParser = createCsvParser(inputStream)
    val questions = parseQuestions(csvParser)
    val totalTimeInSecs = questions.sumOf { it.time }
    val formattedTime = DateTimeUtil.formatTime(context, totalTimeInSecs)

    return Quiz(
        quizId = quizId,
        title = quizTitle,
        subject = quizSubject,
        questions = questions,
        totalTime = formattedTime,
        dateCreated = DateTimeUtil.getCurrentDate(),
        createdBy = userId
    )
}

private fun createCsvParser(inputStream: InputStream): CSVParser {
    return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
        setHeader()
        setSkipHeaderRecord(true)
        setIgnoreSurroundingSpaces(true)
    }.build().parse(inputStream.reader())
}

private fun parseQuestions(csvParser: CSVParser): List<Question> {
    return csvParser.map {
        Question(
            title = it["Question"].toString(),
            choices = listOf(
                it["Choice 1"].toString(),
                it["Choice 2"].toString(),
                it["Choice 3"].toString(),
                it["Choice 4"].toString()
            ),
            correctAnswer = it["Correct Answer"].toString().trim(),
            time = it["Time"].toInt()
        )
    }
}



