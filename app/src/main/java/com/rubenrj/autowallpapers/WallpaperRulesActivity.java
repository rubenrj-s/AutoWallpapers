package com.rubenrj.autowallpapers;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WallpaperRulesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    public static final int ACTIVITY_CODE = 1002;

    WallpaperRule rulesModel;
    TextView tvSince;
    ToggleButton[] days;
    Toolbar toolbarWr;
    ImageView wallpaper;
    private Uri uriImage;
    private String lastImage = null;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_rules);
        //We need that id to change a existent wr in array
        id = getIntent().getStringExtra("id"); //TODO: If null is a new WallpaperRules
        toolbarWr = findViewById(R.id.toolbarWr);
        setSupportActionBar(toolbarWr);
        //Only for API +21
        toolbarWr.setNavigationIcon(R.drawable.ic_arrow_back_24);
        toolbarWr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvSince = findViewById(R.id.tvSince);
        tvSince.setOnClickListener(this);

        //TODO: Remember initialize with property info
        if (id == null) {
            rulesModel = new WallpaperRule();
        } else {
            rulesModel = new SaveManager().getWallpaperRules().get(Integer.parseInt(id));
        }

        tvSince.setText(rulesModel.since);

        days = new ToggleButton[]{
                findViewById(R.id.tbMonday),
                findViewById(R.id.tbTuesday),
                findViewById(R.id.tbWednesday),
                findViewById(R.id.tbThursday),
                findViewById(R.id.tbFriday),
                findViewById(R.id.tbSaturday),
                findViewById(R.id.tbSunday)
        };


        setDays(rulesModel.days);

        wallpaper = findViewById(R.id.ivWallpaper);
        wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission((Manifest.permission.READ_EXTERNAL_STORAGE))
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted request
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        selectWallpaper();
                    }
                } else {
                    //system os is less then marshmallow
                    selectWallpaper();
                }
            }
        });
        if (!rulesModel.imagePath.isEmpty()){
            uriImage = Uri.fromFile(new File(getFilesDir() + "/" + rulesModel.imagePath));
            wallpaper.setImageURI(uriImage);
        }
        //TODO: Check the method to show a permanent deny
    }

    @Override
    public void onClick(View v) {
        //Identify the view
        switch (v.getId()) {
            case R.id.tvSince:
                showTimeDialog(tvSince);
                break;
            case R.id.imgWallpaper:
                selectWallpaper();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wr, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_black) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    selectWallpaper();
                } else {
                    // permission denied
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            uriImage = data.getData();
            wallpaper.setImageURI(uriImage);
            lastImage = rulesModel.imagePath;
        }
    }

    private void showTimeDialog(TextView tv) {
        DialogFragment newFragment = new TimePickerFragment();
        String[] parts = tv.getText().toString().split(":");
        Bundle args = new Bundle();
        args.putInt("hour", Integer.parseInt(parts[0]));
        args.putInt("min", Integer.parseInt(parts[1]));
        args.putInt("tv", tv.getId());
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Equalize days of class with days of the view.
     *
     * @param days
     */
    private void setDays(boolean[] days) {
        for (int i = 0; i < 7; i++) {
            this.days[i].setChecked(days[i]);
        }
    }

    private boolean[] getDays() {
        boolean[] days = new boolean[7];
        for (int i = 0; i < 7; i++) {
            days[i] = this.days[i].isChecked();
        }
        return days;
    }

    private void save() {
        boolean error = false;
        //TODO: Debug that to check if it works
        if (uriImage != null) {
            rulesModel.since = tvSince.getText().toString();
            rulesModel.days = getDays();
            //Only if it is a new image we create a new file.
            if (lastImage != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uriImage, projection, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                //Creating a unique image name
                String fileName = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(Calendar.getInstance().getTime());
                String fileNewPath = fileName + filePath.substring(filePath.lastIndexOf("."));
                File dst = new File(getFilesDir(), fileNewPath);
                try (InputStream in = new FileInputStream(filePath)) {
                    try (OutputStream out = new FileOutputStream(dst)) {
                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                    rulesModel.imagePath = fileNewPath;
                    if (!lastImage.isEmpty()) {
                        if (new File(getFilesDir() + "/" + lastImage).delete()){
                            Log.i("wra", "Old wallpaper deleted.");
                        }
                    }
                } catch (FileNotFoundException e) {
                    error = true;
                    Toast.makeText(this,"Some error occurred saving changes.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    error = true;
                    Toast.makeText(this,"Some error occurred saving changes.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            if (!error) {
                if (!rulesModel.imagePath.isEmpty()) {
                    SaveManager sm = new SaveManager();
                    if (id != null) {
                        sm.setWallpaperRule(Integer.parseInt(id), rulesModel);
                        Log.i("wra", "Rule edited");
                    } else {
                        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
                        int currentCounter = sharedPref.getInt("idCounter", -1);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("idCounter", ++currentCounter);
                        editor.apply();
                        rulesModel.id = currentCounter;
                        sm.addWallpaperRule(rulesModel);
                        Log.i("wra", "Rule added");
                    }
                    Toast.makeText(this, "Rule saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    //TODO: Check conditions, that conditions shouldn't exist
                    Log.w("wra", "The image path is empty, check errors...");
                    Toast.makeText(this,"No image path", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this,"No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectWallpaper() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}