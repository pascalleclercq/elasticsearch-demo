package fr.opensagres.demo.es.commandeclient;

import java.util.ArrayList;
import java.util.List;



public class CommandeClient {
  
    private String numeroCommande;
    private String type;
    private Client client;
    private List<LigneCommande> lignesCommande = new ArrayList<LigneCommande>();
    public boolean addLigneCommande ( LigneCommande ligneCommande )
    {
        return lignesCommande.add( ligneCommande );
    }
    public boolean remove( LigneCommande ligneCommande )
    {
        return lignesCommande.remove( ligneCommande );
    }
    public CommandeClient()
    {}
    public String getNumeroCommande()
    {
        return numeroCommande;
    }
    public void setNumeroCommande( String numeroCommande )
    {
        this.numeroCommande = numeroCommande;
    }
    public String getType()
    {
        return type;
    }
    public void setType( String type )
    {
        this.type = type;
    }
    public Client getClient()
    {
        return client;
    }
    public void setClient( Client client )
    {
        this.client = client;
    }
    public List<LigneCommande> getLignesCommande()
    {
        return lignesCommande;
    }
    public void setLignesCommande( List<LigneCommande> lignesCommande )
    {
        this.lignesCommande = lignesCommande;
    }
    
    
    
}