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
import com.pcs.lean_logistica.model.Download
import com.pcs.lean_logistica.tools.Utils
import java.util.*

private const val TAG = "DOWNLOAD FORM FRAGMENT"

class DownloadFormFragment: Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_download_form, container, false)

        val download: Download = mainActivity.download

        makeEditTextProviders(view, download.provider, download.name)
        makeEditTextOperator(view, download.operators)
        makeSwitch(view, download.type)
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

    private fun makeEditTextProviders(view: View, provider: Int = 0, name: String =""){
        val editText: EditText = view.findViewById(R.id.edit_provider)
        if(provider==0)
            editText.setText(name)
        else
            editText.setText("$provider - $name")

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

    private fun makeEditTextOperator(view: View, num: Int){
        val editText: EditText = view.findViewById(R.id.edit_num_operators)
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

    private fun makeSwitch(view: View, type: String = "SI"){
        val textView: TextView = view.findViewById(R.id.text_download_type)
        textView.text = type

        val switch: Switch = view.findViewById(R.id.switch_download_type)
        switch.isChecked = (type=="SI")

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                textView.text = switch.textOn
            else
                textView.text = switch.textOff

            mainActivity.download.type= textView.text.toString()
        }
    }

    private fun makeButtons(view: View, start: Date?, end: Date?){
        val buttonStart : MaterialButton = view.findViewById(R.id.btn_download_start)
        val buttonEnd : MaterialButton = view.findViewById(R.id.btn_download_end)

        buttonStart.isEnabled = (start==null)
        buttonEnd.isEnabled = (start!=null)


        buttonStart.setOnClickListener {
            if(!mainActivity.download.isValid()){
                Utils.alert(context!!, "Debes rellenar todos los campos")
            }
            else{
                if(save()){
                    buttonStart.isEnabled = false
                    buttonEnd.isEnabled = true
                }
            }
        }

    }

    private fun save(): Boolean{

        return true
    }

}