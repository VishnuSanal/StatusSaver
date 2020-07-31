package phone.vishnu.statussaver.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import phone.vishnu.statussaver.R;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private ImageView imageView;
    private ArrayList<String> arr;
    private onItemClicked listener;

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
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        Bitmap myBitmap = null;

        if (arr.get(position).toLowerCase().endsWith(".jpg")) {
            myBitmap = BitmapFactory.decodeFile(arr.get(position));
            imageView.setImageBitmap(myBitmap);
        } else if (arr.get(position).toLowerCase().endsWith(".mp4")) {
            myBitmap = ThumbnailUtils.createVideoThumbnail(arr.get(position), MediaStore.Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(myBitmap);
        }

        final Bitmap finalMyBitmap = myBitmap;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(finalMyBitmap, arr.get(position).toLowerCase());
            }
        });

    }

    public void setOnItemClickListener(onItemClicked listener) {
        this.listener = listener;
    }

    public interface onItemClicked {
        void onItemClicked(Bitmap bitmap, String path);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv);
        }
    }
}
