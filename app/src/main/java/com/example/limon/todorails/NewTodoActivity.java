package com.example.limon.todorails;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewTodoActivity extends AppCompatActivity {

    Project selectedProject;
    Project othersProject;
    List<Project> projects;

    ListView projectsListView;
    EditText editText;

    AdapterView.OnItemClickListener onProjectClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            for (int i = 0; i < adapterView.getCount(); i++) {
                TextView currentTextView = adapterView.getChildAt(i).findViewById(R.id.text);
                ImageView currentImageView = adapterView.getChildAt(i).findViewById(R.id.image);
                if (i == position) {
                    currentImageView.setVisibility(View.VISIBLE);
                    for (int j = 0; j < projects.size(); j++) {
                        if (currentTextView.getText() == projects.get(i).title) {
                            selectedProject = projects.get(i);
                        }
                    }
                } else {
                    currentImageView.setVisibility(View.INVISIBLE);
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);

        // Setup Action Bar
        android.support.v7.widget.Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = this.findViewById(R.id.new_todo_text);



        // Get projects to List View
        Ion.with(this)
        .load(getString(R.string.projectsIndexRequest))
        .asJsonArray()
        .setCallback(new FutureCallback<JsonArray>() {

             @Override
             public void onCompleted(Exception e, JsonArray result) {
                 if (e != null) {
                     Log.e("Ion", e.toString());
                 } else {

                     projects = new ArrayList<>();
                     projectsListView = NewTodoActivity.this.findViewById(R.id.project_list);
                     ArrayAdapter<String> adapter = new ArrayAdapter<>(
                             NewTodoActivity.this, R.layout.projectlist_cell, R.id.text);

                     for (final JsonElement projectJsonElement : result) {

                         projects.add(new Gson().fromJson(projectJsonElement, Project.class));
                         Project lastProject = projects.get(projects.size() - 1);

                         adapter.add(lastProject.title);

                     }

                     for (int i = 0; i < projects.size(); i++) {
                         if (Objects.equals(projects.get(i).title, "Прочее")) {
                             othersProject = projects.get(i);
                         }
                     }

                     projectsListView.setAdapter(adapter);
                     projectsListView.setOnItemClickListener(onProjectClickListener);
                 }
             }
         });



        // Set default font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.default_font))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_todo, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                if (selectedProject == null) {
                    selectedProject = othersProject;
                }


                String todoText = editText.getText().toString();
                String todoProjectId = Integer.toString(selectedProject.id);
                String url = getString(R.string.projectsRequest) + todoProjectId + "/todo_from_app";


                JsonObject json = new JsonObject();

                json.addProperty("text", todoText);
                json.addProperty("project_id", todoProjectId);

                Ion.with(this)
                        .load(url)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                                NewTodoActivity.this.finish();

                            }
                        });
                return true;
            case android.R.id.home:

                onBackPressed();
                return true;
        }

        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
