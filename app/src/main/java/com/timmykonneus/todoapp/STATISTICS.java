package com.timmykonneus.todoapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class STATISTICS extends Fragment {

    public STATISTICS() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.statistics);
        return inflater.inflate(R.layout.stats, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        TaskStorageHelper.getInstance().getTasks(new TaskStorageHelper.Callback() {
            @Override
            public void onData(List<Task> tasks) {

                TextView currentTotal = (TextView) getView().findViewById(R.id.current_total);
                TextView currentActive = (TextView) getView().findViewById(R.id.current_active);
                TextView currentCompleted = (TextView) getView().findViewById(R.id.current_completed);
                TextView currentArchived = (TextView) getView().findViewById(R.id.current_deleted);

                TextView averageCreated = (TextView) getView().findViewById(R.id.average_created);
                TextView averageCompleted = (TextView) getView().findViewById(R.id.average_completed);


                TextView wordsDescription = (TextView) getView().findViewById(R.id.words_description);

                int activeCount = 0;
                int completedCount = 0;
                int allCompletedCount = 0;
                int archivedCount = 0;
                int descriptionCount = 0;

                if (tasks.size() > 0) {
                    int days = getDaysBetweenDates(tasks.get(0).getCreatedDate());

                    for (Task currentTask : tasks) {
                        if (!currentTask.isCompleted() && !currentTask.isArchived()) {
                            activeCount++;
                        }
                        if (currentTask.isCompleted() && !currentTask.isArchived()) {
                            completedCount++;
                        }
                        if (currentTask.isCompleted()) {
                            allCompletedCount++;
                        }
                        if (currentTask.isArchived()) {
                            archivedCount++;
                        }

                        descriptionCount += countWords(currentTask.getDescription());
                    }
                    currentTotal.setText(String.valueOf(tasks.size()));
                    currentActive.setText(String.valueOf(activeCount));
                    currentCompleted.setText(String.valueOf(completedCount));
                    currentArchived.setText(String.valueOf(archivedCount));

                    averageCreated.setText(String.valueOf(tasks.size() / days));
                    averageCompleted.setText(String.valueOf(allCompletedCount / days));


                    wordsDescription.setText(String.valueOf(descriptionCount / tasks.size()));
                }
            }
        });
    }
            public int countWords(String word) {
        if (word == null) {
            return 0;
        }
        String input = word.trim();
        int count = input.isEmpty() ? 0 : input.split("\\s+").length;
        return count;
    }

    public static int getDaysBetweenDates(Date startdate) {

        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(new Date()))
        {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return dates.size();
    }
}