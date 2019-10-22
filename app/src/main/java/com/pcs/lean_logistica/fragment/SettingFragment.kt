package com.pcs.lean_logistica.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.tools.Prefs
import com.pcs.lean_logistica.tools.Utils

class SettingFragment : Fragment(){

    private lateinit var prefs : Prefs

    private lateinit var spinner: Spinner
    private lateinit var editUrl: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val view: View =inflater.inflate(R.layout.fragment_settings,container,false)

        prefs = Prefs(this.context!!)

        val textView: TextView = view.findViewById(R.id.app_id)
        textView.text = prefs!!.idApp.toString()

        val data = resources.getStringArray(R.array.centers)
        val adapter = ArrayAdapter(context!!, R.layout.spinner_item_selected, data)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner = view.findViewById(R.id.spinner_center)
        spinner.adapter = adapter

        val url = prefs!!.settingsUrl
        editUrl = view.findViewById(R.id.edit_url)
        editUrl.setText(url)

        var button: MaterialButton = view.findViewById(R.id.btn_settings)
        button.setOnClickListener { _ ->
            save()
        }

        return view
    }

    private fun isValid() :Boolean {
        if(editUrl.text.isEmpty()){
            Utils.alert(context!!, "Debes indicar una URL")
            return false
        }
        return true
    }

    private fun save(){
        if(isValid()){
            val position: Int = spinner.selectedItemPosition
            prefs!!.settingsCenter = position

            val url = editUrl.text.toString()
            prefs!!.settingsUrl = url
            Utils.alert(context!!, "Guardado correctamente")
        }
    }

}