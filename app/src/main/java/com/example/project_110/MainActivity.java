package com.example.project_110;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initalizing new Map searchable by tags // & list of vertex info
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON("sample_node_info.json");
        VertexList vertexList = new VertexList(vInfo);

    }

}