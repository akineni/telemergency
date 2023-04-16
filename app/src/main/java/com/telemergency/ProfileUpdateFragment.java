package com.telemergency;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileUpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUpdateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextInputEditText date_of_birth, address, city, state, zip_code, nk_surname, nk_first_name, nk_relationship, nk_phone,
    nk_email, med_other_cond, med_allergies, med_ptr, med_ins, phy_surname, phy_first_name, phy_phone,
            phy_specialty, phy_hos_ref;
    private Spinner gender, blood_group, genotype;
    private GridLayout gridLayout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private OnFragmentInteractionListener mListener;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private TextInputEditText dateOfBirthEditText;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String[] date = dateOfBirthEditText.getText().toString().split("/");
            if(dateOfBirthEditText.getText().toString().isEmpty()) {
                final Calendar c = Calendar.getInstance();
                return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
            }

            if(date.length == 3){
                return new DatePickerDialog(getActivity(), this, Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
            }

            return super.onCreateDialog(savedInstanceState);
        }

        public void setDateOfBirthEditText(TextInputEditText textInputEditText) {
            dateOfBirthEditText = textInputEditText;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            dateOfBirthEditText.setText(d + "/" + (m+1) + "/" + y);
        }
    }

    public ProfileUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileUpdateFragment newInstance(String param1, String param2) {
        ProfileUpdateFragment fragment = new ProfileUpdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getActivity().getPreferences(Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_update, container, false);

        date_of_birth = view.findViewById(R.id.dateOfBirth);
        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dateFragment = new DatePickerFragment();
                ((DatePickerFragment) dateFragment).setDateOfBirthEditText(date_of_birth);
                dateFragment.show(getActivity().getSupportFragmentManager(), "Datepicker");
            }
        });

        blood_group = view.findViewById(R.id.bloodGroup);
        ArrayAdapter<String> spinnerBloodTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item_top, getResources().getStringArray(R.array.blood_group));
        spinnerBloodTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
        blood_group.setAdapter(spinnerBloodTypeAdapter);

        genotype = view.findViewById(R.id.genotype);
        ArrayAdapter<String> spinnerGenotypeAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item_top, getResources().getStringArray(R.array.genotype));
        spinnerGenotypeAdapter.setDropDownViewResource(R.layout.spinner_item);
        genotype.setAdapter(spinnerGenotypeAdapter);

        gender = view.findViewById(R.id.gender);
        ArrayAdapter<String> spinnerGenderAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item_top, getResources().getStringArray(R.array.gender));
        spinnerGenderAdapter.setDropDownViewResource(R.layout.spinner_item);
        gender.setAdapter(spinnerGenderAdapter);

        address = view.findViewById(R.id.address);
        city = view.findViewById(R.id.city);
        state = view.findViewById(R.id.state);
        zip_code = view.findViewById(R.id.zipCode);

        nk_surname = view.findViewById(R.id.surname);
        nk_first_name = view.findViewById(R.id.firstName);
        nk_relationship = view.findViewById(R.id.relationship);
        nk_phone = view.findViewById(R.id.phone);
        nk_email = view.findViewById(R.id.email);

        med_other_cond = view.findViewById(R.id.otherConditions);
        med_allergies = view.findViewById(R.id.allergies);
        med_ptr = view.findViewById(R.id.priorTransfusionReaction);
        med_ins = view.findViewById(R.id.insurance);

        phy_surname = view.findViewById(R.id.surnamePhysician);
        phy_first_name = view.findViewById(R.id.firstNamePhysician);
        phy_phone = view.findViewById(R.id.phonePhysician);
        phy_specialty = view.findViewById(R.id.specialty);
        phy_hos_ref = view.findViewById(R.id.hospitalReference);

        gridLayout  = view.findViewById(R.id.gridLayoutInc);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = firebaseDatabase.getReference("users");

        userRef.child(mAuth.getCurrentUser().getUid()).child("date_of_birth").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    date_of_birth.setText(dataSnapshot.getValue(String.class));

                    String str_age = "";

                    if (date_of_birth.getText().toString().split("/").length == 3) {
                        int diff = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(date_of_birth.getText().toString().split("/")[2]);
                        str_age = diff + "";
                    }
                    editor.putString("age", str_age);
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    gender.setSelection(dataSnapshot.getValue(Integer.class));

                    editor.putString("gender", gender.getSelectedItem().toString());
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) address.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("city").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) city.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) state.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("zip_code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) zip_code.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("next_of_kin").child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) nk_surname.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("next_of_kin").child("first_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) nk_first_name.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("next_of_kin").child("relationship").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) nk_relationship.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        userRef.child(mAuth.getCurrentUser().getUid()).child("next_of_kin").child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) nk_phone.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("next_of_kin").child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) nk_email.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("specialist").child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) phy_surname.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("specialist").child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) phy_surname.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("specialist").child("first_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) phy_first_name.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("specialist").child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) phy_phone.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("specialist").child("specialty").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) phy_specialty.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("specialist").child("hospital_reference").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) phy_hos_ref.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("blood_group").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    blood_group.setSelection(dataSnapshot.getValue(Integer.class));

                    editor.putString("blood_group", blood_group.getSelectedItem().toString());
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("genotype").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    genotype.setSelection(dataSnapshot.getValue(Integer.class));
                    editor.putString("genotype", genotype.getSelectedItem().toString());
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("medical_conditions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String[] medical_conditions = dataSnapshot.getValue(String.class).split(", ");
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        if (gridLayout.getChildAt(i) instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) gridLayout.getChildAt(i);
                            for (int j = 0; j < medical_conditions.length; j++) {
                                if (medical_conditions[j].equals(checkBox.getText().toString()))
                                    checkBox.setChecked(true);
                            }
                        }
                    }

                    editor.putString("medical_conditions", dataSnapshot.getValue(String.class));
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("other_conditions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    med_other_cond.setText(dataSnapshot.getValue(String.class));

                    editor.putString("other_medical_conditions", med_other_cond.getText().toString().trim().replace("\n", ", "));
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("allergies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    med_allergies.setText(dataSnapshot.getValue(String.class));

                    editor.putString("allergies", med_allergies.getText().toString().trim().replace("\n", ", "));
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("prior_transfusion_reaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) med_ptr.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        userRef.child(mAuth.getCurrentUser().getUid()).child("medical").child("insurance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) med_ins.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        MaterialButton updateButton = view.findViewById(R.id.update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Update");
                progressDialog.setMessage("Please be patient while we update your account");
                progressDialog.show();

                String key = mAuth.getCurrentUser().getUid();
                userRef.child(key).child("date_of_birth").setValue(date_of_birth.getText().toString());
                userRef.child(key).child("gender").setValue(gender.getSelectedItemPosition());
                userRef.child(key).child("address").setValue(address.getText().toString().trim());
                userRef.child(key).child("city").setValue(city.getText().toString().trim());
                userRef.child(key).child("state").setValue(state.getText().toString().trim());
                userRef.child(key).child("zip_code").setValue(zip_code.getText().toString().trim());

                userRef.child(key).child("next_of_kin").child("surname").setValue(nk_surname.getText().toString().trim());
                userRef.child(key).child("next_of_kin").child("first_name").setValue(nk_first_name.getText().toString().trim());
                userRef.child(key).child("next_of_kin").child("relationship").setValue(nk_relationship.getText().toString().trim());
                userRef.child(key).child("next_of_kin").child("phone").setValue(nk_phone.getText().toString().trim());
                userRef.child(key).child("next_of_kin").child("email").setValue(nk_email.getText().toString().trim());

                userRef.child(key).child("medical").child("blood_group").setValue(blood_group.getSelectedItemPosition());
                userRef.child(key).child("medical").child("genotype").setValue(genotype.getSelectedItemPosition());

                String medical_conditions = "";
                for(int i = 0; i < gridLayout.getChildCount(); i++) {
                    if(gridLayout.getChildAt(i) instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) gridLayout.getChildAt(i);
                        if(checkBox.isChecked()) medical_conditions += " " +checkBox.getText().toString().replace(" ", "_");
                    }
                }
                medical_conditions = medical_conditions.trim().replace(" ", ", ");
                medical_conditions = medical_conditions.trim().replace("_", " ");

                userRef.child(key).child("medical").child("medical_conditions").setValue(medical_conditions);
                userRef.child(key).child("medical").child("other_conditions").setValue(med_other_cond.getText().toString().trim());
                userRef.child(key).child("medical").child("allergies").setValue(med_allergies.getText().toString().trim());
                userRef.child(key).child("medical").child("prior_transfusion_reaction").setValue(med_ptr.getText().toString().trim());
                userRef.child(key).child("medical").child("insurance").setValue(med_ins.getText().toString().trim());

                userRef.child(key).child("specialist").child("surname").setValue(phy_surname.getText().toString().trim());
                userRef.child(key).child("specialist").child("first_name").setValue(phy_first_name.getText().toString().trim());
                userRef.child(key).child("specialist").child("phone").setValue(phy_phone.getText().toString().trim());
                userRef.child(key).child("specialist").child("specialty").setValue(phy_specialty.getText().toString().trim());
                userRef.child(key).child("specialist").child("hospital_reference").setValue(phy_hos_ref.getText().toString().trim());

                progressDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false).setMessage("Your account has been updated successfully").setTitle(
                        Html.fromHtml("<font color='#30D5F2'>Update successful</font>")).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                        }).setIcon(R.drawable.ic_check_black_24dp);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(0);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
