package dev.matyaqubov.todolist.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.matyaqubov.todolist.R
import dev.matyaqubov.todolist.adapter.MyItemDetailsLookup
import dev.matyaqubov.todolist.adapter.MyItemKeyProvider
import dev.matyaqubov.todolist.adapter.TodoAdapter
import dev.matyaqubov.todolist.model.Todo

class MainActivity : AppCompatActivity(), ActionMode.Callback{
    private lateinit var todoList:MutableList<Todo>
    private lateinit var adapter:TodoAdapter
    private var actionMode: ActionMode?=null
    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private var tracker:SelectionTracker<Todo>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        todoList= mutableListOf()
        addButton=findViewById(R.id.btn_add)
        todoRecyclerView=findViewById(R.id.rv_main)
        setTodo()
        adapter= TodoAdapter(this,todoList)
        todoRecyclerView.layoutManager=GridLayoutManager(this,1)
        todoRecyclerView.adapter=adapter

        setupUI()
    }

    private fun setupUI() {
        tracker=SelectionTracker.Builder(
            "mySelection",
            todoRecyclerView,
            MyItemKeyProvider(adapter),
            MyItemDetailsLookup(todoRecyclerView),
            StorageStrategy.createParcelableStorage(Todo::class.java)
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker=tracker

        setupObserver()
    }

    private fun setupObserver() {
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Todo>(){
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    tracker?.let {
                        todoList=it.selection.toMutableList()
                        if (todoList.isEmpty()){
                            actionMode?.finish()
                        } else{
                            if (actionMode==null) actionMode=startSupportActionMode(this@MainActivity)
                            actionMode?.title="${todoList.size}"
                        }
                    }
                }
            }
        )
    }

    private fun setTodo() {
        todoList.add(Todo(1,"Learn","Something"))
        todoList.add(Todo(2,"pdp","Something"))
        todoList.add(Todo(3,"some","Something"))
        todoList.add(Todo(4,"Clean","Something"))
        todoList.add(Todo(5,"delete","Something"))

    }

    override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        p0?.let {
            val inflater:MenuInflater=it.menuInflater
            inflater.inflate(R.menu.menu,p1)
            return true
        }
        return false
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
       when(p1?.itemId){
           R.id.title ->{
               Toast.makeText(this, todoList.toString(), Toast.LENGTH_SHORT).show()
           }
       }
        return true
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        adapter.tracker?.clearSelection()
        adapter.notifyDataSetChanged()
        actionMode=null
    }


}