package phone.vishnu.statussaver.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.statussaver.R;
import phone.vishnu.statussaver.adapter.RecyclerViewAdapter;
import phone.vishnu.statussaver.fragment.AboutFragment;
import phone.vishnu.statussaver.fragment.HistoryFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, 1))
            isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, 1);

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));

        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        ArrayList<String> uriArrayList = fetchImages();
        recyclerViewAdapter.submitList(uriArrayList);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final String path) {

                if (path.endsWith(".jpg")) {

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    final View inflate = MainActivity.this.getLayoutInflater().inflate(R.layout.image_alert_dialog_layout, null);

                    alertDialogBuilder.setView(inflate);

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.setCancelable(true);

                    Glide.with(MainActivity.this)
                            .load(path)
                            .centerCrop()
                            .into((ImageView) inflate.findViewById(R.id.imageDialogImageVIew));

                    inflate.findViewById(R.id.imageDialogCancelButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    inflate.findViewById(R.id.imageDialogAcceptButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2))
                                isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2);

                            new SaveAsyncTask().execute(path);
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.setCancelable(true);
                    alertDialog.show();


                } else if (path.endsWith(".mp4")) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    final View inflate = MainActivity.this.getLayoutInflater().inflate(R.layout.video_alert_dialog_layout, null);

                    alertDialogBuilder.setView(inflate);

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.setCancelable(true);

                    VideoView videoView = inflate.findViewById(R.id.videoDialogVideoView);
                    videoView.setVideoURI(Uri.parse(path));
                    videoView.requestFocus();
                    videoView.start();

                    inflate.findViewById(R.id.videoDialogCancelButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    inflate.findViewById(R.id.videoDialogAcceptButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2))
                                isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2);

                            new SaveAsyncTask().execute(path);
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.setCancelable(true);
                    alertDialog.show();

                }
            }
        });
    }

    private ArrayList<String> fetchImages() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + ".Statuses" + File.separator;
        File[] files = new File(path).listFiles();

        ArrayList<String> fileNameList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".nomedia")) continue;
                fileNameList.add(file.getAbsolutePath());
            }
        }
        return fileNameList;
    }

    private String getFilePath() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StatusSaver");

        if (!root.exists()) //noinspection ResultOfMethodCallIgnored
            root.mkdirs();

        SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        int lastInt = (prefs.getInt("lastInt", 0)) + 1;

        String file = root.toString() + File.separator + "StatusSaver" + lastInt;

        prefs.edit().putInt("lastInt", lastInt).apply();
        return file;
    }

    private boolean isPermissionGranted(String PERMISSION, int PERMISSION_REQ_CODE) {
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSION)) {
                    showPermissionDeniedDialog(PERMISSION, PERMISSION_REQ_CODE);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{PERMISSION}, PERMISSION_REQ_CODE);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void showPermissionDeniedDialog(final String PERMISSION, final int PERMISSION_REQ_CODE) {

        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Permission");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{PERMISSION}, PERMISSION_REQ_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
            }
        });
        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about: {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                    getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, AboutFragment.newInstance()).addToBackStack(null).commit();
                break;
            }
            case R.id.menu_history: {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                    getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, HistoryFragment.newInstance()).addToBackStack(null).commit();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveAsyncTask extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(MainActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            String filePath = strings[0].endsWith(".jpg") ? (getFilePath() + ".jpg") : (getFilePath() + ".mp4");

            try (InputStream in = new FileInputStream(strings[0])) {

                try (OutputStream out = new FileOutputStream(filePath)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                } finally {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            MediaScannerConnection.scanFile(MainActivity.this, new String[]{filePath}, null, null);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isDone) {
            super.onPostExecute(isDone);

            if (isDone) {
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            p.dismiss();
        }
    }


}