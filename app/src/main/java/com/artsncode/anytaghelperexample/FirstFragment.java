package com.artsncode.anytaghelperexample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.artsncode.anytaghelper.AnyTagHelper;
import com.artsncode.anytaghelper.AnyTagHelperProperties;

public class FirstFragment extends Fragment {

    private EditText editText;

    private TextView textView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        editText = view.findViewById(R.id.text_input);
        textView = view.findViewById(R.id.textview_first);

        super.onViewCreated(view, savedInstanceState);

        final AnyTagHelperProperties anyTagHelperProperties = new AnyTagHelperProperties(
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                '_'
        );



        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(editText.getText());

                AnyTagHelper anyTagHelper = AnyTagHelper.Creator.create(anyTagHelperProperties);
                anyTagHelper
                        .setOnTagClickListener(new AnyTagHelper.OnTagClickListener() {
                            @Override
                            public void onHashTagClicked(String hashTag) {
                                Toast.makeText(getContext(), "Clicked Hashtag " + hashTag, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAtTagClicked(String atTag) {
                                Toast.makeText(getContext(), "Clicked At Tag " + atTag, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLinkTagClicked(String linkTag) {
                                Toast.makeText(getContext(), "Clicked Link Tag " + linkTag, Toast.LENGTH_SHORT).show();
                            }
                        });

                anyTagHelper.handle(textView);

            }
        });

    }
}