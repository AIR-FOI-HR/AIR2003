package hr.foi.air2003.menzapp.assistants

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.VerifiedInputEvent
import android.view.View
import hr.foi.air2003.menzapp.R
import kotlinx.android.synthetic.main.alert_dialog.view.*

class AlertDialogBuilder {
    private lateinit var view: View

    fun createAlertDialog(context: Context, layoutInflater: LayoutInflater) : AlertDialog.Builder {
        val builder = AlertDialog.Builder(context)
        view = layoutInflater.inflate(R.layout.alert_dialog, null)
        builder.setView(view)
        return builder
    }
}