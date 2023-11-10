package com.example.househomey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

/**
 * A Dialog popup fragment that handles confirmation of deletion of items
 * @author Sami Jagirdar
 */
public class DeleteItemsFragment extends DialogFragment {

    private final DeleteCallBack listener;
    private final ArrayList<Item> selectedItems;

    /**
     * Constructor for the Delete Items confirmation dialog fragment
     * @param deleteCallBack context in which the dialog fragment is attached
     * @param selectedItems items selected to be deleted
     */
    public DeleteItemsFragment(DeleteCallBack deleteCallBack, ArrayList<Item> selectedItems) {
        this.listener = deleteCallBack;
        this.selectedItems = selectedItems;
    }

    /**
     * Called to create and return the delete items confirmation dialog
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return The confirm deletion dialog to be displayed
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_delete_items, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle(listener.DialogTitle(selectedItems))
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK",(dialog,which)->{
                    listener.onOKPressed(selectedItems);
                }).create();
    }

    /**
     * A Callback interface for handling deletion of items in the applicable context
     * @see SelectFragment
     */
    public interface DeleteCallBack {
        void onOKPressed(ArrayList<Item> selectedItems);
        String DialogTitle(ArrayList<Item> selectedItems);
    }
}