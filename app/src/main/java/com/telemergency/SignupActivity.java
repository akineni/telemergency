package com.telemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class SignupActivity extends AppCompatActivity {

    TextInputEditText surnameEditText, firstnameEditText, othernamesEditText, phoneEditText, emailEditText, passwordEditText,
            confirmPasswordEditText;
    TextInputLayout surnameLayout, firstnameLayout, othernamesLayout, phoneLayout, emailLayout, passwordLayout, confirmPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        surnameEditText = findViewById(R.id.surname);
        firstnameEditText = findViewById(R.id.firstName);
        othernamesEditText = findViewById(R.id.other_names);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);

        surnameLayout = findViewById(R.id.surnameLayout);
        firstnameLayout = findViewById(R.id.firstNameLayout);
        othernamesLayout = findViewById(R.id.otherNameLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        MaterialButton btnSubmit = findViewById(R.id.signup);
        MaterialButton loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testEmpty(surnameLayout, surnameEditText);
                testEmpty(firstnameLayout, firstnameEditText);
                testEmpty(othernamesLayout, othernamesEditText);

                if(!Validator.isPhoneValid(phoneEditText.getText().toString())) {
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError(getString(R.string.invalid_phone));
                }else{
                    phoneLayout.setError(null);
                    phoneLayout.setErrorEnabled(false);
                }

                if(!Validator.isEmailValid(emailEditText.getText().toString())) {
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError(getString(R.string.invalid_email));
                }else {
                    emailLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                }

                if(passwordEditText.getText().toString().trim().length() < 8){
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError(getString(R.string.invalid_password));
                }else{
                    passwordLayout.setError(null);
                    passwordLayout.setErrorEnabled(false);
                }

                if(!confirmPasswordEditText.getText().toString().trim().equals(passwordEditText.getText().toString().trim())){
                    confirmPasswordLayout.setErrorEnabled(true);
                    confirmPasswordLayout.setError(getString(R.string.password_mismatch));
                }else{
                    confirmPasswordLayout.setError(null);
                    confirmPasswordLayout.setErrorEnabled(false);
                }


                ConstraintLayout root = findViewById(R.id.root);
                for(int i = 0; i < root.getChildCount(); i++) {
                    if(root.getChildAt(i) instanceof  TextInputLayout)
                        if(((TextInputLayout)root.getChildAt(i)).getError() != null) return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("Registration");
                progressDialog.setMessage("Please be patient while we create your account");
                progressDialog.show();

                final FirebaseAuth mAuth = FirebaseAuth.getInstance();

                final String surname, first_name, other_names, phone, email, password, key;
                surname = clean(surnameEditText.getText().toString());
                first_name = clean(firstnameEditText.getText().toString());
                other_names = clean(othernamesEditText.getText().toString());
                phone = phoneEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString();


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference userRef = firebaseDatabase.getReference("users");
                            String key = mAuth.getCurrentUser().getUid();
                            userRef.child(key).child("surname").setValue(surname);
                            userRef.child(key).child("first_name").setValue(first_name);
                            userRef.child(key).child("other_names").setValue(other_names);
                            userRef.child(key).child("phone").setValue(phone);
                            userRef.child(key).child("email").setValue(email);
                            userRef.child(key).child("password").setValue(password);

                            progressDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            builder.setCancelable(false).setMessage("Registration successful, you are now logged in").setTitle(
                                    Html.fromHtml("<font color='#30D5F2'>Registration successful</font>")).
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).setIcon(R.drawable.ic_check_black_24dp);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(0);
                        }else {
                            progressDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            builder.setMessage(task.getException().getMessage()).setTitle(
                                    Html.fromHtml("<font color='#C75450'>Registration Failed</font>")).setIcon(R.drawable.ic_close_red_24dp);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.error));
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(0);
                        }
                    }
                });
            }
        });

        surnameEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                testEmpty(surnameLayout, surnameEditText);
            }
        });
        firstnameEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                testEmpty(firstnameLayout, firstnameEditText);
            }
        });
        othernamesEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                testEmpty(othernamesLayout, othernamesEditText);
            }
        });
        phoneEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(!Validator.isPhoneValid(phoneEditText.getText().toString())) {
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError(getString(R.string.invalid_phone));
                }else{
                    phoneLayout.setError(null);
                    phoneLayout.setErrorEnabled(false);
                }
            }
        });
        emailEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(!Validator.isEmailValid(emailEditText.getText().toString())) {
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError(getString(R.string.invalid_email));
                }else {
                    emailLayout.setError(null);
                    emailLayout.setErrorEnabled(false);
                }

            }
        });

        passwordEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(passwordEditText.getText().toString().trim().length() < 8){
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError(getString(R.string.invalid_password));
                }else{
                    passwordLayout.setError(null);
                    passwordLayout.setErrorEnabled(false);
                }
            }
        });
        confirmPasswordEditText.addTextChangedListener(new Validator() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(!confirmPasswordEditText.getText().toString().trim().equals(passwordEditText.getText().toString().trim())){
                    confirmPasswordLayout.setErrorEnabled(true);
                    confirmPasswordLayout.setError(getString(R.string.password_mismatch));
                }else{
                    confirmPasswordLayout.setError(null);
                    confirmPasswordLayout.setErrorEnabled(false);
                }
            }
        });
    }

    private void testEmpty(TextInputLayout textInputLayout, TextInputEditText textInputEditText){
        if(Validator.isEmpty(textInputEditText.getText().toString())) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.required));
        }else {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }

    private String  clean(String s) {
        return s.trim().substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
