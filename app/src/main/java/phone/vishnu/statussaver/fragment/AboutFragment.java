package phone.vishnu.statussaver.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import phone.vishnu.statussaver.BuildConfig;
import phone.vishnu.statussaver.R;

public class AboutFragment extends Fragment {

    private TextView sourceCodeTV, rateTV;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_about, container, false);
        sourceCodeTV = inflate.findViewById(R.id.aboutPageViewSourceCodeTextView);
        rateTV = inflate.findViewById(R.id.aboutPageRateTextView);
        ((TextView) inflate.findViewById(R.id.aboutSampleVersion)).setText(String.format("Version %s", BuildConfig.VERSION_NAME));
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sourceCodeTV.setOnClickListener(v -> {
            Uri uriUrl = Uri.parse("https://github.com/VishnuSanal/StatusSaver");
            startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
        });
        rateTV.setOnClickListener(v -> {

            Uri uriUrl = Uri.parse("market://details?id=" + requireContext().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
            }
        });
    }
}