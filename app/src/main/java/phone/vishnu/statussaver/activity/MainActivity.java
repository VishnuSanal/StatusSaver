package phone.vishnu.statussaver.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.statussaver.R;
import phone.vishnu.statussaver.adapter.RecyclerViewAdapter;
import phone.vishnu.statussaver.fragments.AboutFragment;


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
            AboutFragment fragment = AboutFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.constraintLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (item.getItemId() == R.id.refresh) {
            setUpRecyclerView();
        }
        return super.onOptionsItemSelected(item);
    }
}