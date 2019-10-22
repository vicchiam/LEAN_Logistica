package com.pcs.lean_logistica.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pcs.lean_logistica.MainActivity
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.model.Download
import com.pcs.lean_logistica.tools.Utils
import java.util.*

class DownloadFragment: Fragment() {

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)

        makeFloatingActionButton(view)
        makeSearchDate(view)

        return view
    }

    private fun makeFloatingActionButton(view: View){
        val actionButton: FloatingActionButton = view.findViewById(R.id.fab_download)
        actionButton.setOnClickListener { _ ->
            mainActivity.download = Download()
            mainActivity.navigateToNewDownload()
        }
    }

    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            Utils.closeKeyboard(context, mainActivity)

            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

    private fun makeSearchDate(view: View, date: Date = Date()){
        val editText: EditText = view.findViewById(R.id.search_date)
        editText.setText(Utils.dateToString(date))
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val aux: String=dayOfMonth.toString().padStart(2,'0')+"/"+(monthOfYear+1).toString().padStart(2,'0')+"/"+year.toString()
                editText.setText(aux)
            }
        editText.onRightDrawableClicked {
            val cal = Calendar.getInstance()
            cal.time = Utils.stringToDate(editText.text.toString())
            Log.d("TIME", Utils.stringToDate(editText.text.toString()).toString())
            DatePickerDialog(context!!,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

}