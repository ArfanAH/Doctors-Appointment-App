package cse489.project.doctorsappointmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChange extends AppCompatActivity {
  ImageView home,appointmentBtn,history;
  EditText CurrPass, NewPass;
  Button changepass;
  boolean passwordVisible;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_password_change);
    home=findViewById(R.id.home);
    appointmentBtn=findViewById(R.id.appointmentBtn);
    history=findViewById(R.id.history);
    CurrPass = findViewById(R.id.currPass);
    NewPass = findViewById(R.id.newPass);
    changepass=findViewById(R.id.changepass);
    changepass.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        changePassword();
      }
    });
    CurrPass.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int Right=2;
        if(event.getAction()==MotionEvent.ACTION_UP){
          if(event.getRawX()>=CurrPass.getRight()-CurrPass.getCompoundDrawables()[Right].getBounds().width()){
            int selection = CurrPass.getSelectionEnd();
            if(passwordVisible){
              CurrPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
              CurrPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
              passwordVisible= false;

            }
            else{

              CurrPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_remove_red_eye_24,0);
              CurrPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
              passwordVisible= true;

            }
            CurrPass.setSelection(selection);
            return true;
          }
        }
        return false;
      }
    });
    NewPass.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final int Right=2;
        if(event.getAction()==MotionEvent.ACTION_UP){
          if(event.getRawX()>=NewPass.getRight()-NewPass.getCompoundDrawables()[Right].getBounds().width()){
            int selection = NewPass.getSelectionEnd();
            if(passwordVisible){
              NewPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
              NewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
              passwordVisible= false;

            }
            else{

              NewPass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_remove_red_eye_24,0);
              NewPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
              passwordVisible= true;

            }
            NewPass.setSelection(selection);
            return true;
          }
        }
        return false;
      }
    });

    home.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(PasswordChange.this, Homepage.class);
        startActivity(i);
        finish();

      }
    });
    appointmentBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(PasswordChange.this, AppontmentForm.class);
        startActivity(i);

      }
    });
    history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(PasswordChange.this, History.class);
        startActivity(i);

      }
    });
  }
  private void changePassword() {
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String currentPassword = CurrPass.getText().toString().trim();
    final String newPassword = NewPass.getText().toString().trim();

    if (user != null && user.getEmail() != null) {

      if (newPassword.length() < 6 || !newPassword.matches(".*\\d.*") || !newPassword.matches(".*[a-zA-Z].*")) {
        Toast.makeText(PasswordChange.this, "New password should be at least 6 characters long and contain both letters and numbers.", Toast.LENGTH_SHORT).show();
        return;
      }

      EmailAuthCredential credential = (EmailAuthCredential) EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

      user.reauthenticate(credential)
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> updateTask) {
                                if (updateTask.isSuccessful()) {
                                  Toast.makeText(PasswordChange.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                  Intent i = new Intent(PasswordChange.this, Homepage.class);
                                  startActivity(i);
                                  finish();

                                } else {
                                  Toast.makeText(PasswordChange.this, "Password update failed. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                              }
                            });
                  } else {
                    Toast.makeText(PasswordChange.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                  }
                }
              });
    }
  }

}