package phone.vishnu.statussaver.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import phone.vishnu.statussaver.R;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private ImageView imageView;
    private ArrayList<String> arr;

    public RecyclerViewAdapter(Context context, ArrayList<String> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
//TODO:Do Hover
        final Bitmap myBitmap = BitmapFactory.decodeFile(arr.get(position));
        imageView.setImageBitmap(myBitmap);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ImageView iv = new ImageView(context);
                iv.setImageBitmap(myBitmap);
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(iv);
                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                generateNoteOnSD(context, myBitmap);
                            }
                        }).start();

                    }
                });
                alert.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    private void generateNoteOnSD(Context context, Bitmap image) {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StatusSaver");

        if (!root.exists()) root.mkdirs();

        SharedPreferences prefs = context.getSharedPreferences("phone.vishnu.statussaver", Context.MODE_PRIVATE);
        int lastInt = (prefs.getInt("number", 0)) + 1;

        String file = root.toString() + File.separator + "StatusSaver" + lastInt + ".jpg";

        prefs.edit().putInt("number", lastInt).apply();

        try {
            FileOutputStream fOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            fOutputStream.flush();
            fOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context, new String[]{file}, null, null);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv);
        }
    }


}
