package android.bignerdranch.filesaver

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

private const val CAUTION_TITLE = "Предупреждение"
private const val DENY_MESSAGE = "Без доступа к хранилищу добавленные файлы не сохранятся"
private const val OK_BUTTON = "ОК"
private const val NULL_EXCEPTION_MESSAGE = "Activity cannot be null"

class MyDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(CAUTION_TITLE)
                .setMessage(DENY_MESSAGE)
                .setPositiveButton(OK_BUTTON) { dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException(NULL_EXCEPTION_MESSAGE)
    }
}