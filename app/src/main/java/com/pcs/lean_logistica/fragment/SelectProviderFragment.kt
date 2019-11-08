package com.pcs.lean_logistica.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonSyntaxException
import com.pcs.lean_logistica.MainActivity
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.adapter.SelectProviderAdapter
import com.pcs.lean_logistica.model.Provider
import com.pcs.lean_logistica.tools.Prefs
import com.pcs.lean_logistica.tools.Router
import com.pcs.lean_logistica.tools.Utils
import java.util.*

private const val TAG: String = "SELECT_PROV_FRAGMENT"

class SelectProviderFragment: Fragment() {

    private lateinit var mainActivity: MainActivity

    private val adapter: SelectProviderAdapter = SelectProviderAdapter()

    private lateinit var searchView: SearchView
    private lateinit var recycler: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_provider, container, false)

        val imageButton: ImageButton = view.findViewById(R.id.btn_back_select_provider)
        imageButton.setOnClickListener {
            mainActivity.navigateToNewDownload()
        }

        searchView = view.findViewById(R.id.search_provider)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.search(query){}
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.search(newText){}
                return true
            }
        })

        recycler = view.findViewById(R.id.recycler_provider)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)

        if(mainActivity.cache.get("providers")==null) {
            getAvailablePurchaseOrders()
        }
        else {
            adapter.selectProviderAdapter(this, (mainActivity.cache.get("providers") as MutableList<Provider>))
            recycler.adapter = adapter
        }

        return view
    }

    private fun getAvailablePurchaseOrders(){
        val prefs = Prefs(mainActivity)
        val url: String = prefs.settingsUrl
        val center: Int = prefs.settingsCenter

        val today = Utils.dateToString(Date(),"yyyy-MM-dd")

        if(url.isNotEmpty()){
            val dialog: AlertDialog = Utils.modalAlert(mainActivity)
            dialog.show()
            Router.get(
                context = context!!,
                url = url,
                params = "action=get-providers&center=$center&date=$today",
                responseListener = { response ->
                    if(context!=null) {
                        try{
                            val list: List<Provider> = Utils.fromJson(response)
                            val mutableList = list.toMutableList()
                            mainActivity.cache.set("providers", mutableList)
                            adapter.selectProviderAdapter(this, mutableList)
                            recycler.adapter = adapter
                        }
                        catch (ex: JsonSyntaxException){
                            Utils.alert(context!!,"El formato de la respuesta no es correcto: $response")
                        }
                        catch (ex: Exception){
                            Utils.alert(context!!,ex.toString())
                        }
                        finally {
                            dialog.dismiss()
                        }
                    }
                    else
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

    fun selectItem(provider: Provider){
        Utils.closeKeyboard(context!!, mainActivity)
        mainActivity.download.provider = provider.code
        mainActivity.download.name = provider.name
        mainActivity.navigateToNewDownload()
    }

    fun selectLongItem(provider: Provider){
        Utils.closeKeyboard(context!!, mainActivity)
        try{
            val articles: List<String> = provider.articles.split(";")
            val descriptions: List<String> = provider.descriptions.split(";")
            val quantities: List<String> = provider.quantities.split(";")
            val unds: List<String> = provider.unds.split(";")
            var msj="";
            for(i in articles.indices){
                msj+="${articles[i]}  -  ${descriptions[i]}  -  ${quantities[i]} ${unds[i]} \n"
            }
            Utils.alert(context!!, msj, "Articulos Previstos")
        }
        catch (e: Exception){
            Log.e(TAG, e.message!!)
        }
    }

}