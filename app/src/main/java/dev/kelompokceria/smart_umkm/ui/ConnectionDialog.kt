package dev.kelompokceria.smart_umkm.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import dev.kelompokceria.smart_umkm.R

class ConnectionDialog(private val context: Context)  {
    fun show(onRetry: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_connection, null)

        val retryButton = dialogView.findViewById<Button>(R.id.retryButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)



        builder.setView(dialogView)
        val dialog = builder.create()

        // Set dialog background to be transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        retryButton.setOnClickListener {
            dialog.dismiss()
            onRetry()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}