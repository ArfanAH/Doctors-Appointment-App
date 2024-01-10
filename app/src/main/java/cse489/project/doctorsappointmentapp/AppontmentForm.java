package cse489.project.doctorsappointmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppontmentForm extends AppCompatActivity {

    EditText name,phone,age,address;
    ImageView home,appointmentBtn,history;
    TextView date;
    RadioGroup gender;
    RadioButton selectedRadioButton;
    Button next;
    Spinner time;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    int selectedRadioButtonId;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appontment_form);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);
        time = findViewById(R.id.time);
        home=findViewById(R.id.home);
        appointmentBtn=findViewById(R.id.appointmentBtn);
        history=findViewById(R.id.history);
        gender=findViewById(R.id.gender);
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        address = findViewById(R.id.address);
        date = findViewById(R.id.date);
        next = findViewById(R.id.next);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        String[] timeSlots = {"Select Time (PM)","5:00-5:20","5:25-5:55", "6:00-6:20", "6:25-6:55", "7:00-7:20", "7:25-7:55", "8:00-8:20", "8:25-8:55", "9:00-9:20"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time.setAdapter(adapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields()) {
                    String Name = name.getText().toString();
                    String Phone = phone.getText().toString();
                    String Age = age.getText().toString();
                    String Time = time.getSelectedItem().toString();
                    String Address = address.getText().toString();
                    String Date = date.getText().toString();
                    selectedRadioButtonId = gender.getCheckedRadioButtonId();
                    selectedRadioButton = findViewById(selectedRadioButtonId);
                    String Gender = selectedRadioButton.getText().toString();
                    checkAppointmentExistence(Time, Date, Name, Phone, Age, Address, Gender);
                }
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppontmentForm.this, Homepage.class);
                startActivity(i);
                finish();

            }
        });
        appointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppontmentForm.this, AppontmentForm.class);
                startActivity(i);

            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AppontmentForm.this, History.class);
                startActivity(i);

            }
        });
    }
    private void showDatePickerDialog() {
        Date currentDate = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        String currentTimeString = sdfTime.format(currentDate);
        String currentDateString = sdfDate.format(currentDate);

        boolean isTimeInRange = isTimeBetween("15:00", "23:59", currentTimeString);

        Calendar calendar = Calendar.getInstance();
        try {
            Date date = sdfDate.parse(currentDateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    date.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        if (isTimeInRange) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean isTimeBetween(String startTime, String endTime, String currentTime) {
        try {
            Date time1 = new SimpleDateFormat("HH:mm").parse(startTime);
            Date time2 = new SimpleDateFormat("HH:mm").parse(endTime);
            Date currTime = new SimpleDateFormat("HH:mm").parse(currentTime);

            return currTime.after(time1) && currTime.before(time2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }



    private boolean validateFields() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Name cannot be empty");
            return false;
        }
        if (phone.getText().toString().isEmpty()) {
            phone.setError("Phone number cannot be empty");
            return false;
        }
        if (age.getText().toString().isEmpty()) {
            age.setError("Age cannot be empty");
            return false;
        }
        String selectedTime = time.getSelectedItem().toString();
        if (selectedTime.equals("Select Time (PM)")) {
            Toast.makeText(AppontmentForm.this, "Choose a valid slot", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(AppontmentForm.this, "Time cannot be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.getText().toString().isEmpty()) {
            address.setError("Address cannot be empty");
            return false;
        }
        if(date.getText().toString().isEmpty()){
            date.setError("date cannot be empty");
            return false;
        }

        return true;
    }
    private void checkAppointmentExistence(String time, String date, String name, String phone, String age, String address, String gender) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("appointments")
                    .whereEqualTo("time", time)
                    .whereEqualTo("date", date)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    Toast.makeText(AppontmentForm.this, "Please choose another time slot.", Toast.LENGTH_SHORT).show();
                                    System.out.println("Ok");
                                }
                                else {
                                    db.collection("appointments")
                                            .whereEqualTo("p_id", userId)
                                            .whereEqualTo("date", date)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (!task.getResult().isEmpty()) {
                                                            Toast.makeText(AppontmentForm.this, "You already have an appointment on this day.", Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            Intent previewIntent = new Intent(AppontmentForm.this, PreviewForm.class);
                                                            Bundle b = new Bundle();
                                                            b.putString("name", name);
                                                            b.putString("phone", phone);
                                                            b.putString("age", age);
                                                            b.putString("time", time);
                                                            b.putString("address", address);
                                                            b.putString("gender", gender);
                                                            b.putString("date", date);
                                                            previewIntent.putExtras(b);
                                                            startActivity(previewIntent);
                                                        }
                                                    } else {
                                                        System.out.println("Error");
                                                    }
                                                }
                                            });
                                }
                            } else {
                                System.out.println("Error");
                            }
                        }
                    });
        } else {

            System.out.println("Error");
        }
    }

}


