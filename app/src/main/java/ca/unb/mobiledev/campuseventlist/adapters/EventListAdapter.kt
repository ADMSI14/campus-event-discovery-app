package ca.unb.mobiledev.campuseventlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ca.unb.mobiledev.campuseventlist.R
import ca.unb.mobiledev.campuseventlist.models.Event

/**
 * Sealed class to represent different types of list items
 */
sealed class EventListItem {
    data class DateHeader(val date: String) : EventListItem()
    data class EventItem(val event: Event) : EventListItem()
}

/**
 * Custom adapter for displaying events grouped by date
 */
class EventListAdapter(private val items: List<EventListItem>) : BaseAdapter() {
    
    override fun getCount(): Int = items.size
    
    override fun getItem(position: Int): EventListItem = items[position]
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun getViewTypeCount(): Int = 2
    
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EventListItem.DateHeader -> 0
            is EventListItem.EventItem -> 1
        }
    }
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]
        
        return when (item) {
            is EventListItem.DateHeader -> {
                val view = convertView ?: LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_event_date_header, parent, false)
                val textView = view as TextView
                textView.text = item.date
                view.isClickable = false
                view.isFocusable = false
                view
            }
            is EventListItem.EventItem -> {
                val view = convertView ?: LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_event, parent, false)
                val textView = view as TextView
                textView.text = item.event.name
                view
            }
        }
    }
    
    override fun isEnabled(position: Int): Boolean {
        return items[position] is EventListItem.EventItem
    }
}

