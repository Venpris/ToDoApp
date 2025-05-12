package com.example.todoapp

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateSubtaskDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.subtask_dialog, null))
                .setPositiveButton(R.string.ok
                ) { dialog, id ->
                    //TODO: Add subtask to database
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}