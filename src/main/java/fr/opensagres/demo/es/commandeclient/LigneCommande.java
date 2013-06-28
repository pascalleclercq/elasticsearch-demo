package fr.opensagres.demo.es.commandeclient;

import java.math.BigDecimal;

public class LigneCommande
{
    private Integer numeroLigne;
    private CommandeClient commande;
    private String codeArticle;
    private String libelleArticle;
    private BigDecimal quantite;
    private BigDecimal prixUnitaire;

    public Integer getNumeroLigne()
    {
        return numeroLigne;
    }
    public void setNumeroLigne( Integer numeroLigne )
    {
        this.numeroLigne = numeroLigne;
    }
    public CommandeClient getCommande()
    {
        return commande;
    }
    public void setCommande( CommandeClient commande )
    {
        this.commande = commande;
    }
    public String getCodeArticle()
    {
        return codeArticle;
    }
    public void setCodeArticle( String codeArticle )
    {
        this.codeArticle = codeArticle;
    }
    public String getLibelleArticle()
    {
        return libelleArticle;
    }
    public void setLibelleArticle( String libelleArticle )
    {
        this.libelleArticle = libelleArticle;
    }
    public BigDecimal getQuantite()
    {
        return quantite;
    }
    public void setQuantite( BigDecimal quantite )
    {
        this.quantite = quantite;
    }
    public BigDecimal getPrixUnitaire()
    {
        return prixUnitaire;
    }
    public void setPrixUnitaire( BigDecimal prixUnitaire )
    {
        this.prixUnitaire = prixUnitaire;
    }
    
    
 
    
}
