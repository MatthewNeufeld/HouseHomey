package com.example.househomey;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.househomey.Filters.DateFilterFragment;
import com.example.househomey.Filters.KeywordFilterFragment;
import com.example.househomey.Filters.MakeFilterFragment;
import com.example.househomey.Filters.TagFilterFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This fragment represents the home screen containing the primary list of the user's inventory
 * @author Owen Cooke, Jared Drueco, Lukas Bonkowski
 */
public class HomeFragment extends Fragment implements DeleteItemsFragment.OnFragmentInteractionListener {
    private CollectionReference itemRef;
    private ListView itemListView;
    private PopupMenu filterView;
    private ArrayList<Item> itemList;
    private ArrayAdapter<Item> itemAdapter;

    /**
     * This constructs a new HomeFragment with the appropriate list of items
     * @param itemRef A reference to the firestore collection containing the items to display
     */
    public HomeFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
        itemList = new ArrayList<>();
    }

    /**
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return the home fragment view containing the inventory list
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        itemListView = rootView.findViewById(R.id.item_list);
        itemAdapter = new ItemAdapter(getContext(), itemList);
        itemListView.setAdapter(itemAdapter);

        itemRef.addSnapshotListener(this::setupItemListener);

        View filterButton = rootView.findViewById(R.id.filter_dropdown_button);
        filterButton.setOnClickListener(this::showFilterMenu);

        ItemAdapter itemView = (ItemAdapter) itemAdapter;
        Button selectButton = rootView.findViewById(R.id.select_items_button);
        Button cancelButton = rootView.findViewById(R.id.cancel_select_button);
        View baseStateTools = rootView.findViewById(R.id.base_state_tools);
        View selectStateButtons = rootView.findViewById(R.id.select_state_tools);

        selectButton.setOnClickListener(v -> {
            itemView.setSelectState(true);
            selectButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.VISIBLE);
            baseStateTools.setVisibility(View.GONE);
            selectStateButtons.setVisibility(View.VISIBLE);


        });

        cancelButton.setOnClickListener(v -> {
            itemView.setSelectState(false);
            selectButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.GONE);
            baseStateTools.setVisibility(View.VISIBLE);
            selectStateButtons.setVisibility(View.GONE);
            // Unselect all items
            unselectAllItems();
        });

            final Button deleteButton = rootView.findViewById(R.id.action_delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean itemsToDelete = false;
                    int i = 0;
                    while (!itemsToDelete && i < itemList.size()) {
                        if (itemList.get(i).isSelected()) {
                            itemsToDelete = true;
                        }
                        i += 1;
                    }

                    if (itemsToDelete) {
                        // TODO: Ideally there should be a dialog fragment here asking for confirmation, and the below code
                        // TODO: should be the implementation of an interface in the dialog
                        // TODO: but since I can't implement interfaces in a fragment class, I wrote the entire code here
                        Integer numItemsSelected = 0;
                        for (int j=0;j<itemList.size();j++) {
                            if (itemList.get(j).isSelected()) {
                                numItemsSelected+=1;
                                itemRef
                                        .document(itemList.get(j).getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Firestore", "DocumentSnapshots successfully Deleted!");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // TODO: Handle the failure to delete the document
                                            Log.d("Firestore", "Failed to create new item");
                                        });
                            }
                        }
                        Toast.makeText(requireActivity().getApplicationContext(), "Deleted "+ numItemsSelected +" item(s).",
                                Toast.LENGTH_SHORT).show();
                        unselectAllItems();
                    }
                    else {
                        Toast.makeText(requireActivity().getApplicationContext(), "Please select one or more cities to delete.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        return rootView;

    }

    /**
     * This method updates the itemAdapter with changes in the firestore database and creates new
     * item objects
     * @param querySnapshots The updated information on the inventory from the database
     * @param error Non-null if an error occurred in Firestore
     */
    private void setupItemListener(QuerySnapshot querySnapshots, FirebaseFirestoreException error) {
        if (error != null) {
            Log.e("Firestore", error.toString());
            return;
        }
        if (querySnapshots != null) {
            itemList.clear();
            for (QueryDocumentSnapshot doc: querySnapshots) {
                Map<String, Object> data = new HashMap<>(doc.getData());
                itemList.add(new Item(doc.getId(), data));
            }
//                    Every time the data list is updated, all the checkboxes are unselected
            for (int i=0;i<itemList.size();i++) {
                View itemView = itemListView.getChildAt(i);
                if (itemView != null) {
                    CheckBox itemCheckBox = itemView.findViewById(R.id.item_checkBox);
                    itemCheckBox.setChecked(false);
                }
            }
            itemAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Displays the filter menu with options to select the appropriate filter
     * @param view The view to set the filter menu on
     */
    private void showFilterMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.filter, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_by_dates) {
                View dateFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_dates, null);
                DateFilterFragment dateFilterFragment = new DateFilterFragment("Modify Date Filter", dateFilterView);
                dateFilterFragment.show(requireActivity().getSupportFragmentManager(), "dates_filter_dialog");
            } else if (itemId == R.id.filter_by_make) {
                View makeFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_make, null);
                MakeFilterFragment makeFilterFragment = new MakeFilterFragment("Modify Make Filter", makeFilterView);
                makeFilterFragment.show(requireActivity().getSupportFragmentManager(), "make_filter_dialog");
            } else if (itemId == R.id.filter_by_keywords) {
                View keywordFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_keywords, null);
                KeywordFilterFragment keywordFilterFragment = new KeywordFilterFragment("Modify Keyword Filter", keywordFilterView);
                keywordFilterFragment.show(requireActivity().getSupportFragmentManager(), "keywords_filter_dialog");
            } else if (itemId == R.id.filter_by_tags) {
                View tagFilterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_filter_by_tags, null);
                TagFilterFragment tagFilterFragment = new TagFilterFragment("Modify Tag Filter", tagFilterView);
                tagFilterFragment.show(requireActivity().getSupportFragmentManager(), "tags_filter_dialog");
            } else {
                return false;
            }
            return true;
        });

        popupMenu.show();

    }


    @Override
    public void onOKPressed(){
        for (int i=0;i<itemList.size();i++) {
            if (itemList.get(i).isSelected()) {
                itemRef
                        .document(itemList.get(i).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "DocumentSnapshots successfully Deleted!");
                            }
                        })
                        .addOnFailureListener(e -> {
                    // TODO: Handle the failure to delete the document
                    Log.d("Firestore", "Failed to create new item");
                });
            }
        }
    }

    @Override
    public String DialogTitle() {

        Integer numItemsToDelete=0;
        for (int i=0;i<itemList.size();i++) {
            if (itemList.get(i).isSelected()) {
                numItemsToDelete++;
            }
        }
        return "Confirm deletion of "+numItemsToDelete.toString()+" item(s)?";
    }
    private void unselectAllItems() {
        for (int i=0;i<itemList.size();i++) {
            itemList.get(i).setSelected(false);
        }
        itemAdapter.notifyDataSetChanged();
    }

}
