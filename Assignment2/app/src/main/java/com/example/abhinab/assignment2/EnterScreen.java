package com.example.abhinab.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EnterScreen extends Activity {
    EditText lblName,lblId,lblAge;
    RadioGroup rg;
    boolean name = false,age = false,id = false,sex = false;
    String selectedRadioButtonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_screen);

        lblName = findViewById(R.id.patientName);
        lblId = findViewById(R.id.patientId);
        lblAge = findViewById(R.id.patientAge);
        rg = findViewById(R.id.radioGSex);
        final Button btnEnter = (Button) findViewById(R.id.btnEnter);




        lblId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Toast.makeText(getApplicationContext(),"Required Field",Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                btnEnter.setEnabled((!lblId.getText().toString().trim().isEmpty()) && (!lblName.getText().toString().trim().isEmpty())&& (!lblAge.getText().toString().trim().isEmpty()));

            }

            @Override
            public void afterTextChanged(Editable s) {
            id = true;

            }
        });


        lblName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(getApplicationContext(),"Required Field",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                btnEnter.setEnabled((!lblId.getText().toString().trim().isEmpty()) && (!lblName.getText().toString().trim().isEmpty())&& (!lblAge.getText().toString().trim().isEmpty()));

            }

            @Override
            public void afterTextChanged(Editable s) {
            name = true;

            }
        });



        lblAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(getApplicationContext(),"Required Field",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                btnEnter.setEnabled((!lblId.getText().toString().trim().isEmpty()) && (!lblName.getText().toString().trim().isEmpty())&& (!lblAge.getText().toString().trim().isEmpty()));

            }

            @Override
            public void afterTextChanged(Editable s) {
                age = true;

            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedRadioButtonID = rg.getCheckedRadioButtonId();

                if (selectedRadioButtonID != -1) {

                    RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadioButtonID);
                    selectedRadioButtonText = selectedRadioButton.getText().toString();
//                    Toast.makeText(getApplicationContext(),selectedRadioButtonText,Toast.LENGTH_SHORT).show();
                    onSendInfo();



                }
                else{

                    Toast.makeText(getApplicationContext(),"Please select your sex",Toast.LENGTH_SHORT).show();

                }







            }
        });




    }
    public void onSendInfo() {

        Intent passInfoIntent = new Intent(this,MainActivity.class);
        final int result = 1;

        passInfoIntent.putExtra("Id",Integer.parseInt(lblId.getText().toString()));
        passInfoIntent.putExtra("Name",lblName.getText().toString());
        passInfoIntent.putExtra("Age",Integer.parseInt(lblAge.getText().toString()));
        passInfoIntent.putExtra("Sex",selectedRadioButtonText);

        startActivity(passInfoIntent);

    }


}
