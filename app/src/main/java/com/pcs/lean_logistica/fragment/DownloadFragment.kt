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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pcs.lean_logistica.MainActivity
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.adapter.DownloadAdapter
import com.pcs.lean_logistica.model.Download
import com.pcs.lean_logistica.tools.Prefs
import com.pcs.lean_logistica.tools.Router
import com.pcs.lean_logistica.tools.Utils
import java.util.*

class DownloadFragment: Fragment() {

    private lateinit var mainActivity: MainActivity

    private val adapter = DownloadAdapter()

    private lateinit var recycler: RecyclerView

    private var currentDate: Date = Date()

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

        recycler = view.findViewById(R.id.recycler_download)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)

        adapter.DownloadAdapter(this, mainActivity.listDownload)
        recycler.adapter = adapter

        if(mainActivity.listDownload.isEmpty()){
            getDownloads()
        }
        else{
            adapter.DownloadAdapter(this, mainActivity.listDownload)
            recycler.adapter = adapter
        }
        adapter.search(Utils.dateToString(currentDate)){}

        mainActivity.currentAdapter = adapter
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
                currentDate = Utils.stringToDate(aux)
                adapter.search(aux){}
            }
        editText.onRightDrawableClicked {
            val cal = Calendar.getInstance()
            cal.time = Utils.stringToDate(editText.text.toString())
            DatePickerDialog(context!!,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun getDownloads(){
        val prefs = Prefs(mainActivity)
        val url: String = prefs.settingsUrl

        if(url.isNotEmpty()){
            val dialog = Utils.modalAlert(mainActivity, "Guardando")
            dialog.show()
            Router.get(
                context = context!!,
                url = url,
                params = "action=get-downloads",
                responseListener = { response ->
                    if(context!=null){
                        val list: List<Download> = Utils.fromJson(response)
                        mainActivity.listDownload = list.toMutableList()
                        adapter.DownloadAdapter(this, mainActivity.listDownload)
                        recycler.adapter = adapter
                        adapter.search(Utils.dateToString(currentDate)){}
                    }
                    dialog.dismiss()
                },
                errorListener = { err ->
                    if(context!=null){
                        Utils.alert(context!!, err)
                        dialog.dismiss()
                    }
                }
            )
        }
    }

    fun editDownload(download: Download){
        mainActivity.download = download
        mainActivity.navigateToNewDownload()
    }

}