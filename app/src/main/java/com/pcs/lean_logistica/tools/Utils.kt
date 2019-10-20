package com.pcs.lean_logistica.tools

import android.content.Context
import com.pcs.lean_logistica.R
import kotlinx.android.synthetic.main.fragment_container.view.*

class Utils {

    companion object {

        fun isDoubleFragment(context: Context): Boolean{
            return context.resources.getIdentifier("fragment_container3", "id", context.packageName)!=0
            return false
        }

    }

}