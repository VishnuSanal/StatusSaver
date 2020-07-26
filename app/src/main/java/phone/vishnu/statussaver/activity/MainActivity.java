package phone.vishnu.statussaver.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.statussaver.R;
import phone.vishnu.statussaver.adapter.RecyclerViewAdapter;
import phone.vishnu.statussaver.fragment.DetailsFragment;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);

        askForPermission();
        setUpRecyclerView();
    }

    private void askForPermission() {
        int PERMISSION_REQ_CODE = 222;

        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Please Accept Required Permission", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
            }
        }

    }

    private void setUpRecyclerView() {
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(this, FetchImages());

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.onItemClicked() {
            @Override
            public void onItemClicked(Bitmap bitmap, String path) {
                getSupportFragmentManager().beginTransaction().add(R.id.container, DetailsFragment.newInstance(path, bitmap)).addToBackStack(null).commit();
            }
        });

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
            } else if (file.getName().toLowerCase().endsWith(".mp4")) {
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
            Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.refresh) {
            setUpRecyclerView();
        }
        return super.onOptionsItemSelected(item);
    }
}