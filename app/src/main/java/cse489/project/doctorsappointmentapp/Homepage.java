package cse489.project.doctorsappointmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Homepage extends AppCompatActivity {
    TextView empty;
    ImageView home,appointmentBtn,history;
    ImageView Logout,userAvatar;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView Name;
    FirebaseFirestore db;
    Button details;
  LinearLayout appointmentsLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Name= findViewById(R.id.name);
        Logout=findViewById(R.id.logout);
        home=findViewById(R.id.home);
        userAvatar=findViewById(R.id.userAvatar);
        appointmentBtn=findViewById(R.id.appointmentBtn);
        history=findViewById(R.id.history);
        auth=FirebaseAuth.getInstance();
        empty=findViewById(R.id.empty);
        appointmentsLayout = findViewById(R.id.appointments_layout);
        details=findViewById(R.id.details);
        db = FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();
        if(user==null){
            Intent i = new Intent(Homepage.this, LogInActivity.class);
            startActivity(i);
            finish();
        }
        else {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        Name.setText(userName);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        displayUserAppointments();
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Homepage.this, LogInActivity.class);
                startActivity(i);
                finish();
            }
        });
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Homepage.this, details.class);
                startActivity(i);

            }
        });
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Homepage.this, PasswordChange.class);
                startActivity(i);

            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Homepage.this, Homepage.class);
                startActivity(i);
                finish();

            }
        });
        appointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Homepage.this, AppontmentForm.class);
                startActivity(i);

            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Homepage.this, History.class);
                startActivity(i);

            }
        });
    }
    private void displayUserAppointments() {
        db.collection("appointments")
                .whereEqualTo("p_id", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Calendar currentCalendar = Calendar.getInstance();
                            currentCalendar.set(Calendar.SECOND, 0);
                            currentCalendar.set(Calendar.MILLISECOND, 0);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String appointmentDate = document.getString("date");
                                String appointmentTimeRange = document.getString("time");
                                String value = document.getString("passed");

                                if (appointmentTimeRange != null && value.equals("0")) {
                                    try {
                                        String[] timeSplit = appointmentTimeRange.split("-");
                                        String startTime = timeSplit[0].trim();
                                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                                        String combinedDateTime = appointmentDate + " " + startTime + " PM";
                                        Calendar storedCalendar = Calendar.getInstance();
                                        storedCalendar.setTime(dateTimeFormat.parse(combinedDateTime));
                                        storedCalendar.set(Calendar.SECOND, 0);
                                        storedCalendar.set(Calendar.MILLISECOND, 0);
                                        if (storedCalendar.before(currentCalendar)) {
                                            document.getReference().update("passed", "1");
                                            Log.d("Appointment", "Updated passed value");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } displayAppointmentsWithZeroValue();
                        } else {
                            Log.d("Appointment", "Task unsuccessful: " + task.getException());
                        }
                    }
                });
    }


    private void displayAppointmentsWithZeroValue() {
        db.collection("appointments")
                .whereEqualTo("p_id", user.getUid())
                .whereEqualTo("passed", "0")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String appointmentDate = document.getString("date");
                                String appointmentTime = document.getString("time");
                                String name = document.getString("name");
                                String phone = document.getString("phone");
                                String age = document.getString("age");
                                String address = document.getString("address");
                                String p_id = document.getString("p_id");
                                String email = document.getString("email");
                                String gender = document.getString("gender");
                                String documentId = document.getId();
                                View appointmentCard = getLayoutInflater().inflate(R.layout.layout_appointmentlist, null);
                                TextView dateTextView = appointmentCard.findViewById(R.id.datelist);
                                TextView timeTextView = appointmentCard.findViewById(R.id.timelist);
                                TextView nameTextView = appointmentCard.findViewById(R.id.name);
                                dateTextView.setText(appointmentDate);
                                timeTextView.setText(appointmentTime+"PM");
                                nameTextView.setText(name);
                                appointmentsLayout.addView(appointmentCard);
                                appointmentCard.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        showCancelDialog(Homepage.this,appointmentDate,appointmentTime);
                                        return true;
                                    }
                                });
                                appointmentCard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent detailsIntent = new Intent(Homepage.this, AppointmentDetails.class);
                                        Bundle b = new Bundle();
                                        b.putString("name", name);
                                        b.putString("phone", phone);
                                        b.putString("age", age);
                                        b.putString("time", appointmentTime);
                                        b.putString("address", address);
                                        b.putString("date", appointmentDate);
                                        b.putString("documentId", documentId);
                                        b.putString("p_id", p_id);
                                        b.putString("gender", gender);
                                        b.putString("email", email);
                                        detailsIntent.putExtras(b);
                                        startActivity(detailsIntent);

                                    }
                                });

                            }
                        } else {
                             empty.setText("No Upcoming Appointments");
                        }
                    }
                });
    }


    private void showCancelDialog(final Context context, final String date, final String time) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_appointment);

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
                                                        Toast.makeText(Homepage.this, "Appointment Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(Homepage.this, Homepage.class);
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

                                }
                            }
                        });

            }
        });

        dialog.show();
    }
}