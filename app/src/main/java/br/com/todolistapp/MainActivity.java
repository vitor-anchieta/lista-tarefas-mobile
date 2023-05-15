package br.com.todolistapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView taskListView;
    private ArrayList<Task> tasks;
    private TaskAdapter taskAdapter;
    private final int ADD_TASK_REQUEST_CODE = 1;
    private TaskDbHelper dbHelper;
    private Spinner spinner_idioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDbHelper(this);

        taskListView = findViewById(R.id.task_list_view);
        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, tasks);
        taskListView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
        });
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task task = tasks.get(position);
            toggleTaskCompletion(task);
        });

        loadTasks();

        spinner_idioma = findViewById(R.id.spinner_idioma);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.listar_linguas, android.R.layout.simple_spinner_dropdown_item);
        spinner_idioma.setAdapter(arrayAdapter);
        spinner_idioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    setAppLanguage("pt");
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else if (position == 2) {
                    setAppLanguage("en");
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    public void addTask(String taskName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, taskName);
        values.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED, 0);
        long newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
        if (newRowId != -1) {
            Task task = new Task((int) newRowId, taskName, false);
            tasks.add(task);
            taskAdapter.notifyDataSetChanged();
        }
        db.close();
    }

    public void toggleTaskCompletion(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int taskId = task.getId();
        boolean completed = !task.isCompleted();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED, completed ? 1 : 0);

        int rowsAffected = db.update(
                TaskContract.TaskEntry.TABLE_NAME,
                values,
                TaskContract.TaskEntry._ID + " = ?",
                new String[]{String.valueOf(taskId)}
        );

        if (rowsAffected > 0) {
            task.setCompleted(completed);
            taskAdapter.notifyDataSetChanged();
        }

        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            String taskName = data.getStringExtra(AddTaskActivity.EXTRA_TASK);
            addTask(taskName);
        }
    }

    private void loadTasks() {
        tasks.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COLUMN_TASK_NAME, TaskContract.TaskEntry.COLUMN_TASK_COMPLETED},
                null,
                null,
                null,
                null,
                null
        );

        int columnIndexId = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int columnIndexName = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
        int columnIndexCompleted = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED);

        while (cursor.moveToNext()) {
            int taskId = cursor.getInt(columnIndexId);
            String taskName = cursor.getString(columnIndexName);
            boolean completed = cursor.getInt(columnIndexCompleted) == 1;

            Task task = new Task(taskId, taskName, completed);
            tasks.add(task);
        }

        cursor.close();
        taskAdapter.notifyDataSetChanged();
        db.close();
    }
    public void setAppLanguage(String linguagem){
        Locale localidade = new Locale(linguagem);
        Locale.setDefault(localidade);
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = localidade;
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
    }
}


