package app.uicomponents.dialogs

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import app.uicomponents.R
import javax.inject.Inject

class DialogProvider @Inject constructor() {

    fun showAndroidDialogMessage(
        context: Context, message: String, onActionClick: () -> Unit, onNegativeClick: () -> Unit
    ) {
        showAndroidDialogMessage(context, null, message, onActionClick, onNegativeClick)
    }


    fun showAndroidDialogMessage(context: Context, title: String, message: String, onActionClick: () -> Unit) {
        showAndroidDialogMessage(context, title, message, onActionClick, {})
    }

    fun showAndroidDialogMessage(
        context: Context, title: String?, message: String, onActionClick: () -> Unit, onNegativeClick: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        if (title.isNullOrEmpty().not())
            builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            dialog.dismiss()
            onActionClick()
        }

        builder.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
            onNegativeClick()
        }
        builder.show()
    }


    fun showNativeErrorDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton(context.resources.getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun showNativeMessageDialog(context: Context, title: String, message: String, onDismiss: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton(context.resources.getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            onDismiss()
        }
        builder.show()
    }

    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        primaryBtn: String,
        secondaryBtn: String,
        primaryBtnAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton(primaryBtn) { dialog, _ ->
            dialog.dismiss()
            primaryBtnAction()
        }
        builder.setNegativeButton(secondaryBtn) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}