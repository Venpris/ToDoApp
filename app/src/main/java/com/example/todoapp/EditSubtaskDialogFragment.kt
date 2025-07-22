package com.example.todoapp

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class EditSubtaskDialogFragment : DialogFragment() {

    interface OnSubtaskEditedListener {
        fun onSubtaskEdited(subtaskId: Int, newTitle: String)
    }

    private var listener: OnSubtaskEditedListener? = null
    private var subtaskId: Int = -1
    private var currentTitle: String = ""

    fun setOnSubtaskEditedListener(listener: OnSubtaskEditedListener) {
        this.listener = listener
    }

    fun setSubtaskData(subtaskId: Int, currentTitle: String) {
        this.subtaskId = subtaskId
        this.currentTitle = currentTitle
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            val dialogView = inflater.inflate(R.layout.subtask_dialog, null)

            val titleTextView = dialogView.findViewById<TextView>(R.id.tv_new_subtask)
            titleTextView.text = getString(R.string.edit_subtask)

            val titleInputLayout = dialogView.findViewById<TextInputLayout>(R.id.title_input)
            titleInputLayout.editText?.setText(currentTitle)

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
                    val title = titleInputLayout.editText?.text.toString()
                    if (validateTitle(title, titleInputLayout)) {
                        listener?.onSubtaskEdited(subtaskId, title)
                        dialog.dismiss()
                    }
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun validateTitle(title: String, titleInputLayout: TextInputLayout): Boolean {
        return if (title.trim().isEmpty()) {
            titleInputLayout.error = "Title cannot be empty"
            false
        } else {
            titleInputLayout.error = null
            true
        }
    }

    companion object {
        fun newInstance(subtaskId: Int, currentTitle: String): EditSubtaskDialogFragment {
            val fragment = EditSubtaskDialogFragment()
            fragment.setSubtaskData(subtaskId, currentTitle)
            return fragment
        }
    }
}