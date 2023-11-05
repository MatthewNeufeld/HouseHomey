package com.example.househomey;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * This fragment is for the "View Item Page" - which currently displays the details and comment linked
 * to the item
 * @author Matthew Neufeld
 */
public class ViewItemFragment extends Fragment {

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view item fragment
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_item, container, false);

        // Initialize TextViews
        TextView make = rootView.findViewById(R.id.view_item_make);
        TextView model = rootView.findViewById(R.id.view_item_model);
        TextView serialNumber = rootView.findViewById(R.id.view_item_serial_number);
        TextView cost = rootView.findViewById(R.id.view_item_cost);
        TextView comment = rootView.findViewById(R.id.view_item_comment);

        // Set TextViews to detail arguments sent over from ItemAdapter
        Bundle details = getArguments();
        if (details != null) {
            make.setText(details.getString("make", ""));
            model.setText(details.getString("model", ""));
            serialNumber.setText(details.getString("serialNumber", ""));
            cost.setText(details.getString("cost", ""));
            comment.setText(details.getString("comment", ""));
        }

        // Initialize edit button
        Button editButton = rootView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(v -> {
            // TODO: Go to Edit Item fragment once it has been created
        });


        return rootView;
    }
}