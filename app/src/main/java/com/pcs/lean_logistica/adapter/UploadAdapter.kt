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
import com.pcs.lean_logistica.fragment.UploadFragment
import com.pcs.lean_logistica.model.Upload
import com.pcs.lean_logistica.tools.Utils

class UploadAdapter: RecyclerView.Adapter<UploadAdapter.ViewHolder>(), Filterable {

    private lateinit var uploadFragment: UploadFragment

    private lateinit var originalList: List<Upload>
    private lateinit var showList: MutableList<Upload>

    private var lastSearch: String? = ""

    fun uploadAdapter(uploadFragment: UploadFragment, list: MutableList<Upload>){
        this.uploadFragment = uploadFragment
        this.originalList = list
        this.showList = ArrayList(list)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Upload = this.showList[position]
        holder.bind(item, uploadFragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_upload, parent, false))
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
        private val relativeLayout: RelativeLayout = view.findViewById(R.id.item_upload)

        private val textDock: TextView =  view.findViewById(R.id.item_upload_dock)
        private val textOperators: TextView =  view.findViewById(R.id.item_upload_operators)
        private val textPallets: TextView =  view.findViewById(R.id.item_upload_pallets)
        private val textStart: TextView =  view.findViewById(R.id.item_upload_start)
        private val textEnd: TextView =  view.findViewById(R.id.item_upload_end)


        fun bind(upload: Upload, uploadFragment: UploadFragment){
            val dockText = "Muelle de carga ${upload.dock}"
            textDock.text = dockText

            val palletsText = "Palets: ${upload.pallets}"
            textPallets.text = palletsText
            textStart.text = Utils.dateToString(upload.start!!,"dd/MM/yyyy HH:mm")
            if(upload.end!=null)
                textEnd.text = Utils.dateToString(upload.end!!, "dd/MM/yyyy HH:mm")
            else
                textEnd.text = "No Finalizada"
            if(upload.pending)
                relativeLayout.background =
                    uploadFragment.resources.getDrawable(
                        android.R.color.holo_green_light,
                        uploadFragment.context!!.theme
                    )
            else
                relativeLayout.background =
                    uploadFragment.resources.getDrawable(
                        android.R.color.white,
                        uploadFragment.context!!.theme
                    )

            val operationsText = "Operarios: ${upload.operators}"
            textOperators.text = operationsText

            itemView.setOnClickListener {
                uploadFragment.editUpload(upload)
            }
        }

    }
}