package com.example.lab9.models;

/**
 * Classe modèle représentant un étudiant dans l'application.
 * Les attributs correspondent aux colonnes de la base de données distante.
 */
public class Student {
    // Identifiant unique de l'étudiant
    private int    id;
    // Informations personnelles
    private String nom;
    private String prenom;
    // Localisation
    private String ville;
    // Genre de l'étudiant
    private String sexe;

    /**
     * Constructeur par défaut nécessaire pour certaines bibliothèques comme Gson.
     */
    public Student() {}

    /**
     * Constructeur complet pour l'initialisation manuelle.
     */
    public Student(String nom, String prenom, String ville, String sexe) {
        this.nom    = nom;
        this.prenom = prenom;
        this.ville  = ville;
        this.sexe   = sexe;
    }

    // --- Getters & Accesseurs ---

    public int    getId()     { return id;     }
    public String getNom()    { return nom;    }
    public String getPrenom() { return prenom; }
    public String getVille()  { return ville;  }
    public String getSexe()   { return sexe;   }

    /**
     * Représentation textuelle de l'étudiant (utilisée dans les logs).
     */
    @Override
    public String toString() {
        return prenom + " " + nom + " — " + ville;
    }
}