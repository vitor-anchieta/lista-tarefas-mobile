package br.com.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView taskListView;
    private ArrayList<String> tasks;
    private ArrayAdapter<String> arrayAdapter;
    private final int ADD_TASK_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskListView = findViewById(R.id.task_list_view);
        tasks = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        taskListView.setAdapter(arrayAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener((View.OnClickListener) v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();
    }

    public void addTask(String task) {
        tasks.add(task);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            String task = data.getStringExtra(AddTaskActivity.EXTRA_TASK);
            tasks.add(task);
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
