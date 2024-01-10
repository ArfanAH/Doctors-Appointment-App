package cse489.project.doctorsappointmentapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
  EditText userName,email,password,confpassword;
  Button signupBtn;
  TextView loginNow;
  boolean passwordVisible,confpasswordVisible;
 String token;
  ProgressBar progressbar;
  FirebaseAuth mAuth;
  FirebaseFirestore db;
  @Override
  public void onStart() {
    super.onStart();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser != null){
      Intent i = new Intent(SignUpActivity.this, Homepage.class);
      startActivity(i);
    }
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    mAuth=FirebaseAuth.getInstance();
    db=FirebaseFirestore.getInstance();
    userName = findViewById(R.id.name);
    email = findViewById(R.id.email);
    password = findViewById(R.id.password);
    confpassword = findViewById(R.id.confirm_password);
    signupBtn = findViewById(R.id.signupBtn);
    loginNow = findViewById(R.id.loginNow);
    progressbar = findViewById(R.id.signProgressBar);
    password.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int Right=2;
        if(event.getAction()==MotionEvent.ACTION_UP){
          if(event.getRawX()>=password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
            int selection = password.getSelectionEnd();
            if(passwordVisible){
              password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_visibility_off_24,0);
              password.setTransformationMethod(PasswordTransformationMethod.getInstance());
              passwordVisible= false;

            }
            else{

              password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_remove_red_eye_24,0);
              password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
              passwordVisible= true;

            }
            password.setSelection(selection);
            return true;
          }
        }
        return false;
      }
    });

    confpassword.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int Right=2;
        if(event.getAction()==MotionEvent.ACTION_UP){
          if(event.getRawX()>=confpassword.getRight()-confpassword.getCompoundDrawables()[Right].getBounds().width()){
            int selection = confpassword.getSelectionEnd();
            if(confpasswordVisible){
              confpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_visibility_off_24,0);
              confpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
              confpasswordVisible= false;

            }
            else{

              confpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_remove_red_eye_24,0);
              confpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
              confpasswordVisible= true;

            }
            confpassword.setSelection(selection);
            return true;
          }
        }
        return false;
      }
    });

    signupBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        String name = userName.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        String confPassword = confpassword.getText().toString();


        String validationMessage = validateInputs(name, Email, Password, confPassword);
        if (!validationMessage.isEmpty()) {
          Toast.makeText(SignUpActivity.this, validationMessage, Toast.LENGTH_SHORT).show();
        }
        else {
          progressbar.setVisibility(View.VISIBLE);
          signupBtn.setBackgroundColor(Color.parseColor("#BDBDBD"));
          new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

              mAuth.createUserWithEmailAndPassword(Email,Password)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                        String userID = mAuth.getCurrentUser().getUid();
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("name", name);
                        userInfo.put("email", Email);
                        db.collection("users").document(userID)
                            .set(userInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                Toast.makeText(SignUpActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUpActivity.this, Homepage.class);
                                startActivity(i);
                                finish();
                              }
                            });
                      } else {

                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                      }

                    }
                  });

            }
          },4000);

        }
      }
    });
    loginNow.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(i);
        finish();

      }
    });
  }
  private String validateInputs(String name, String email,String password, String confPassword) {
    boolean isNameValid = name.matches("[a-zA-Z ]{4,15}+");
    boolean isEmailValid = email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    boolean isPasswordValid = password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$");
    boolean passwordsMatch = password.equals(confPassword);
    boolean empty = password.isEmpty() || name.isEmpty()  || confPassword.isEmpty() || email.isEmpty();
    if (empty) {
      return "You have to fill all the inputs correctly.";
    } else if (!isNameValid) {
      return "Invalid name. It should contain 4 to 15 characters.";
    } else if (!isEmailValid) {
      return "Invalid email address.";
    }  else if (!isPasswordValid) {
      return "Invalid password.Password must contain at least one letter,digit,and at least 6 characters long.";
    } else if (!passwordsMatch) {
      return "Passwords did not match.";
    }

    return "";
  }
}