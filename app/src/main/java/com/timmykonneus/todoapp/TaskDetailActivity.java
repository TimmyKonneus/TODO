package com.timmykonneus.todoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDetailActivity extends AppCompatActivity implements
        View.OnClickListener {
    public static final String KEY_TASK = "task";
    private Task task;


    //TaskStorageHelper storageHelper = TaskStorageHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView createdLabel = (TextView) findViewById(R.id.label_createdDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        Button deleteButton = (Button) findViewById(R.id.delete_button);

        // Get the task that we might have received
        Intent intent = getIntent();
        task = intent.getParcelableExtra(KEY_TASK);

        if (task != null) {
            // Add click listener only for an existing Task
            deleteButton.setOnClickListener(this);
        } else {
            // Don't show if task is null (new Task!)
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // Only update the field if we have an existing task
        if (task != null) {
            EditText title = (EditText) findViewById(R.id.title);
            title.setText(task.getTitle());
            EditText description = (EditText) findViewById(R.id.description);
            description.setText(task.getDescription());
            CheckBox completed = (CheckBox) findViewById(R.id.completed);
            completed.setChecked(task.isCompleted());
            createdLabel.setText(dateFormat.format(task.getCreatedDate()));
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                saveOrCreateTask();
                break;
            case R.id.delete_button:
                deleteTask();
                break;
        }
    }
    private void deleteTask() {
        task.setArchived(true);
        TaskStorageHelper.getInstance().deleteTask(task);
        finish();
    }
    private void saveOrCreateTask() {
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);
        CheckBox completed = (CheckBox) findViewById(R.id.completed);

        if (task != null) {
            if (task.isArchived()){
                task.setArchived(false);
            }
            task.setTitle(String.valueOf(title.getText()));
            task.setDescription(String.valueOf(description.getText()));
            task.setCompleted(completed.isChecked());
            if (task.isCompleted() && task.getCompletedDate() == null){
                task.setCompletedDate(new Date());
            } else if (!task.isCompleted()){
                task.setCompletedDate(null);
            }
        } else {
            Task newTask = new Task();
            newTask.setTitle(String.valueOf(title.getText()));
            newTask.setDescription(String.valueOf(description.getText()));
            newTask.setCompleted(completed.isChecked());
            newTask.setCreatedDate(new Date());
            if (newTask.isCompleted()){
                newTask.setCompletedDate(new Date());
            }
            task = newTask;

        }
        TaskStorageHelper.getInstance().saveTask(task);

        finish();
    }
}