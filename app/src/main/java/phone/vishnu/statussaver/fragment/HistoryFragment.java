package phone.vishnu.statussaver.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.statussaver.R;
import phone.vishnu.statussaver.adapter.RecyclerViewAdapter;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.historyRecyclerView);
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {

        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), GridLayoutManager.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), GridLayoutManager.VERTICAL));

        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        ArrayList<String> uriArrayList = fetchImages();
        recyclerViewAdapter.submitList(uriArrayList);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final String path) {

                if (path.endsWith(".jpg")) {

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    final View inflate = getActivity().getLayoutInflater().inflate(R.layout.image_alert_dialog_layout, null);

                    alertDialogBuilder.setView(inflate);

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.setCancelable(true);

                    Glide.with(getActivity())
                            .load(path)
                            .centerCrop()
                            .into((ImageView) inflate.findViewById(R.id.imageDialogImageVIew));

                    ((Button) inflate.findViewById(R.id.imageDialogAcceptButton)).setText("Delete");
                    inflate.findViewById(R.id.imageDialogShareButton).setVisibility(View.GONE);

                    inflate.findViewById(R.id.imageDialogCancelButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    inflate.findViewById(R.id.imageDialogAcceptButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            deleteFile(path);
//                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    });

                    alertDialog.setCancelable(true);
                    alertDialog.show();


                } else if (path.endsWith(".mp4")) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    final View inflate = getActivity().getLayoutInflater().inflate(R.layout.video_alert_dialog_layout, null);

                    alertDialogBuilder.setView(inflate);

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.setCancelable(true);

                    ((Button) inflate.findViewById(R.id.videoDialogAcceptButton)).setText("Delete");
                    inflate.findViewById(R.id.videoDialogShareButton).setVisibility(View.GONE);

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
                            alertDialog.dismiss();
                            deleteFile(path);
//                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                    alertDialog.setCancelable(true);
                    alertDialog.show();

                }
            }
        });
    }

    private void deleteFile(String path) {
        try {
            new File(path).delete();
            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
            requireContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> fetchImages() {
        String path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StatusSaver").getAbsolutePath();

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
}