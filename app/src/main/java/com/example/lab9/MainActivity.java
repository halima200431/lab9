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

/**
 * Activité principale gérant l'inscription des étudiants et la communication avec le serveur.
 * Cette classe utilise Volley pour les requêtes HTTP et Gson pour le parsing JSON.
 */
public class MainActivity extends AppCompatActivity {

    // Constantes pour le débogage et l'URL de l'API
    private static final String TAG        = "StudentManager";
    // Adresse IP de l'hôte vue depuis l'émulateur Android (10.0.2.2)
    private static final String ADD_URL    = "http://10.0.2.2/studentapi/api/addStudent.php";

    // Éléments de l'interface graphique
    private EditText    etNom, etPrenom;
    private Spinner     spVille;
    private RadioButton rbHomme, rbFemme;
    private Button      btnAdd;
    private TextView    tvStatus;
    
    // File d'attente pour les requêtes réseau Volley
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Liaison avec le layout XML
        setContentView(R.layout.activity_main);

        // Initialisation des composants UI par leurs IDs
        etNom   = findViewById(R.id.etNom);
        etPrenom= findViewById(R.id.etPrenom);
        spVille = findViewById(R.id.spVille);
        rbHomme = findViewById(R.id.rbHomme);
        rbFemme = findViewById(R.id.rbFemme);
        btnAdd  = findViewById(R.id.btnAdd);
        tvStatus= findViewById(R.id.tvStatus);

        // Création de la file de requêtes réseau
        queue = Volley.newRequestQueue(this);

        // Configuration de l'action du bouton d'enregistrement
        btnAdd.setOnClickListener(v -> {
            // On vérifie d'abord que les données saisies sont valides
            if (validateForm()) {
                sendStudentToServer();
            }
        });
    }

    /**
     * Vérifie la validité des champs du formulaire.
     * @return true si tout est OK, false sinon.
     */
    private boolean validateForm() {
        if (etNom.getText().toString().trim().isEmpty()) {
            tvStatus.setText("Le nom est obligatoire");
            return false;
        }
        if (etPrenom.getText().toString().trim().isEmpty()) {
            tvStatus.setText("Le prénom est obligatoire");
            return false;
        }
        // Vérification qu'un bouton radio est bien sélectionné
        if (!rbHomme.isChecked() && !rbFemme.isChecked()) {
            tvStatus.setText("Veuillez sélectionner le sexe");
            return false;
        }
        tvStatus.setText(""); // Effacement des messages d'erreur précédents
        return true;
    }

    /**
     * Envoie les données de l'étudiant au serveur distant via une requête POST.
     */
    private void sendStudentToServer() {
        // Désactivation temporaire du bouton et message d'attente
        btnAdd.setEnabled(false);
        tvStatus.setText("Envoi en cours...");

        // Création de la requête StringRequest (POST)
        StringRequest request = new StringRequest(
            Request.Method.POST,
            ADD_URL,
            response -> {
                // Succès : journalisation et traitement de la réponse
                Log.d(TAG, "Réponse : " + response);
                btnAdd.setEnabled(true);
                parseAndDisplay(response);
            },
            error -> {
                // Échec : affichage de l'erreur réseau
                Log.e(TAG, "Erreur réseau : " + error.getMessage());
                btnAdd.setEnabled(true);
                tvStatus.setText("Erreur réseau. Vérifier XAMPP.");
            }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Préparation des paramètres POST à envoyer au script PHP
                String sexe = rbHomme.isChecked() ? "homme" : "femme";
                Map<String, String> params = new HashMap<>();
                params.put("nom",    etNom.getText().toString().trim());
                params.put("prenom", etPrenom.getText().toString().trim());
                params.put("ville",  spVille.getSelectedItem().toString());
                params.put("sexe",   sexe);
                return params;
            }
        };

        // Ajout de la requête à la file pour exécution immédiate
        queue.add(request);
    }

    /**
     * Analyse la réponse JSON reçue du serveur et met à jour l'interface.
     * @param jsonResponse Chaîne de caractères au format JSON.
     */
    private void parseAndDisplay(String jsonResponse) {
        try {
            Gson gson = new Gson();
            // L'API PHP renvoie un objet contenant une clé "etudiants"
            JSONObject obj      = new JSONObject(jsonResponse);
            String listJson     = obj.getJSONArray("etudiants").toString();
            
            // Définition du type générique pour la liste d'étudiants
            Type listType = new TypeToken<List<Student>>(){}.getType();
            List<Student> liste = gson.fromJson(listJson, listType);

            // Notification de réussite et affichage du compteur
            tvStatus.setText("✔ Ajouté ! Total : " + liste.size() + " étudiant(s)");
            
            // Affichage dans les logs pour vérification
            for (Student s : liste) {
                Log.d(TAG, s.toString());
            }

            // Remise à zéro des champs du formulaire après succès
            resetForm();

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du parsing JSON : " + e.getMessage());
            tvStatus.setText("Réponse reçue mais erreur d'interprétation");
        }
    }

    /**
     * Réinitialise les champs de saisie.
     */
    private void resetForm() {
        etNom.setText("");
        etPrenom.setText("");
        rbHomme.setChecked(false);
        rbFemme.setChecked(false);
    }
}