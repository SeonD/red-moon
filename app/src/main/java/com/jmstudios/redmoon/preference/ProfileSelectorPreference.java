package com.jmstudios.redmoon.preference;

import android.preference.Preference;
import android.widget.Spinner;
import android.widget.Button;
import android.view.View;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.text.InputType;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import com.jmstudios.redmoon.R;
import com.jmstudios.redmoon.model.ProfilesModel;

public class ProfileSelectorPreference extends Preference
    implements OnItemSelectedListener {
    public static final int DEFAULT_VALUE = 1;

    private static final String TAG = "ProfileSelectorPreference";
    private static final boolean DEBUG = true;

    private static final int DEFAULT_OPERATIONS_AM = 3;

    private Spinner mProfileSpinner;
    private Button mProfileActionButton;
    ArrayAdapter<CharSequence> mArrayAdapter;
    private int mProfile;
    private View mView;
    private ProfilesModel mProfilesModel;

    private ArrayList<CharSequence> mDefaultOperations;

    public ProfileSelectorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_profile_selector);

        mProfilesModel = new ProfilesModel(context);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mProfile = getPersistedInt(DEFAULT_VALUE);
        } else {
            mProfile = (Integer) defaultValue;
            persistInt(mProfile);
        }
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        mView = view;

        mProfileSpinner = (Spinner) view.findViewById(R.id.profile_spinner);
        mProfileActionButton = (Button) view.findViewById(R.id.profile_action_button);

        initLayout();
    }

    private void initLayout() {
        if (DEBUG) Log.i(TAG, "Starting initLayout");
        // The default operations first need to be converted to an ArrayList,
        // because the ArrayAdapter will turn it into an AbstractList otherwise,
        // which doesn't support certain actions, like adding elements.
        // See: http://stackoverflow.com/a/3200631
        mDefaultOperations = new ArrayList<CharSequence>
            (Arrays.asList(getContext().getResources().getStringArray(R.array.standard_profiles_array)));
        mArrayAdapter = new ArrayAdapter<CharSequence>
            (getContext(), android.R.layout.simple_spinner_item, mDefaultOperations);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        readProfiles();

        mProfileSpinner.setAdapter(mArrayAdapter);
        mProfileSpinner.setSelection(mProfile);
        mProfileSpinner.setOnItemSelectedListener(this);

        updateButtonSetup();
    }

    private void updateButtonSetup() {
        if (mProfile > (DEFAULT_OPERATIONS_AM - 1)) {
            if (DEBUG) Log.i(TAG, "Setting remove button");
            mProfileActionButton.setText(getContext().getResources().getString
                                         (R.string.button_remove_profile));
            mProfileActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openRemoveProfileDialog();
                    }
                });

        } else {
            if (DEBUG) Log.i(TAG, "Setting add button");
            mProfileActionButton.setText(getContext().getResources().getString
                                         (R.string.button_add_profile));
            mProfileActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAddNewProfileDialog();
                    }
                });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (DEBUG) Log.i(TAG, "Item " + pos + " selected");
        mProfile = pos;
        persistInt(mProfile);
        updateButtonSetup();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void openRemoveProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString
                         (R.string.remove_profile_dialog_title));

        String okString = getContext().getResources().getString(R.string.button_remove_profile);
        String cancelString = getContext().getResources().getString(R.string.cancel_dialog);

        builder.setPositiveButton(okString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        mProfilesModel.removeProfile(mProfile - DEFAULT_OPERATIONS_AM);
                        if (mProfile >= DEFAULT_OPERATIONS_AM + mProfilesModel.getProfiles().size())
                            mProfile--;
                        initLayout();
                }
            });

        builder.setNegativeButton(cancelString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        builder.show();
    }

    private void openAddNewProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getResources().getString(R.string.add_new_profile_dialog_title));

        final EditText nameInput = new EditText(getContext());
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        nameInput.setHint(getContext().getResources().getString(R.string.add_new_profile_edit_hint));

        builder.setView(nameInput);

        String okString = getContext().getResources().getString(R.string.ok_dialog);
        String cancelString = getContext().getResources().getString(R.string.cancel_dialog);

        builder.setPositiveButton(okString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!(nameInput.getText().toString().trim().equals(""))) {
                        ProfilesModel.Profile profile = new ProfilesModel.Profile
                            (nameInput.getText().toString(), 100, 100, 100);

                        mProfilesModel.addProfile(profile);
                        mArrayAdapter.add((CharSequence) profile.mProfileName);

                         mProfileSpinner.setSelection
                            (mProfilesModel.getProfiles().size() - 1 + DEFAULT_OPERATIONS_AM);
                    } else {
                        dialog.cancel();
                    }
                }
            });

        builder.setNegativeButton(cancelString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        builder.show();
    }

    //Section: Reading and writing profiles

    /**
     * Reads the profiles saved in the SharedPreference in the spinner
     */
    public void readProfiles() {
        ArrayList<ProfilesModel.Profile> profiles = mProfilesModel.getProfiles();

        for (ProfilesModel.Profile profile : profiles) {
            mArrayAdapter.add((CharSequence) profile.mProfileName);
        }
    }
}
