package com.example.lab9;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lab9.models.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG        = "StudentManager";
    private static final String ADD_URL    = "http://10.0.2.2/studentapi/api/addStudent.php";

    private EditText    etNom, etPrenom;
    private Spinner     spVille;
    private RadioButton rbHomme, rbFemme;
    private Button      btnAdd;
    private TextView    tvStatus;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNom   = findViewById(R.id.etNom);
        etPrenom= findViewById(R.id.etPrenom);
        spVille = findViewById(R.id.spVille);
        rbHomme = findViewById(R.id.rbHomme);
        rbFemme = findViewById(R.id.rbFemme);
        btnAdd  = findViewById(R.id.btnAdd);
        tvStatus= findViewById(R.id.tvStatus);

        queue = Volley.newRequestQueue(this);

        btnAdd.setOnClickListener(v -> {
            if (validateForm()) {
                sendStudentToServer();
            }
        });
    }

    private boolean validateForm() {
        if (etNom.getText().toString().trim().isEmpty()) {
            tvStatus.setText("Le nom est obligatoire");
            return false;
        }
        if (etPrenom.getText().toString().trim().isEmpty()) {
            tvStatus.setText("Le prénom est obligatoire");
            return false;
        }
        if (!rbHomme.isChecked() && !rbFemme.isChecked()) {
            tvStatus.setText("Veuillez sélectionner le sexe");
            return false;
        }
        tvStatus.setText("");
        return true;
    }

    private void sendStudentToServer() {
        btnAdd.setEnabled(false);
        tvStatus.setText("Envoi en cours...");

        StringRequest request = new StringRequest(
            Request.Method.POST,
            ADD_URL,
            response -> {
                Log.d(TAG, "Réponse : " + response);
                btnAdd.setEnabled(true);
                parseAndDisplay(response);
            },
            error -> {
                Log.e(TAG, "Erreur réseau : " + error.getMessage());
                btnAdd.setEnabled(true);
                tvStatus.setText("Erreur réseau. Vérifier XAMPP.");
            }
        ) {
            @Override
            protected Map<String, String> getParams() {
                String sexe = rbHomme.isChecked() ? "homme" : "femme";
                Map<String, String> params = new HashMap<>();
                params.put("nom",    etNom.getText().toString().trim());
                params.put("prenom", etPrenom.getText().toString().trim());
                params.put("ville",  spVille.getSelectedItem().toString());
                params.put("sexe",   sexe);
                return params;
            }
        };

        queue.add(request);
    }

    private void parseAndDisplay(String jsonResponse) {
        try {
            Gson     gson       = new Gson();
            // Notre API renvoie un objet avec "etudiants" dedans
            JSONObject obj      = new JSONObject(jsonResponse);
            String listJson     = obj.getJSONArray("etudiants").toString();
            Type   listType     = new TypeToken<List<Student>>(){}.getType();
            List<Student> liste = gson.fromJson(listJson, listType);

            tvStatus.setText("✔ Ajouté ! Total : " + liste.size() + " étudiant(s)");
            for (Student s : liste) {
                Log.d(TAG, s.toString());
            }

            // Vider le formulaire
            etNom.setText("");
            etPrenom.setText("");
            rbHomme.setChecked(false);
            rbFemme.setChecked(false);

        } catch (Exception e) {
            Log.e(TAG, "Erreur parsing : " + e.getMessage());
            tvStatus.setText("Réponse reçue mais parsing échoué");
        }
    }
}