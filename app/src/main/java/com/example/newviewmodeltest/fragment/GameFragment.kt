package com.example.newviewmodeltest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.newviewmodeltest.R
import com.example.newviewmodeltest.databinding.FragmentGameBinding
import com.example.newviewmodeltest.viewmodel.GameViewModel
import com.example.newviewmodeltest.viewmodel.MAX_NO_OF_WORDS
import com.example.newviewmodeltest.viewmodel.SCORE_INCREASE
import com.example.newviewmodeltest.viewmodel.allWordsList
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Error

/**
 * Created by ZhangSilong on 2022/5/28.
 */
class GameFragment : Fragment() {

    private lateinit var gameBinding: FragmentGameBinding
    private val viewModel: GameViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameBinding = FragmentGameBinding.inflate(inflater, container, false)
        Log.d("GameFragment", "GameFragment created/re-created!")
        return gameBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameBinding.submit.setOnClickListener {
            onSubmitWord()
        }
        gameBinding.skip.setOnClickListener {
            onSkipWord()
        }
        viewModel.currentScrambledWord.observe(viewLifecycleOwner) {
            gameBinding.textViewUnscrambledWord.text = it
        }
        viewModel.score.observe(viewLifecycleOwner) {
            gameBinding.score.text = getString(R.string.score, it)
        }
        viewModel.currentWordCount.observe(viewLifecycleOwner) {
            gameBinding.wordCount.text = getString(R.string.word_count, it, MAX_NO_OF_WORDS)
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }

    private fun onSubmitWord() {
        val playerWord = gameBinding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Congratulations!")
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton("exit") { _, _ ->
                exitGame()
            }
            .setPositiveButton("play again") { _, _ ->
                restartGame()
            }
            .show()
    }


    private fun exitGame() {
        activity?.finish()
    }

    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            gameBinding.textField.isErrorEnabled = true
            gameBinding.textField.error = getString(R.string.try_again)
        } else {
            gameBinding.textField.isErrorEnabled = false
            gameBinding.textInputEditText.text = null
        }
    }


}