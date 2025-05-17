package com.example.todoapp

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateNewDialogFragment: DialogFragment() {

    companion object {
        private const val ARG_TITLE = "arg_title"

        fun newInstance(title: String): CreateNewDialogFragment {
            val fragment = CreateNewDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            val rootView = inflater.inflate(R.layout.dialog_new, null)
            val titleTextView = rootView.findViewById<TextView>(R.id.tv_new_subtask)
            arguments?.getString(ARG_TITLE)?.let { title ->
                titleTextView.text = title
            }

            builder.setView(rootView)
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