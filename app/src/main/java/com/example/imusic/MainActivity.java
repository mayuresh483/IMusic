package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.nofilestext);

        // Dexter is use to show permission popup to the users
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> songs = fetchAllSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[songs.size()];

                        for (int i=0 ; i<songs.size();i++){
                            items[i] = songs.get(i).getName().replace(".mp3","");
                        }

                        if(songs.size() > 0){
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(MainActivity.this, PlaySongsActivity.class);
                                    String currentSong = listView.getItemAtPosition(position).toString();
                                    intent.putExtra("songs",songs);
                                    intent.putExtra("currentSong",currentSong);
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }else{
                            textView.setText("No songs found in local storage");
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }

    public ArrayList<File> fetchAllSongs(File files){
           ArrayList<File> fileList = new ArrayList<>();
           File[] songs = files.listFiles();
           if(songs!=null){
               for(File myFiles:songs){
                   if(!myFiles.isHidden() && myFiles.isDirectory()){
                        fileList.addAll(fetchAllSongs(myFiles));
                   }else{
                        if(myFiles.getName().endsWith("mp3") && !myFiles.getName().startsWith(".")){
                            fileList.add(myFiles);
                        }
                   }
               }
           }
           return fileList;
    }
}