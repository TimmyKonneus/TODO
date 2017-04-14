package com.timmykonneus.todoapp;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TASKLIST extends Fragment {

    public TASKLIST() {
    }


    private TasksAdapter tasksAdapter;
    private int selectedID = R.id.action_filter_all;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tasklist, container, false);
        getActivity().setTitle(R.string.tasks);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskDetailActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView tasksList = (RecyclerView) getView().findViewById(R.id.task_list);
        tasksAdapter = new TasksAdapter();
        tasksList.setAdapter(tasksAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTaskList(selectedID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filters, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void setTaskList(int id) {

        selectedID = id;
        if (selectedID == R.id.action_filter_all) {
            getActivity().setTitle(R.string.alltasks);
        }
        if (selectedID == R.id.action_filter_active) {
            getActivity().setTitle(R.string.Active_task);
        }
        if (selectedID == R.id.action_filter_completed) {
            getActivity().setTitle(R.string.completed_task);
        }
        if (selectedID == R.id.action_filter_archived) {
            getActivity().setTitle(R.string.archived_task);
        }
        TaskStorageHelper.getInstance().getTasks(new TaskStorageHelper.Callback() {
            @Override
            public void onData(List<Task> tasks) {
                ArrayList<Task> filtered = new ArrayList<>();
                for (Task task : tasks) {

                    if (selectedID == R.id.action_filter_all) {
                        getActivity().setTitle(R.string.alltasks);
                        if (!task.isArchived()){
                            filtered.add(task);
                        }

                    } else if (selectedID == R.id.action_filter_active) {
                        if (!task.isCompleted() && !task.isArchived()) {
                            filtered.add(task);

                        }

                    }else if (selectedID == R.id.action_filter_completed) {
                        if (task.isCompleted() && !task.isArchived()) {
                            filtered.add(task);
                        }

                    }else if (selectedID == R.id.action_filter_archived) {
                        if (task.isArchived()) {
                            filtered.add(task);
                        }
                    }

                }
                tasksAdapter.setTasks(filtered);
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setTaskList(id);
        return true;
    }





        private class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
        private List<Task> tasks = new ArrayList<>();

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }
        @Override
        public TASKLIST.TasksAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.task_item, parent, false);
            return new TASKLIST.TasksAdapter.TaskViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TASKLIST.TasksAdapter.TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.title.setText(task.getTitle());
            holder.description.setText(task.getDescription());
            holder.completed.setChecked(task.isCompleted());

        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView description;
            final CheckBox completed;

            TaskViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Task task = tasks.get(position);
                        Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                        intent.putExtra(TaskDetailActivity.KEY_TASK, task);
                        startActivity(intent);
                    }
                });

                title = (TextView) itemView.findViewById(R.id.title);
                description = (TextView) itemView.findViewById(R.id.description);
                completed = (CheckBox) itemView.findViewById(R.id.completed);

                completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        int position = getAdapterPosition();
                        final Task task = tasks.get(position);


                        if (task.isCompleted() != isChecked) {
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    task.setCompleted(isChecked);
                                    TaskStorageHelper.getInstance().saveTask(task);
                                    setTaskList(selectedID);
                                }
                            };
                            buttonView.post(runnable);
                        }

                        if (task.isCompleted()) {
                            if (selectedID == R.id.action_filter_active){
                                setTaskList(R.id.action_filter_active);

                            }
                            if (task.getCreatedDate() == null) {
                                task.setCreatedDate(new Date());
                            }
                        } else {
                            if (selectedID == R.id.action_filter_completed){
                                setTaskList(R.id.action_filter_completed);
                            }
                            task.setCompletedDate(null);
                        }
                        TaskStorageHelper.getInstance().saveTask(task);
                    }
                });
            }
        }
    }
}