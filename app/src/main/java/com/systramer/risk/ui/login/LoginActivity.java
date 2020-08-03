package com.systramer.risk.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.systramer.risk.ListaCitasActivity;
import com.systramer.risk.R;
import android.provider.Settings.Secure;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    public String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private RequestQueue requestQueue;
    private LoginViewModel loginViewModel;
    public String Id;
    public TelephonyManager manager;
    public String IMEI;
    public ProgressBar loadingProgressBar;
    ConstraintLayout layout;
    private Snackbar snackbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
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
                    String user = usernameEditText.getText().toString().trim();
                    String pass = passwordEditText.getText().toString().trim();
                    ValidarSesion(user, pass);
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                //finish();
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
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
        layout = (ConstraintLayout) findViewById(R.id.container);
        //OBTENER IMEI
        manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(permiso == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try{
                    IMEI = manager.getImei().toString();
                }
                catch (Exception e){
                    IMEI = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                }

            }
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},123);
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        //Toast.makeText(getApplicationContext(), IMEI, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();

    }
    private void ValidarSesion(String User, String Pass) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try{
                    IMEI = manager.getImei();
                }
                catch (Exception e){
                    IMEI = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
                }
            }
            final JSONObject Parametros = new JSONObject();
            try {
                Parametros.put("Correo", User);
                Parametros.put("Password", Pass);
                Parametros.put("Imei", IMEI);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final String savedata = Parametros.toString();
            String URL = "https://risk-management-backend-dot-systramer.uc.r.appspot.com/public/api/Sesion/UsuarioAplicacion";
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String Mensaje = obj.getString("Message");
                        if (Mensaje.equals("Consulta Exitosa")) {
                            String Data = obj.getString("Data");
                            //for (int i = 0; i < Data.length(); i++){
                            //JSONObject Fila = Data.getJSONObject(i);
                            switch (Data) {
                                case "Usuario incorrecto":
                                    Toast.makeText(LoginActivity.this, "Usuario y/o contraseña incorrecto(s)", Toast.LENGTH_LONG).show();
                                    //snackbar.make(layout, "Usuario y/o contraseña incorrecto(s)", Snackbar.LENGTH_LONG).show();
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                    break;
                                case "Contraseña incorrecta":
                                    Toast.makeText(LoginActivity.this, "Usuario y/o contraseña incorrecto(s)", Toast.LENGTH_LONG).show();
                                    //snackbar.make(layout, "Usuario y/o contraseña incorrecto(s)", Snackbar.LENGTH_LONG).show();
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                    break;
                                case "IMEI incorrecto":
                                    Toast.makeText(LoginActivity.this, "IMEI incorrecto", Toast.LENGTH_LONG).show();
                                    //snackbar.make(layout, "IMEI incorrecto", Snackbar.LENGTH_LONG).show();
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                    break;
                                default:
                                    JSONObject Informacion = new JSONObject(Data);
                                    Id = Informacion.getString("Id");
                                    String Nombre = Informacion.getString("Nombre");
                                    String Telefono = Informacion.getString("Telefono");
                                    //Toast.makeText(LoginActivity.this, Telefono, Toast.LENGTH_LONG).show();
                                    /*
                                    Toast.makeText(getApplicationContext(), "Bienvenido " + Nombre, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, ListaCitasActivity.class);
                                    intent.putExtra("IdUsuario", Id);
                                    startActivity(intent);
                                    finish();
                                    */

                                    sendVerificationCodeToUser("+52"+Telefono);
                                    break;
                            }
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        onStop();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return savedata == null ? null : savedata.getBytes("utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            requestQueue.add(stringRequest);
        }
        else{
            Toast.makeText(this, "No tienes permiso para tomar el IMEI", Toast.LENGTH_LONG).show();
        }
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Bienvenido ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, ListaCitasActivity.class);
                    intent.putExtra("IdUsuario", Id);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void sendVerificationCodeToUser(String Telefono){
        //Toast.makeText(LoginActivity.this,Telefono,Toast.LENGTH_LONG).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Telefono,
                60,
                TimeUnit.SECONDS,
                LoginActivity.this,
                mCallbacks);

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
    {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            /*
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(String.valueOf(LoginActivity.this), "onVerificationCompleted:" + credential);
            */
            signInWithPhoneAuthCredential(credential);


        }

        private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if(code != null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            loadingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(String.valueOf(LoginActivity.this), "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            // ...
        }
    };
}