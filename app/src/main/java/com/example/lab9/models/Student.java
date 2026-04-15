package com.example.lab9.models;

public class Student {
    private int    id;
    private String nom;
    private String prenom;
    private String ville;
    private String sexe;

    public Student() {}

    public Student(String nom, String prenom, String ville, String sexe) {
        this.nom    = nom;
        this.prenom = prenom;
        this.ville  = ville;
        this.sexe   = sexe;
    }

    // Getters
    public int    getId()     { return id;     }
    public String getNom()    { return nom;    }
    public String getPrenom() { return prenom; }
    public String getVille()  { return ville;  }
    public String getSexe()   { return sexe;   }

    @Override
    public String toString() {
        return prenom + " " + nom + " — " + ville;
    }
}