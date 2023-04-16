package com.telemergency;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int SEND_SMS_REQUEST_CODE = 200;

    private static boolean DELIVERED;

    public static final String SMS_DELIVERED = "com.telemergency.SMS_DELIVERED";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseDatabase firebaseDatabase;
    private String[] emergency_centers;
    private String message;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        perms();

        sharedPreferences = getActivity().getPreferences(getContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("emergency_centers");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editor.putString("emergency_centers", dataSnapshot.getValue(String.class));
                editor.commit();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        message = getString(R.string.app_name) + "\n\n";
        message += "Name: " + sharedPreferences.getString("surname","") + " " +
                sharedPreferences.getString("first_name","") + " " + sharedPreferences.getString("other_names","") + "\n";
        message += "Gender: " + sharedPreferences.getString("gender", "") + "\n";
        message += "Age: " + sharedPreferences.getString("age",  "") + "\n";
        message += "Blood group: " + sharedPreferences.getString("blood_group","") + "\n";
        message += "Genotype: " + sharedPreferences.getString("genotype", "") + "\n";
        message += "Medical conditions: " + sharedPreferences.getString("medical_conditions", "") + ", " +
        sharedPreferences.getString("other_medical_conditions", "") + "\n";
        message += "Allergies: " + sharedPreferences.getString("allergies","") ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        final ImageButton alertButton = view.findViewById(R.id.alertButton);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emergency_centers = sharedPreferences.getString("emergency_centers","+2349068857142").split(" ");
                try {
                    if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED){
                        if (!DELIVERED) {
                            SmsManager smsManager = SmsManager.getDefault();

                            ArrayList<PendingIntent> deliveredIntent = new ArrayList<>();
                            ArrayList<String> parts = smsManager.divideMessage(message);

                            for(int i = 0; i < parts.size(); i++){
                                deliveredIntent.add(PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_DELIVERED), 0));
                            }
                            if (emergency_centers != null) {
                                for (String center : emergency_centers) {
                                    smsManager.sendMultipartTextMessage(center,  null, parts,null,  deliveredIntent);
                                }
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.drawer_layout), "Relax, help is on the way", Snackbar.LENGTH_LONG);

                            snackbar.getView().setBackgroundColor(Color.WHITE);
                            TextView snackbarTextView = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                                snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            else
                                snackbarTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                            snackbarTextView.setTextColor(getResources().getColor(R.color.dodgerBlueLight));
                            snackbarTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins));

                            snackbar.show();
                        }
                    }else {
                        perms();
                    }
                }catch (Exception e) {
                    Toast.makeText(getContext(),"Failed to send alert, please try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  view;
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

    public static void setDelivered(boolean bool) {
        DELIVERED = bool;
    }

    private void perms() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Alerts are sent via SMS and requires the SMS permission granted").setTitle(
                        Html.fromHtml("<font color='#30D5F2'>SMS permission information</font>")).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(0);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
            }
        }
    }
}
