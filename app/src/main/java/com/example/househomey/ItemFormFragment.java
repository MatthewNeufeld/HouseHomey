package com.example.househomey;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

/**
 * This abstract class serves as a base for creating and managing both Add and Edit Item forms.
 * It provides common functionality for handling user input and date selection.
 *
 * @author Owen Cooke
 */
public abstract class ItemFormFragment extends Fragment {
    protected Date dateAcquired;
    protected TextInputEditText dateTextView;
    protected final CollectionReference itemRef;

    /**
     * Constructs a new ItemFormFragment with a reference to a Firestore collection of items.
     *
     * @param itemRef A reference to a Firestore collection of items
     */
    public ItemFormFragment(CollectionReference itemRef) {
        this.itemRef = itemRef;
    }

    /**
     * Creates the basic view for a validated Item form.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return A View representing the Item form with validation listeners.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        initDatePicker(rootView);
        initTextValidators(rootView);
        return rootView;
    }

    /**
     * Checks whether the text input for a required form field is empty.
     * Additionally, if empty, it sets an inline error message on the input field.
     * If not empty, it removes the inline error.
     *
     * @param id Id of the required field's TextInputLayout
     * @return a boolean indicating if the field is empty
     */
    protected boolean isRequiredFieldEmpty(int id) {
        TextInputLayout textInputLayout = getView().findViewById(id);
        if (TextUtils.isEmpty(textInputLayout.getEditText().getText().toString().trim())) {
            textInputLayout.setError("This field is required");
            return true;
        }
        textInputLayout.setError(null);
        return false;
    }


    /**
     * Gets the user input as a string from a given TextInputEditText
     *
     * @param id Id of the TextInputEditText object
     * @return The user input String
     * @throws NullPointerException if the input text is null
     */
    protected String getInputText(int id) {
        return Objects.requireNonNull(((TextInputEditText) requireView().findViewById(id)).getText()).toString();
    }

    /**
     * Initializes and configures a Date Picker for selecting past/present acquisition dates.
     *
     * @param rootView The root view of the UI where the date picker is to be displayed.
     */
    private void initDatePicker(View rootView) {
        dateTextView = rootView.findViewById(R.id.add_item_date);

        // Create constraint to restrict dates to past/present
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(System.currentTimeMillis());
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateAcquired = new Date(selection);
            dateTextView.setText(datePicker.getHeaderText());
            ((TextInputLayout) rootView.findViewById(R.id.add_item_date_layout)).setError(null);
        });

        // Show DatePicker when date field selected
        dateTextView.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "Date Picker"));
    }

    /**
     * Initializes text validators for required input fields on the add item form.
     *
     * @param rootView The root view of the UI where the input fields are located.
     */
    private void initTextValidators(View rootView) {
        TextInputEditText descView = rootView.findViewById(R.id.add_item_description);
        TextInputEditText costView = rootView.findViewById(R.id.add_item_cost);

        // Add text watchers for empty input to both description and cost
        TextWatcher emptyTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.equals(descView.getEditableText())) {
                    isRequiredFieldEmpty(R.id.add_item_description_layout);
                } else if (editable == costView.getEditableText()) {
                    isRequiredFieldEmpty(R.id.add_item_cost_layout);
                }
            }
        };
        descView.addTextChangedListener(emptyTextWatcher);
        costView.addTextChangedListener(emptyTextWatcher);

        // Add listener for rounding cost to 2 decimals
        costView.setOnFocusChangeListener((v, b) -> {
            String costString = costView.getText().toString();
            try {
                costString = new BigDecimal(costString).setScale(2, RoundingMode.HALF_UP).toString();
                costView.setText(costString);
            } catch (NumberFormatException ignored) {
            }
        });
    }
}