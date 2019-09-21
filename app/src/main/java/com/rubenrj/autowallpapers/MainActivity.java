package com.rubenrj.autowallpapers;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    ListView wrList;
    Toolbar myToolbar;
    WallpaperRuleAdapter adapter;
    SaveManager svMng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        wrList = findViewById(R.id.wrList);
        wrList.setEmptyView(findViewById(R.id.emptyView));

        svMng = new SaveManager(this);

        adapter = new WallpaperRuleAdapter(this, R.id.wrList, svMng.getWallpaperRules());
        wrList.setAdapter(adapter);
        wrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("info", "Click en " + position);
                openWallpaperRules(String.valueOf(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_black){
            openWallpaperRules(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == WallpaperRulesActivity.ACTIVITY_CODE){
            adapter.notifyDataSetChanged();
        }
    }

    private void openWallpaperRules(String index){
        Intent intent = new Intent(this, WallpaperRulesActivity.class);
        if(index != null){
            intent.putExtra("id", index);
        }
        startActivityForResult(intent, WallpaperRulesActivity.ACTIVITY_CODE);
    }

}

