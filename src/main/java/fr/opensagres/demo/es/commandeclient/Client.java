package fr.opensagres.demo.es.commandeclient;

public class Client
{
    private String numeroClient;

    public String getNumeroClient()
    {
        return numeroClient;
    }

    public void setNumeroClient( String numeroClient )
    {
        this.numeroClient = numeroClient;
    }

    public String getPrenom()
    {
        return prenom;
    }

    public void setPrenom( String prenom )
    {
        this.prenom = prenom;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom( String nom )
    {
        this.nom = nom;
    }

    private String prenom;

    private String nom;

}
