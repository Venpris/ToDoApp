package com.example.todoapp

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RenameCategoryDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.rename_category_dialog, null))
                .setPositiveButton(R.string.ok
                ) { dialog, id ->
                    //TODO: Update category
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}