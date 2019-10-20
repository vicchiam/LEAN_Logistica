package com.pcs.lean_logistica.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pcs.lean_logistica.R

class SettingFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        val view: View =inflater.inflate(R.layout.fragment_settings,container,false)

        return view
    }

}