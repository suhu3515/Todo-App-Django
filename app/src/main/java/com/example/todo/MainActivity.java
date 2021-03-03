package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.AdapterView.*;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton floatingActionButton;
    ArrayList<Tasks> tasksList = null;
    ArrayList<String> todoList = null;
    JsonArray tasksJsonArray = null;
    ListAdapter adapter;
    JSONObject object,object2,object3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        floatingActionButton = findViewById(R.id.float_button);
        tasksList = new ArrayList<>();
        todoList = new ArrayList<>();
        getDatas();
        registerForContextMenu(listView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder addAlert = new AlertDialog.Builder(MainActivity.this);
                View addView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
                final EditText editTextAddTask = addView.findViewById(R.id.et_tasks_add);
                Button btnAdd = addView.findViewById(R.id.btn_add);
                Button btnCancel = addView.findViewById(R.id.btn_cancel);
                addAlert.setView(addView);
                final AlertDialog alertDialog = addAlert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editTextAddTask.getText().toString().isEmpty())
                        {
                            editTextAddTask.setError("Enter any task");
                            editTextAddTask.requestFocus();
                        }
                        else
                        {
                            Call<JSONObject> addDataCall = RetrofitClient.getInstance().getMyApi().addTask(editTextAddTask.getText().toString());

                            addDataCall.enqueue(new Callback<JSONObject>() {
                                @Override
                                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                                    object = response.body();
                                    if (object != null)
                                    {
                                        alertDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Added task", Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Some error occurred!",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JSONObject> call, Throwable t) {

                                    Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
                alertDialog.show();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        menu.setHeaderTitle("Select action");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        String item_id = tasksList.get(listPosition).getId();
        String item_name = tasksList.get(listPosition).getTask();

        if (item.getItemId() == R.id.edit)
        {
            final AlertDialog.Builder editAlert = new AlertDialog.Builder(MainActivity.this);
            View editView = getLayoutInflater().inflate(R.layout.custom_edit_dialog_layout,null);
            final EditText editTextEditTask = editView.findViewById(R.id.et_tasks_edit);
            editTextEditTask.setText(item_name);
            Button btnUpdate = editView.findViewById(R.id.btn_update);
            Button btnECancel = editView.findViewById(R.id.btn_cancel_e);
            editAlert.setView(editView);
            final AlertDialog alert = editAlert.create();
            alert.setCanceledOnTouchOutside(false);
            btnECancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    alert.dismiss();
                }
            });
            btnUpdate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (editTextEditTask.getText().toString().isEmpty())
                    {
                        editTextEditTask.setError("Please enter task");
                        editTextEditTask.requestFocus();
                    }
                    else
                    {
                        Call<JSONObject> editDataCall = RetrofitClient.getInstance().getMyApi().updateTask(item_id, editTextEditTask.getText().toString());

                        editDataCall.enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                                Log.e( "onResponse: ", response.body().toString() );
                                object2 = response.body();
                                if (object2 != null)
                                {
                                    alert.dismiss();
                                    Toast.makeText(MainActivity.this, "Edited task", Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Some error occurred!",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {

                                Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            alert.show();
        }
        else if (item.getItemId() == R.id.delete)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Call<JSONObject> deleteDataCall = RetrofitClient.getInstance().getMyApi().deleteTask(item_id);

                            deleteDataCall.enqueue(new Callback<JSONObject>() {
                                @Override
                                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                                    object3 = response.body();
                                    if (object3 != null)
                                    {
                                        Toast.makeText(MainActivity.this, "Deleted Task", Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Some error occurred!",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JSONObject> call, Throwable t) {

                                    Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    })
                    .setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else
        {
            return false;
        }
        return true;
    }

    private void getDatas()
    {
        Call<JsonArray> getDataCall = RetrofitClient.getInstance().getMyApi().getData();

        getDataCall.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                tasksJsonArray = response.body();
                if (tasksJsonArray.size() != 0)
                {
                    for (int i=0; i < tasksJsonArray.size(); i++)
                    {
                        JsonObject jsonObject = (JsonObject) tasksJsonArray.get(i);
                        tasksList.add(new Tasks(
                                jsonObject.get("id").toString(),
                                String.valueOf(jsonObject.get("taskname")).replaceAll("\"","")
                        ));
                        todoList.add(String.valueOf(jsonObject.get("taskname")));
                    }
                }
                adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,todoList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                Toast.makeText(MainActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }
}