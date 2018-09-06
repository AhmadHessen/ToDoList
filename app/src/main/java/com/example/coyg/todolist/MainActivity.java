package com.example.coyg.todolist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.AppExecutors;
import com.example.coyg.todolist.database.TaskEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements Adapter.ItemClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    Adapter adapter;
    FloatingActionButton fab;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initViews();

        recyclerView.setLayoutManager(new LinearLayoutManager (this));

        // Initialize the adapter and attach it to the RecyclerView
        adapter = new Adapter( this, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        recyclerView.addItemDecoration(decoration);

        new ItemTouchHelper (new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                AppExecutors.getInstance ().getDiskIO ().execute (new Runnable ()
                {
                    @Override
                    public void run()
                    {
                        int position = viewHolder.getAdapterPosition ();
                        List<TaskEntry> taskEntries = adapter.getmTaskEntries ();
                        db.taskDAO ().delete (taskEntries.get (position));
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

        ////////////////////////////////
        //FloatingActionButton Section//
        ////////////////////////////////
            fab.setOnClickListener (new View.OnClickListener ()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                    startActivity(intent);
                }
            });

            db = AppDatabase.getsInstance ((getApplicationContext ()));
            setupViewModel ();
    }

    private void initViews()
    {
        fab= findViewById (R.id.fab);
        recyclerView = findViewById (R.id.recyclerView);
    }

    private void setupViewModel()
    {
        MainViewModel mainViewModel = ViewModelProviders.of (this).get(MainViewModel.class);
        mainViewModel.getTask ().observe (this, new Observer<List<TaskEntry>> ()
        {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries)
            {
                adapter.setTasks(taskEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId)
    {
        Intent intent = new Intent (MainActivity.this,AddTaskActivity.class);
        intent.putExtra (AddTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity (intent);
    }
}
