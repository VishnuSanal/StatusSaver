package phone.vishnu.statussaver.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.statussaver.R;
import phone.vishnu.statussaver.SaveItem;
import phone.vishnu.statussaver.adapter.RecyclerViewAdapter;
import phone.vishnu.statussaver.fragment.AboutFragment;
import phone.vishnu.statussaver.fragment.HistoryFragment;

@SuppressWarnings("SameParameterValue")
public class MainActivity extends AppCompatActivity {

    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        setUpRecyclerView();
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                        showPermissionDeniedDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        final RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, GridLayoutManager.VERTICAL));

        recyclerViewAdapter = new RecyclerViewAdapter();
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


                    inflate.findViewById(R.id.imageDialogShareButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dexter.withContext(MainActivity.this)
                                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                            new SaveAsyncTask(true, "image/jpg").execute(path);
                                        }

                                        @Override
                                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                                            showPermissionDeniedDialog();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                                            permissionToken.continuePermissionRequest();
                                        }
                                    })
                                    .check();

                            alertDialog.dismiss();
                        }
                    });

                    inflate.findViewById(R.id.imageDialogAcceptButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Dexter.withContext(MainActivity.this)
                                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                            new SaveAsyncTask().execute(path);
                                        }

                                        @Override
                                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                                            showPermissionDeniedDialog();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                                            permissionToken.continuePermissionRequest();
                                        }
                                    })
                                    .check();

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

                    inflate.findViewById(R.id.videoDialogShareButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Dexter.withContext(MainActivity.this)
                                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                            new SaveAsyncTask(true, "video/mp4").execute(path);
                                        }

                                        @Override
                                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                                            showPermissionDeniedDialog();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                                            permissionToken.continuePermissionRequest();
                                        }
                                    })
                                    .check();
                            alertDialog.dismiss();

                        }
                    });

                    inflate.findViewById(R.id.videoDialogAcceptButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Dexter.withContext(MainActivity.this)
                                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                            new SaveAsyncTask().execute(path);
                                        }

                                        @Override
                                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                                            showPermissionDeniedDialog();
                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                                            permissionToken.continuePermissionRequest();
                                        }
                                    })
                                    .check();
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.setCancelable(true);
                    alertDialog.show();

                }
            }
        });
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", getPackageName(), null))
                );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_about) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, AboutFragment.newInstance()).addToBackStack(null).commit();
        } else if (itemId == R.id.menu_history) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, HistoryFragment.newInstance()).addToBackStack(null).commit();
        } else if (itemId == R.id.menu_refresh) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                {
                    ArrayList<String> uriArrayList = fetchImages();
                    recyclerViewAdapter.submitList(uriArrayList);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareItem(String type, String filePath) {
        Uri uri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", new File(filePath));
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(type);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private class SaveAsyncTask extends AsyncTask<String, Integer, SaveItem> {

        private boolean isShare = false;
        private String type;
        private ProgressDialog p;

        public SaveAsyncTask() {
        }

        public SaveAsyncTask(boolean isShare, String type) {
            this.isShare = isShare;
            this.type = type;
        }

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
        protected SaveItem doInBackground(String... strings) {

            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StatusSaver");

            if (!root.exists()) //noinspection ResultOfMethodCallIgnored
                root.mkdirs();

            String filePath = root.toString() + File.separator + Uri.fromFile(new File(strings[0])).getLastPathSegment();

            if (new File(filePath).exists()) {
                return new SaveItem(true, true, filePath);
            }

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
                return new SaveItem(false, false);
            }

            MediaScannerConnection.scanFile(MainActivity.this, new String[]{filePath}, null, null);

            return new SaveItem(false, true, filePath);
        }

        @Override
        protected void onPostExecute(SaveItem saveItem) {
            super.onPostExecute(saveItem);

            if (saveItem.isDone()) {

                if (saveItem.alreadyExists())
                    Toast.makeText(MainActivity.this, "Status Already Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                if (isShare && saveItem.getFilePath() != null && type != null)
                    shareItem(type, saveItem.getFilePath());

            } else
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();

            p.dismiss();
        }
    }
}