package com.example.ns.firebaseauthcus;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText emailn;
    private EditText namen;
    private EditText passwordn;
    private EditText phnnon;
    private Button signupn;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailn = findViewById(R.id.email);
        namen = findViewById(R.id.name);
        passwordn = findViewById(R.id.password);
        phnnon = findViewById(R.id.phnno);
        signupn = findViewById(R.id.signup);
        signupn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view == signupn){
            registeruser();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){


        }
    }

    public void registeruser(){

        final String email = emailn.getText().toString().trim();
        String password = passwordn.getText().toString().trim();
        final String name = namen.getText().toString().trim();
        final String phnno = phnnon.getText().toString().trim();


        if(email.isEmpty()){
            emailn.setError("Email is empty");
            emailn.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailn.setError("Invalid email");
            emailn.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordn.setError("Password is empty");
            passwordn.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordn.setError("Password length should be less than 6");
            passwordn.requestFocus();
            return;
        }
        if(name.length() < 6){
            namen.setError("Name length should be more than 6");
            namen.requestFocus();
            return;
        }

        if(phnno.length() != 10){
            phnnon.setError("Invalid phone number");
            phnnon.requestFocus();
            return;
        }

        progressDialog.setMessage("Creating account....please wait");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            user user = new user(email,phnno,name);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this,"Account created succesfully",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        }
                        else {
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
