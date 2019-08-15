package phone.vishnu.statussaver.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import vishnu.statussaver.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private int lastInt;
    private SharedPreferences prefs = null;
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
                        generateNoteOnSD(context, myBitmap);
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

        prefs = context.getSharedPreferences("phone.vishnu.statussaver", Context.MODE_PRIVATE);

        lastInt = (prefs.getInt("number", 0)) + 1;

        String file = root.toString() + File.separator + "StatusSaver" + lastInt + ".jpg";

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("number", lastInt);
        editor.apply();

        try {
            FileOutputStream fOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);

            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            fOutputStream.flush();
            fOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Save Failed", Toast.LENGTH_SHORT).show();
        }


    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv);
        }
    }


}
