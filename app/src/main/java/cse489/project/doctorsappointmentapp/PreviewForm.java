package cse489.project.doctorsappointmentapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PreviewForm extends AppCompatActivity {
  TextView name,phone,age,email,time,date,address,gender;
  Button submit,cancel;
   FirebaseFirestore db;
   FirebaseAuth mAuth;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preview_form);
    name = findViewById(R.id.name);
    db = FirebaseFirestore.getInstance();
    mAuth=FirebaseAuth.getInstance();
    phone = findViewById(R.id.phone);
    age = findViewById(R.id.age);
    email=findViewById(R.id.email);
    time = findViewById(R.id.time);
    address = findViewById(R.id.address);
    gender = findViewById(R.id.gender);
    date = findViewById(R.id.date);
    submit = findViewById(R.id.submit);
    cancel = findViewById(R.id.cancel);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    String Name = extras.getString("name");
    String Phone = extras.getString("phone");
    String Age = extras.getString("age");
    String Time = extras.getString("time");
    String Address = extras.getString("address");
    String Gender = extras.getString("gender");
    String Date = extras.getString("date");
    name.setText(Name);
    phone.setText(Phone);
    age.setText(Age);
    time.setText(Time);
    address.setText(Address);
    gender.setText(Gender);
    date.setText(Date);
    FirebaseUser currentUser = mAuth.getCurrentUser();
    if(currentUser!=null) {
      email.setText(currentUser.getEmail());
      submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Map<String, Object> appointment = new HashMap<>();
          appointment.put("name", Name);
          appointment.put("p_id", currentUser.getUid());
          appointment.put("email", currentUser.getEmail());
          appointment.put("phone", Phone);
          appointment.put("age", Age);
          appointment.put("time", Time);
          appointment.put("date", Date);
          appointment.put("address", Address);
          appointment.put("gender", Gender);
          appointment.put("passed","0");
          db.collection("appointments")
                  .add(appointment)
                  .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                      Toast.makeText(PreviewForm.this, "New Appointment Added on " + Date, Toast.LENGTH_SHORT).show();
                      Intent i = new Intent(PreviewForm.this, Homepage.class);
                      startActivity(i);
                      finish();
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.w(TAG, "Error adding appointment", e);
                    }
                  });

        }
      });
      cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent i = new Intent(PreviewForm.this, AppontmentForm.class);
          startActivity(i);
          finish();

        }

      });
    }
    else{
      Toast.makeText(PreviewForm.this, "User is Empty", Toast.LENGTH_SHORT).show();
    }

  }
}