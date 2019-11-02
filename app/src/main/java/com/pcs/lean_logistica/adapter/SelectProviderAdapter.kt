package com.pcs.lean_logistica.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcs.lean_logistica.R
import com.pcs.lean_logistica.fragment.SelectProviderFragment
import com.pcs.lean_logistica.model.Provider
import com.pcs.lean_logistica.tools.Utils

class SelectProviderAdapter: RecyclerView.Adapter<SelectProviderAdapter.ViewHolder>(), Filterable {

    private lateinit var selectProviderFragment: SelectProviderFragment

    private lateinit var originalList: List<Provider>
    private lateinit var showList: MutableList<Provider>

    fun selectProviderAdapter(selectProviderFragment: SelectProviderFragment, list: MutableList<Provider>){
        this.selectProviderFragment = selectProviderFragment
        this.originalList = list
        this.showList = ArrayList(list)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Provider = this.showList[position]
        holder.bind(item, selectProviderFragment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_provider_select, parent, false))
    }

    override fun getItemCount(): Int {
        return showList.size
    }

    private var onNothingFound: (() -> Unit)? = null

    fun search(s: String?, onNothingFound: (() -> Unit)?) {
        this.onNothingFound = onNothingFound
        filter.filter(s)
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
        private val textCode: TextView =  view.findViewById(R.id.item_select_provider_provider)
        private val textName: TextView =  view.findViewById(R.id.item_select_provider_name)
        private val textDate: TextView =  view.findViewById(R.id.item_select_provider_date)

        fun bind(provider: Provider, selectProviderFragment: SelectProviderFragment){
            textCode.text = "${provider.code}"
            textName.text = provider.name
            textDate.text = Utils.dateToString(provider.dateDelivery)

            itemView.setOnClickListener {
                selectProviderFragment.selectItem(provider)
            }

            itemView.setOnLongClickListener {
                selectProviderFragment.selectLongItem(provider)
                true
            }
        }

    }

}