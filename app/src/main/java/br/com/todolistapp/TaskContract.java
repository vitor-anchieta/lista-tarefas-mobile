package br.com.todolistapp;

import android.provider.BaseColumns;

public class TaskContract {
    public static final class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK_NAME = "task_name";
        public static final String COLUMN_TASK_COMPLETED = "completed";
    }
}
