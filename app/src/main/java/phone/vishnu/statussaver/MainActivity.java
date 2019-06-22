package phone.vishnu.statussaver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;

import vishnu.statussaver.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter(this, FetchImages());
        mRecyclerView.setAdapter(mAdapter);
    }


    private ArrayList<String> FetchImages() {

        ArrayList<String> filenames = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + ".Statuses" + File.separator;

        File directory = new File(path);
        File[] files = directory.listFiles();

        for (File file : files) {

            if (file.getName().toLowerCase().endsWith(".jpg")) {

                String file_name = file.getPath();
                filenames.add(file_name);
            }
        }

        return filenames;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, Main3Activity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.refresh) {
            setUpRecyclerView();
        }
        return super.onOptionsItemSelected(item);
    }
}