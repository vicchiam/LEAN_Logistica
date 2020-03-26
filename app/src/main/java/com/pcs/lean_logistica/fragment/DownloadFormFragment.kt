package com.pcs.lean_logistica.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pcs.lean_logistica.MainActivity
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.adapter.DownloadAdapter
import com.pcs.lean_logistica.model.Download
import com.pcs.lean_logistica.tools.Prefs
import com.pcs.lean_logistica.tools.Router
import com.pcs.lean_logistica.tools.Utils
import java.util.*

class DownloadFormFragment: Fragment() {

    private lateinit var mainActivity: MainActivity

    private lateinit var buttonStart : MaterialButton
    private lateinit var buttonEnd : MaterialButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_download_form, container, false)

        val download: Download = mainActivity.download

        val readOnly: Boolean = (download.start!=null)
        val readOnlyEnd: Boolean = (download.end!=null)

        makeEditTextProviders(view, download.provider, download.name, readOnly)
        makeEditTextOperator(view, download.operators, readOnly)
        makeEditTextPallets(view, download.pallets, readOnlyEnd)
        makeSwitch(view, download.type, readOnly)
        makeButtons(view, download.start, download.end)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            Utils.closeKeyboard(context, mainActivity)

            var hasConsumed = false
            if (v is EditText && event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
            hasConsumed
        }
    }

    private fun makeEditTextProviders(view: View, provider: Int = 0, name: String ="", readOnly: Boolean){
        val editText: EditText = view.findViewById(R.id.edit_provider)
        editText.isEnabled = !readOnly

        if(provider==0)
            editText.setText(name)
        else {
            val text="$provider - $name"
            editText.setText(text)
        }

        editText.onRightDrawableClicked {
            mainActivity.navigateToSelectProvider()
        }

        editText.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    mainActivity.download.provider = 0
                    mainActivity.download.name = s.toString()
                }
            })
    }

    private fun makeEditTextOperator(view: View, num: Int, readOnly: Boolean){
        val editText: EditText = view.findViewById(R.id.edit_num_operators)
        editText.isEnabled=!readOnly

        editText.setText(num.toString())
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty())
                    mainActivity.download.operators = s.toString().toInt()
            }
        })
    }

    private fun makeEditTextPallets(view: View, num: Int, readOnly: Boolean){
        val editText: EditText = view.findViewById(R.id.edit_num_pallets)
        editText.isEnabled=!readOnly

        val textNum= if(num>0) num.toString() else ""

        editText.setText(textNum)
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty())
                    mainActivity.download.pallets = s.toString().toInt()
            }
        })
    }

    private fun makeSwitch(view: View, type: Int = 1, readOnly: Boolean){
        val nameType=if(type==1)"SI" else "NO"

        val textView: TextView = view.findViewById(R.id.text_download_type)
        textView.text = nameType

        val switch: Switch = view.findViewById(R.id.switch_download_type)
        switch.isEnabled=!readOnly
        switch.isChecked = (type==1)

        switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                textView.text = switch.textOn
            else
                textView.text = switch.textOff

            mainActivity.download.type = if(isChecked) 1 else 0
        }
    }

    private fun makeButtons(view: View, start: Date?, end: Date?){
        buttonStart = view.findViewById(R.id.btn_download_start)
        buttonEnd = view.findViewById(R.id.btn_download_end)

        buttonStart.isEnabled = (start==null)
        buttonEnd.isEnabled = (start!=null && end==null)


        buttonStart.setOnClickListener {
            if(!mainActivity.download.isValid())
                Utils.alert(context!!, "Debes rellenar todos los campos")
            else
                saveStart()
        }

        buttonEnd.setOnClickListener {
            saveEnd()
        }
    }

    private fun saveStart(){
        mainActivity.download.start = Date()

        val prefs = Prefs(mainActivity)
        val url: String = prefs.settingsUrl
        val center: Int = prefs.settingsCenter

        val params = HashMap<String,String>()
        params["action"]="add-download"
        params["center"]=center.toString()
        params["id_device"]=mainActivity.idApp.toString()
        params["provider"]=mainActivity.download.provider.toString()
        params["name"]=mainActivity.download.name
        params["operators"]=mainActivity.download.operators.toString()
        params["pallets"]=mainActivity.download.pallets.toString()
        params["type"]=mainActivity.download.type.toString()
        params["start"]=Utils.dateToString(mainActivity.download.start!!, "yyyy-MM-dd HH:mm:ss")
        if(mainActivity.download.end!=null)
            params["end"]=Utils.dateToString(mainActivity.download.end!!, "yyyy-MM-dd HH:mm:ss")
        else
            params["end"]=""

        if(url.isNotEmpty()){
            val dialog = Utils.modalAlert(mainActivity, "Guardando")
            dialog.show()
            Router.post(
                context = context!!,
                url = url,
                params = params,
                responseListener = { response ->
                    if (context != null){
                        if (response.toLongOrNull()!=null)
                            saveStartOk(response.toLong())
                        else
                            saveStartError(response)
                        dialog.dismiss()
                    }
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

    private fun saveStartOk(id: Long){
        buttonStart.isEnabled = false
        buttonEnd.isEnabled = true

        mainActivity.download.id = id

        mainActivity.download.position = mainActivity.listDownload.size
        mainActivity.listDownload.add(mainActivity.download)
        (mainActivity.currentAdapter as DownloadAdapter).update()
        mainActivity.cleanFragment()
    }

    private fun saveStartError(response: String){
        Utils.alert(context!!, "Error: $response")
        mainActivity.download.start = null
    }

    private fun saveEnd(){
        Log.d("POSITION",mainActivity.download.position.toString())
        Log.d("LENGTH", mainActivity.listDownload.size.toString())
        mainActivity.listDownload[mainActivity.download.position].end = Date()

        val prefs = Prefs(mainActivity)
        val url: String = prefs.settingsUrl

        if(url.isNotEmpty()){
            val dialog = Utils.modalAlert(mainActivity, "Guardando")
            dialog.show()
            Router.get(
                context = context!!,
                url = url,
                params = "action=end-download&id=${mainActivity.download.id}&pallets=${mainActivity.download.pallets}",
                responseListener = { response ->
                    if (context != null){
                        if (response=="ok")
                            saveEndOk()
                        else
                            saveEndError(response)
                        dialog.dismiss()
                    }
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

    private fun saveEndOk(){
        buttonEnd.isEnabled = false
        (mainActivity.currentAdapter as DownloadAdapter).update()
        mainActivity.cleanFragment()
    }

    private fun saveEndError(response: String){
        Utils.alert(context!!, "Error: $response")
        mainActivity.listDownload[mainActivity.download.position].end = null
    }


}