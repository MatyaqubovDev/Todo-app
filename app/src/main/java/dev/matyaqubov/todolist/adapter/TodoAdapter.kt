package dev.matyaqubov.todolist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import dev.matyaqubov.todolist.R
import dev.matyaqubov.todolist.model.Todo

class TodoAdapter(var context: Context, var todoList: List<Todo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tracker:SelectionTracker<Todo>?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = todoList[position]
        if (holder is TodoViewHolder) {
            holder.title.text = todo.title
            holder.desc.text = todo.describtion

            tracker?.let {
                holder.setBackground(todo,it.isSelected(todo))
            }

        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun getItem(position: Int): Todo = todoList[position]

    fun getPosition(id: Int) = todoList.indexOfFirst { it.id == id }


    inner class TodoViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        var item = view.findViewById<LinearLayout>(R.id.item)
        var title = view.findViewById<TextView>(R.id.tv_title)
        var desc = view.findViewById<TextView>(R.id.tv_desc)
        var level = view.findViewById<TextView>(R.id.tv_level)


        fun setBackground(todo: Todo, isActivated: Boolean = false) {
            title.text = todo.title
            view.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Todo> =
            object : ItemDetailsLookup.ItemDetails<Todo>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Todo? {
                    return todoList[position]
                }

            }
    }
}

class MyItemKeyProvider(private val adapter: TodoAdapter) : ItemKeyProvider<Todo>(SCOPE_CACHED) {
    override fun getKey(position: Int): Todo? {
        return adapter.getItem(position)
    }

    override fun getPosition(key: Todo): Int {
        return adapter.getPosition(key.id)
    }

}

class MyItemDetailsLookup(private val recyclerView: RecyclerView):ItemDetailsLookup<Todo>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<Todo>? {
        val view=recyclerView.findChildViewUnder(e.x,e.y)

        if(view!=null){
            return (recyclerView.getChildViewHolder(view)as TodoAdapter.TodoViewHolder).getItemDetails()
        }
        return null
    }

}