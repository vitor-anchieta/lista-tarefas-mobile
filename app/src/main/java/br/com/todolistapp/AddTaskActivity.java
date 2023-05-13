package br.com.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {
    private EditText taskEditText;
    public static final String EXTRA_TASK = "br.com.todolistapp.EXTRA_TASK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskEditText = findViewById(R.id.task_edit_text);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskEditText.getText().toString();
                if (!task.isEmpty()) {
                    saveTask();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Por favor, digite uma tarefa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveTask() {
        String task = taskEditText.getText().toString().trim();
        if (task.isEmpty()) {
            Toast.makeText(this, "Por favor, digite uma tarefa", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK, task);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
