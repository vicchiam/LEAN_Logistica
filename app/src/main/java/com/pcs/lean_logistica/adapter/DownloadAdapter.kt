package com.pcs.lean_logistica.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.fragment.DownloadFragment
import com.pcs.lean_logistica.fragment.SelectProviderFragment
import com.pcs.lean_logistica.model.Download
import com.pcs.lean_logistica.model.Provider
import com.pcs.lean_logistica.tools.Utils

class DownloadAdapter: RecyclerView.Adapter<DownloadAdapter.ViewHolder>(), Filterable {

    private lateinit var downloadFragment: DownloadFragment

    private lateinit var originalList: List<Download>
    private lateinit var showList: MutableList<Download>

    private var lastSearch: String? = ""

    fun DownloadAdapter(downloadFragment: DownloadFragment, list: MutableList<Download>){
        this.downloadFragment = downloadFragment
        this.originalList = list
        this.showList = ArrayList(list)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Download = this.showList[position]
        holder.bind(item, downloadFragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_download, parent, false))
    }

    override fun getItemCount(): Int {
        return showList.size
    }

    private var onNothingFound: (() -> Unit)? = null

    fun search(s: String?, onNothingFound: (() -> Unit)?) {
        lastSearch = s
        this.onNothingFound = onNothingFound
        filter.filter(s)
    }

    fun update(){
        search(lastSearch){}
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                showList.clear()
                if (constraint.isNullOrBlank()) {
                    showList.addAll(originalList)
                } else {
                    val searchResults = originalList.filter {
                        it.getSearchCriteria().contains(constraint)
                    }
                    showList.addAll(searchResults)
                }
                return filterResults.also {
                    it.values = showList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (showList.isNullOrEmpty())
                    onNothingFound?.invoke()
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val relativeLayout: RelativeLayout = view.findViewById(R.id.item_download)

        private val textProvider: TextView =  view.findViewById(R.id.item_download_provider)
        private val textStart: TextView =  view.findViewById(R.id.item_download_start)
        private val textEnd: TextView =  view.findViewById(R.id.item_download_end)
        private val textOperators: TextView =  view.findViewById(R.id.item_download_operators)

        fun bind(download: Download, downloadFragment: DownloadFragment){
            textProvider.text = "${download.provider} - ${download.name}"
            textStart.text = Utils.dateToString(download.start!!,"dd/MM/yyyy HH:mm")
            if(download.end!=null)
                textEnd.text = Utils.dateToString(download.end!!, "dd/MM/yyyy HH:mm")
            if(download.pending)
                relativeLayout.background =
                    downloadFragment.resources.getDrawable(
                        android.R.color.holo_green_light,
                        downloadFragment.context!!.theme
                    )
            else
                relativeLayout.background =
                    downloadFragment.resources.getDrawable(
                        android.R.color.white,
                        downloadFragment.context!!.theme
                    )
            textOperators.text = "Operaios: ${download.operators}"

            itemView.setOnClickListener {

            }
        }

    }
}