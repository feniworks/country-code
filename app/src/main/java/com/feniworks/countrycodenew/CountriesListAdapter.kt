package com.feniworks.countrycodenew

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class CountriesListAdapter(private val ctx: Context, private val itemsArrayList: ArrayList<CountriesListItem>)
    : ArrayAdapter<CountriesListItem>(ctx, R.layout.countries_list_item, itemsArrayList) {
    private val originalItemsArrayList: ArrayList<CountriesListItem> = ArrayList(itemsArrayList)
    private var filter: Filter? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var vi = convertView
        val holder: ViewHolder
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (vi == null) {
            vi = inflater.inflate(R.layout.countries_list_item, parent, false)!!

            holder = ViewHolder()
            holder.title = vi.findViewById(R.id.list_item_title)
            holder.prefix = vi.findViewById(R.id.list_item_prefix)
            //holder.currency = (TextView)vi.findViewById(R.id.list_item_currency);
            holder.timezone = vi.findViewById(R.id.list_item_timezone)
            holder.icon = vi.findViewById(R.id.list_item_picture)

            vi.tag = holder
        } else {
            holder = vi.tag as ViewHolder
        }

        // 4. Set the text for textView
        holder.title.text = itemsArrayList[position].title
        holder.prefix.text = itemsArrayList[position].prefix
        //.currency.setText( itemsArrayList.get( position ).getCurrency() );
        holder.timezone.text = itemsArrayList[position].timezone
        holder.icon.setImageDrawable(itemsArrayList[position].icon)

        // 5. return rowView
        return vi
    }

    override fun getCount(): Int = itemsArrayList.size

    override fun getItem(position: Int): CountriesListItem = itemsArrayList[position]

    override fun getFilter(): Filter {
        if (filter == null)
            filter = TracksListFilter()
        return filter as Filter
    }

    internal inner class TracksListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            val search = constraint.toString().toLowerCase(Locale.ROOT)

            if (search.isEmpty()) {
                val list = ArrayList(originalItemsArrayList)
                results.values = list
                results.count = list.size
            } else {
                val list = ArrayList(originalItemsArrayList)
                val nlist = ArrayList<CountriesListItem>()
                val count = list.size

                for (i in 0 until count) {
                    val item = list[i]
                    val title = item.title.toLowerCase(Locale.ROOT)
                    val prefix = item.prefix.toLowerCase(Locale.ROOT).substring(1)

                    if (title.contains(search) || prefix.contains(search))
                        nlist.add(item) // Add found item to the new list
                }
                results.values = nlist
                results.count = nlist.size
            }

            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            clear()
            addAll(results.values as ArrayList<CountriesListItem>)
        }
    }

    private class ViewHolder {
        lateinit var title: TextView
        lateinit var prefix: TextView
        lateinit var timezone: TextView
        lateinit var icon: ImageView
    }
}