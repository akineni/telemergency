package com.telemergency.ui.login;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.telemergency.DashboardActivity;
import com.telemergency.ForgotPasswordActivity;
import com.telemergency.R;
import com.telemergency.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private FirebaseAuth mAuth;
    TextInputEditText usernameEditText, passwordEditText;
    ProgressBar loadingProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.userId);
        passwordEditText = findViewById(R.id.password);
        final TextInputLayout usernameInputLayout = findViewById(R.id.userIdLayout);
        final TextInputLayout passwordInputLayout = findViewById(R.id.passwordLayout);
        final MaterialButton loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        MaterialButton signupButton = findViewById(R.id.buttonSignup);
        MaterialButton forgotPasswordButton = findViewById(R.id.buttonForgotPassword);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }

                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameInputLayout.setError(getString(loginFormState.getUsernameError()));
                }else{
                    usernameInputLayout.setError(null);
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordInputLayout.setError(getString(loginFormState.getPasswordError()));
                }else{
                    passwordInputLayout.setError(null);
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(new Intent(getBaseContext(),  DashboardActivity.class)));
            finish();
        }
    }

    private  void signIn() {
        mAuth.signInWithEmailAndPassword(usernameEditText.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(new Intent(getBaseContext(),  DashboardActivity.class)));
                            finish();
                        }else {
                            loadingProgressBar.setVisibility(View.INVISIBLE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(task.getException().getMessage()).setTitle(
                                    Html.fromHtml("<font color='#C75450'>Login Failed</font>")).setIcon(R.drawable.ic_close_red_24dp);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.error));
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(0);
                        }
                    }
                });
    }
}
