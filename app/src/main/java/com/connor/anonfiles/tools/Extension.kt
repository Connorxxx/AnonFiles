package com.connor.anonfiles.tools

import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.OnSwipe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.connor.anonfiles.App
import com.drake.brv.listener.DefaultItemTouchCallback
import com.google.android.material.snackbar.Snackbar
import java.util.*

fun View.showSnackBar(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
}

fun String.showToast() {
    Toast.makeText(App.context, this, Toast.LENGTH_LONG).show()
}