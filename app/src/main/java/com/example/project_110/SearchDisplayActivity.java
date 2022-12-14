package com.example.project_110;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SearchDisplayActivity extends AppCompatActivity {

    //Get selected exhibits with this

    SearchBar searchbar;
    TextView selectedDisplayCount;
    Button clearSelectedButton;

    public RecyclerView searchRecyclerView,selectedRecyclerView;

    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<Void> future;
    private SearchListViewModel searchListViewModel;
    
    private boolean keepThreadOpen;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_display);
        selectedDisplayCount = findViewById(R.id.selected_exhibit_count);
        clearSelectedButton = findViewById(R.id.clear_selected_button);
        searchListViewModel = new ViewModelProvider(this)
                .get(SearchListViewModel.class);
        // initializing new Map searchable by tags // & list of vertex info
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "zoo_graph_new.json");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this, "zoo_node_new.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this, "zoo_edge_new.json");
        VertexList vertexList = new VertexList(vInfo);
//        System.out.println(vertexList.search("bird").size());

        SearchView search = (SearchView) findViewById(R.id.search);
        searchbar = new SearchBar(search, vertexList);
        SearchDisplayAdapter searchAdapter = new SearchDisplayAdapter();
        SelectedDisplayAdapter selectedAdapter = new SelectedDisplayAdapter();
        selectedAdapter.setHasStableIds(true);
        searchAdapter.setHasStableIds(true);
        this.clearSelectedButton.setOnClickListener(view -> {
            //Log.d("clear pressed", "thing should happen");
           searchListViewModel.clearSelectedExhibits();

        });
        //EVIL LINE OF CODE BELOW
        //searchListViewModel.getSearchListItems().observe(this, searchAdapter::setSearchListItems);

        searchRecyclerView = findViewById(R.id.search_items);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedRecyclerView = findViewById(R.id.selected_items);

        selectedRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        searchAdapter.setOnSearchListItemClickedHandlder(searchListViewModel::selectExhibit);

        //Log.d("Exhibit Stats", getSelectedExhibitsList().get(1).name + " " + getSelectedExhibitsList().size());


        searchRecyclerView.setAdapter(searchAdapter);
        selectedRecyclerView.setAdapter(selectedAdapter);


        keepThreadOpen = true;
        
        this.future = backgroundThreadExecutor.submit(() -> {
            while (keepThreadOpen) { // TODO: change true to something that makes sense
                //System.out.println("still in old thread");
                runOnUiThread(() -> {
                    List<VertexInfoStorable> packedList = new ArrayList();
                    for (ZooData.VertexInfo vertex : searchbar.currentAnimalsFromQuery){
                        packedList.add(new VertexInfoStorable(vertex));
                    }
                   //this.selectedDisplayCount.setText(getSelectedExhibitsList().size());
                    searchAdapter.setSearchListItems(packedList);
                    selectedAdapter.setSelectedExhibits(getSelectedExhibitsList());
                    selectedDisplayCount.setText("Selected Exhibits: " + getSelectedExhibitsList().size());
                });
                Thread.sleep(100);
            }
            return null;
        });




        //Log.d("tag", searchbar.currentAnimalsFromQuery.get(0).name);



    }

        //Call this to get list of selected exhibits
    public List<VertexInfoStorable> getSelectedExhibitsList(){
        return searchListViewModel.getSelectedExhibits();
    }
    public void onPlanButtonClick(View view) {

        if (getSelectedExhibitsList().size() == 0) {
            return;
        }
        keepThreadOpen = false;
        Intent intent = new Intent(this, PlanActivity.class);
        intent.putParcelableArrayListExtra("selectedExhibitsList", (ArrayList<VertexInfoStorable>) getSelectedExhibitsList());
        startActivity(intent);
    }


}



 /*Map<String, ZooData.VertexInfo> indexedZooData = new HashMap();

        List<String> grizzList = new ArrayList();
        grizzList.add("grizzly");
        grizzList.add("bear");
        grizzList.add("mammal");

        indexedZooData.put("grizzly_bears",new ZooData.VertexInfo("grizzly_bears", ZooData.VertexInfo.Kind.EXHIBIT, "Grizzly Bears", grizzList));
        List<String> pengList = new ArrayList();
        pengList.add("penguin");
        pengList.add("antarctic");
        pengList.add("bird");
        pengList.add("snow");

        indexedZooData.put("penguin_place",new ZooData.VertexInfo("penguin_place", ZooData.VertexInfo.Kind.EXHIBIT, "Penguin Place", pengList));

        List<String> pandaList = new ArrayList();
        pandaList.add("panda");
        pandaList.add("bird");

        indexedZooData.put("panda_palace",new ZooData.VertexInfo("panda_palace", ZooData.VertexInfo.Kind.EXHIBIT, "Panda Palace", pandaList));

        //indexedZooData = ZooData.loadVertexInfoJSON(this, "sample_node_info.json");

        VertexList vertexList = new VertexList(indexedZooData);


        String tag = "bird";

        List<ZooData.VertexInfo> searchList =  vertexList.search(t

        Log.d("tag", searchList.get(0).name);*/