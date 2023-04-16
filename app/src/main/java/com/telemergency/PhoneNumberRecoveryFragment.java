package com.telemergency;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhoneNumberRecoveryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhoneNumberRecoveryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhoneNumberRecoveryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PhoneNumberRecoveryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhoneNumberRecoveryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhoneNumberRecoveryFragment newInstance(String param1, String param2) {
        PhoneNumberRecoveryFragment fragment = new PhoneNumberRecoveryFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_phone_number_recovery, container, false);
        final MaterialButton buttonSubmit = view.findViewById(R.id.submit);
        final TextInputLayout phoneLayout = view.findViewById(R.id.phoneLayout);
        MaterialButton buttonOtherRecoveryOption = view.findViewById(R.id.buttonOtherRecoveryOption);
        buttonOtherRecoveryOption.setText(Html.fromHtml("<span>Or <u>recover password via <b>email</b></u></span"));
        buttonOtherRecoveryOption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        buttonOtherRecoveryOption.setTypeface(ResourcesCompat.getFont(getContext(),R.font.poppins));
        buttonOtherRecoveryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(Uri.parse("email_recovery"));
                }
            }
        });

        final TextInputEditText phoneEditText = view.findViewById(R.id.phone);
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Validator.isPhoneValid(phoneEditText.getText().toString())) {
                    phoneLayout.setError(getString(R.string.invalid_phone));
                    buttonSubmit.setEnabled(false);
                }else {
                    phoneLayout.setError(null);
                    buttonSubmit.setEnabled(true);
                }
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

    @Override
    public void onStart() {
        super.onStart();
        TextInputEditText phoneEditText = getView().findViewById(R.id.phone);
        phoneEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        phoneEditText.setTypeface(ResourcesCompat.getFont(getContext(),R.font.poppins));
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
