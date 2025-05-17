package com.example.todoapp

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout

class CreateSubtaskDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            val dialogView = inflater.inflate(R.layout.subtask_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog?.cancel()
                }

            val dialog = builder.create()

            dialog.setOnShowListener { dialogInterface ->
                val positiveButton =
                    (dialogInterface as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {
                    val titleInputLayout =
                        dialogView.findViewById<TextInputLayout>(R.id.title_input)
                    val title = titleInputLayout.editText?.text.toString() ?: ""
                    if (validateTitle(title, titleInputLayout)) {
                        // TODO: Add subtask to database
                        dialog.dismiss()
                    }
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun validateTitle(title: String, titleInputLayout: TextInputLayout): Boolean {
        if (title.isEmpty()) {
            titleInputLayout.error = "Title is required"
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return false
        }
        titleInputLayout.error = null
        return true
    }
}