package phone.vishnu.statussaver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import phone.vishnu.statussaver.R;
import phone.vishnu.statussaver.activity.MainActivity;

public class DetailsFragment extends Fragment {

    private static String path = "";
    private static Bitmap bitmap;
    private ImageView imageView;
    private Button saveButton, cancelButton;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(String path, Bitmap bitmap) {
        DetailsFragment.path = path;
        DetailsFragment.bitmap = bitmap;
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_details, container, false);
        imageView = inflate.findViewById(R.id.imageIV);
        saveButton = inflate.findViewById(R.id.saveButton);
        cancelButton = inflate.findViewById(R.id.cancelButton);

        imageView.setImageBitmap(bitmap);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Please Wait...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (path.endsWith(".jpg")) generateNoteOnSD(getActivity(), bitmap);
                            else copy(getActivity(), path);

                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity(), MainActivity.class));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return inflate;
    }

    private void generateNoteOnSD(Context context, Bitmap image) {
        String filePath = getFilePath() + ".jpg";

        try {
            FileOutputStream fOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            fOutputStream.flush();
            fOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);
    }

    void copy(Context context, String src) throws IOException {

        String filePath = getFilePath() + ".mp4";

        try (InputStream in = new FileInputStream(src)) {

            try (OutputStream out = new FileOutputStream(filePath)) {
                // Transfer bytes from in to out
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
        }
        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);

    }

    private String getFilePath() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StatusSaver");

        if (!root.exists()) root.mkdirs();

        SharedPreferences prefs = getActivity().getSharedPreferences("phone.vishnu.statussaver", Context.MODE_PRIVATE);
        int lastInt = (prefs.getInt("number", 0)) + 1;

        String file = root.toString() + File.separator + "StatusSaver" + lastInt;

        prefs.edit().putInt("number", lastInt).apply();
        return file;
    }
}