package cse489.project.doctorsappointmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class History extends AppCompatActivity {
  LinearLayout history_layout;
  ImageView home,appointmentBtn,history;
  ImageView Logout;
  FirebaseAuth auth;
  FirebaseUser user;
  TextView Name;
  FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
    history_layout=findViewById(R.id.history_layout);
    Logout=findViewById(R.id.logout);
    home=findViewById(R.id.home);
    appointmentBtn=findViewById(R.id.appointmentBtn);
    history=findViewById(R.id.history);
    auth=FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    user=auth.getCurrentUser();

    Logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(History.this, LogInActivity.class);
        startActivity(i);
        finish();
      }
    });
    displayAppointmentsWithZeroValue();
    home.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(History.this, Homepage.class);
        startActivity(i);
        finish();

      }
    });
    appointmentBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(History.this, AppontmentForm.class);
        startActivity(i);

      }
    });
    history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(History.this, History.class);
        startActivity(i);

      }
    });
  }
  private void displayAppointmentsWithZeroValue() {
    db.collection("appointments")
        .whereEqualTo("p_id", user.getUid())
        .whereEqualTo("passed", "1")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
              for (QueryDocumentSnapshot document : task.getResult()) {
                String appointmentDate = document.getString("date");
                String appointmentTime = document.getString("time");
                String name = document.getString("name");
                String phone = document.getString("phone");
                String age = document.getString("age");
                String address = document.getString("address");
                String email = document.getString("email");
                String gender = document.getString("gender");


                View appointmentCard = getLayoutInflater().inflate(R.layout.historycard, null);
                TextView dateTextView = appointmentCard.findViewById(R.id.datelist);
                TextView timeTextView = appointmentCard.findViewById(R.id.timelist);
                TextView nameTextview = appointmentCard.findViewById(R.id.name);
                dateTextView.setText(appointmentDate);
                timeTextView.setText(appointmentTime+"PM");
                nameTextview.setText(name);
                history_layout.addView(appointmentCard);
                appointmentCard.setOnLongClickListener(new View.OnLongClickListener() {
                  @Override
                  public boolean onLongClick(View v) {
                    showCancelDialog(History.this,appointmentDate,appointmentTime);
                    return true;
                  }
                });
                appointmentCard.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    Intent historydetailsIntent = new Intent(History.this, HistoryDetails.class);
                    Bundle b = new Bundle();
                    b.putString("name", name);
                    b.putString("phone", phone);
                    b.putString("age", age);
                    b.putString("time", appointmentTime);
                    b.putString("address", address);
                    b.putString("date", appointmentDate);
                    b.putString("gender", gender);
                    b.putString("email", email);
                    historydetailsIntent.putExtras(b);
                    startActivity(historydetailsIntent);

                  }
                });
              }
            } else {

            }
          }
        });
  }
  private void showCancelDialog(final Context context, final String date, final String time) {
    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.remove_history);

    Button btnCancel = dialog.findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        db.collection("appointments")
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                      for (QueryDocumentSnapshot document : task.getResult()) {

                        document.getReference().delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                  @Override
                                  public void onSuccess(Void aVoid) {

                                    dialog.dismiss();
                                    Toast.makeText(History.this, "Appointment Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(History.this, History.class);
                                    startActivity(i);
                                    finish();
                                  }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                  }
                                });
                      }
                    } else {
                      // Handle error
                    }
                  }
                });

      }
    });

    dialog.show();
  }
}