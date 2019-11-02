package com.pcs.lean_logistica.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.pcs.lean_logistica.MainActivity
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.adapter.UploadAdapter
import com.pcs.lean_logistica.model.Upload
import com.pcs.lean_logistica.tools.Prefs
import com.pcs.lean_logistica.tools.Router
import com.pcs.lean_logistica.tools.Utils
import java.util.*

class UploadFormFragment: Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_upload_form, container, false)

        val upload: Upload = mainActivity.upload

        val readOnly: Boolean = (upload.start!=null)
        val readOnlyEnd: Boolean = (upload.end!=null)

        makeEditTextDock(view, upload.dock, readOnly)
        makeEditTextOperator(view, upload.operators, readOnly)


        makeEditTextPallets(view, upload.pallets, readOnlyEnd)
        makeButtons(view, upload.start , upload.end)

        return view
    }

    private fun makeEditTextDock(view: View, num: Int, readOnly: Boolean){
        val editText: EditText = view.findViewById(R.id.edit_dock)
        editText.isEnabled=!readOnly

        val textNum= if(num>0) num.toString() else ""

        editText.setText(textNum)
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty())
                    mainActivity.upload.dock = s.toString().toInt()
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
                    mainActivity.upload.operators = s.toString().toInt()
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
                    mainActivity.upload.pallets = s.toString().toInt()
            }
        })
    }

    private fun makeButtons(view: View, start: Date?, end: Date?){
        buttonStart = view.findViewById(R.id.btn_upload_start)
        buttonEnd = view.findViewById(R.id.btn_upload_end)

        buttonStart.isEnabled = (start==null)
        buttonEnd.isEnabled = (start!=null && end==null)


        buttonStart.setOnClickListener {
            if(!mainActivity.upload.isValid())
                Utils.alert(context!!, "Debes rellenar todos los campos")
            else
                saveStart()
        }

        buttonEnd.setOnClickListener {
            saveEnd()
        }
    }

    private fun saveStart(){
        mainActivity.upload.start = Date()

        val prefs = Prefs(mainActivity)
        val url: String = prefs.settingsUrl
        val center: Int = prefs.settingsCenter

        val params = HashMap<String,String>()
        params["action"]="add-upload"
        params["center"]=center.toString()
        params["id_device"]=mainActivity.idApp.toString()
        params["dock"]=mainActivity.upload.dock.toString()
        params["operators"]=mainActivity.upload.operators.toString()
        params["pallets"]=mainActivity.upload.operators.toString()
        params["start"]=Utils.dateToString(mainActivity.upload.start!!, "yyyy-MM-dd HH:mm:ss")
        if(mainActivity.upload.end!=null)
            params["end"]=Utils.dateToString(mainActivity.upload.end!!, "yyyy-MM-dd HH:mm:ss")
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

        mainActivity.upload.id = id

        mainActivity.upload.position = mainActivity.listUpload.size
        mainActivity.listUpload.add(mainActivity.upload)
        (mainActivity.currentAdapter as UploadAdapter).update()
        mainActivity.cleanFragment()
    }

    private fun saveStartError(response: String){
        Utils.alert(context!!, "Error: $response")
        mainActivity.upload.start = null
    }

    private fun saveEnd(){
        Log.d("POSITION",mainActivity.upload.position.toString())
        Log.d("LENGTH", mainActivity.listUpload.size.toString())
        mainActivity.listUpload[mainActivity.upload.position].end = Date()

        val prefs = Prefs(mainActivity)
        val url: String = prefs.settingsUrl

        if(url.isNotEmpty()){
            val dialog = Utils.modalAlert(mainActivity, "Guardando")
            dialog.show()
            Router.get(
                context = context!!,
                url = url,
                params = "action=end-upload&id=${mainActivity.upload.id}&pallets=${mainActivity.upload.pallets}",
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
        (mainActivity.currentAdapter as UploadAdapter).update()
        mainActivity.cleanFragment()
    }

    private fun saveEndError(response: String){
        Utils.alert(context!!, "Error: $response")
        mainActivity.listUpload[mainActivity.upload.position].end = null
    }


}