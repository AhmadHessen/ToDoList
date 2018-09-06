package com.example.coyg.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.coyg.todolist.database.AppDatabase;
import com.example.coyg.todolist.database.AppExecutors;
import com.example.coyg.todolist.database.TaskEntry;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity
{

        public static final String EXTRA_TASK_ID = "extrataskid";
        public static final String INSTANCE_TASK_ID = "instancetaskid";

        public static final int PRIORITY_HIGH = 1;
        public static final int PRIORITY_MEDIUM = 2;
        public static final int PRIORITY_LOW = 3;

        public static final int DEFAULT_TASK_ID = -1;

        public static final String TAG = AddTaskActivity.class.getSimpleName ();

        EditText editText;
        RadioGroup radioGroup;
        Button button;

        private int mTaskid = DEFAULT_TASK_ID;

        private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_task);
        
        initViews();

        mDb = AppDatabase.getsInstance (getApplicationContext ());

        if ((savedInstanceState != null && savedInstanceState.containsKey ((INSTANCE_TASK_ID))))
        {
            mTaskid = savedInstanceState.getInt (INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent ();
        if(intent != null && intent.hasExtra (EXTRA_TASK_ID))
        {
            mTaskid = intent.getIntExtra (EXTRA_TASK_ID, DEFAULT_TASK_ID);
            final LiveData<TaskEntry> taskEntry = mDb.taskDAO ().loadTaskById (mTaskid);
            taskEntry.observe (this, new Observer<TaskEntry> ()
            {
                @Override
                public void onChanged(@Nullable TaskEntry task)
                {
                    taskEntry.removeObserver (this);
                    populateUI (task);
                }
            });
        }
    }

    private void initViews()
    {
        editText = findViewById (R.id.edittext);
        radioGroup = findViewById (R.id.radiogroup);
        button = findViewById (R.id.addbutton);
        button.setOnClickListener (new View.OnClickListener ()
        {
            @Override
            public void onClick(View view)
            {
                onSaveButtonClicked();
            }
        });
    }

    private void populateUI(TaskEntry taskEntry)
    {
        if (taskEntry == null)
            return;

        editText.setText (taskEntry.getDescription ());
        setPriorityInViews (taskEntry.getPriority ());

    }

    public void onSaveButtonClicked()
    {
        String description = editText.getText ().toString ();
        int prority = getPriorityFromViews();
        Date date = new Date();

        final TaskEntry  taskEntry = new TaskEntry (description,prority,date);
        AppExecutors.getInstance ().getDiskIO ().execute (new Runnable ()
        {
            @Override
            public void run()
            {
                if (mTaskid == DEFAULT_TASK_ID)
                {
                    mDb.taskDAO ().insertTask (taskEntry);
                }
                else
                {
                    taskEntry.setId (mTaskid);
                    mDb.taskDAO ().updateTask (taskEntry);
                }
                finish ();
            }
        });
    }

    private int getPriorityFromViews()
    {
        int priority = 1;

        int checkedID = ((RadioGroup)  findViewById (R.id.radiogroup)).getCheckedRadioButtonId ();

        switch (checkedID)
        {
            case R.id.phigh: priority=PRIORITY_HIGH; break;
            case R.id.pmed: priority=PRIORITY_MEDIUM; break;
            case R.id.plow: priority=PRIORITY_LOW; break;
        }

        return priority;
    }

    public void setPriorityInViews(int priority)
    {
        switch (priority)
        {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radiogroup)).check(R.id.phigh);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radiogroup)).check(R.id.pmed);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radiogroup)).check(R.id.plow);
        }
    }
}