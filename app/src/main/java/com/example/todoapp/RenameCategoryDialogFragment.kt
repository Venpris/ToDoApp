package com.example.todoapp

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class RenameCategoryDialogFragment: DialogFragment() {
    companion object {
        private const val ARG_CATEGORY_ID = "category_id"
        private const val ARG_CATEGORY_NAME = "category_name"

        fun newInstance(
            categoryId: Int,
            categoryName: String
        ): RenameCategoryDialogFragment {
            val fragment = RenameCategoryDialogFragment()
            val args = Bundle()
            args.putInt(ARG_CATEGORY_ID, categoryId)
            args.putString(ARG_CATEGORY_NAME, categoryName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val categoryId = arguments?.getInt(ARG_CATEGORY_ID) ?: -1
        val categoryName = arguments?.getString(ARG_CATEGORY_NAME) ?: ""

        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater

            val dialogView = inflater.inflate(R.layout.rename_category_dialog, null)
            
            val titleInputLayout = dialogView.findViewById<TextInputLayout>(R.id.title_input)
            titleInputLayout.editText?.setText(categoryName)

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
                        parentFragmentManager.setFragmentResult(
                            "categoryNewNameRequest",
                            bundleOf("categoryId" to categoryId, "newName" to title)
                        )
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