package com.jstech.onestop.firebase;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jstech.onestop.R;
import com.jstech.onestop.model.Mechanic;

public class AddMechanicActivity extends AppCompatActivity implements View.OnClickListener{

    EditText eTxtMechanicName;
    EditText eTxtMechanicExperience;
    EditText eTxtMechanicEmail;
    EditText eTxtMechanicPhone;
    EditText eTxtMechanicAddress;
    Button btnAddMech;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    Mechanic mechanic = null;

    String mechTypeRcv;


    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mechanic);

        mechTypeRcv = getIntent().getExtras().getString("keyMechType");
        mechanic = new Mechanic();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        eTxtMechanicAddress = (EditText)findViewById(R.id.editTextAddressMech);
        eTxtMechanicName = (EditText)findViewById(R.id.editTextNameMech);
        eTxtMechanicEmail = (EditText)findViewById(R.id.editTextEmailMech);
        eTxtMechanicExperience = (EditText)findViewById(R.id.editTextExperienceMech);
        eTxtMechanicPhone = (EditText)findViewById(R.id.editTextPhoneMech);
        btnAddMech = (Button)findViewById(R.id.buttonAddMech);
        btnAddMech.setOnClickListener(this);


    }

    void addMechanic()
    {
        if (!validateEntries()) {
            return;
        }
        showProgressDialog();
        String experience = eTxtMechanicExperience.getText().toString();
        String phone = eTxtMechanicPhone.getText().toString();
        String email = eTxtMechanicEmail.getText().toString();
        String name = eTxtMechanicName.getText().toString();
        String address = eTxtMechanicAddress.getText().toString();
        String password = "nobody";
        String emailf = name.substring(0,name.indexOf(" "))+ mechTypeRcv+".mechanic@onestop.com";
        mechanic.setPhone(phone);
        mechanic.setEmail(email);
        mechanic.setName(name);
        mechanic.setAddress(address);
        mechanic.setExperience(Double.parseDouble(experience));


        mAuth.createUserWithEmailAndPassword(emailf,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                hideProgressDialog();
                if (task.isSuccessful()) {
                    onAuthSuccess(task.getResult().getUser(), mechanic);
                } else {
                    Toast.makeText(AddMechanicActivity.this, "Adding mechanic failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void onAuthSuccess(FirebaseUser fUser, Mechanic mechanic) {
        eTxtMechanicPhone.setText("");
        eTxtMechanicExperience.setText("");
        eTxtMechanicEmail.setText("");
        eTxtMechanicName.setText("");
        eTxtMechanicAddress.setText("");
        Toast.makeText(AddMechanicActivity.this, "New mechanic's entry has been added!", Toast.LENGTH_LONG).show();
        writeNewMechanic(fUser.getUid(),mechanic.getName(), mechanic.getExperience(), mechanic.getEmail(), mechanic.getPhone(), mechanic.getAddress());
    }

    private void writeNewMechanic(String userId, String mechName, Double mechExperience, String mechEmail ,String mechPhone, String mechAddress) {
        Mechanic mechanic = new Mechanic(mechName, mechExperience, mechAddress, mechPhone, mechEmail);
        mDatabase.child("Mechanics").child(mechTypeRcv).child(userId).setValue(mechanic);
    }

    private boolean validateEntries() {
        boolean result = true;
        if (TextUtils.isEmpty(eTxtMechanicName.getText().toString())) {
            eTxtMechanicName.setError("Required");
            result = false;
        } else {
            eTxtMechanicName.setError(null);
        }
        if (TextUtils.isEmpty(eTxtMechanicEmail.getText().toString())) {
            eTxtMechanicEmail.setError("Required");
            result = false;
        } else {
            eTxtMechanicEmail.setError(null);
        }

        if (TextUtils.isEmpty(eTxtMechanicExperience.getText().toString())) {
            eTxtMechanicExperience.setError("Required");
            result = false;
        } else {
            eTxtMechanicExperience.setError(null);
        }
        if (TextUtils.isEmpty(eTxtMechanicPhone.getText().toString())) {
            eTxtMechanicPhone.setError("Required");
            result = false;
        } else {
            eTxtMechanicPhone.setError(null);
        }
        if (TextUtils.isEmpty(eTxtMechanicAddress.getText().toString())) {
            eTxtMechanicAddress.setError("Required");
            result = false;
        } else {
            eTxtMechanicAddress.setError(null);
        }
        return result;
    }


    @Override
    public void onClick(View v) {
        addMechanic();
    }
}
