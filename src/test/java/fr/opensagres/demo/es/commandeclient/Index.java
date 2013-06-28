package fr.opensagres.demo.es.commandeclient;
import java.math.BigDecimal;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import fr.opensagres.demo.es.commandeclient.CommandeClient;
import fr.opensagres.demo.es.commandeclient.builder.CommandeClientBuilder;

public class  Index  
{

    public static void main( String[] args ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Client client = new TransportClient().addTransportAddress( new InetSocketTransportAddress( "localhost", 9300 ) );
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for ( int i = 0; i < 100000; i++ )
        {
            
            
        
        String json = mapper.writeValueAsString( createPrelevementHorsSp(i) );
     // either use client#prepare, or use Requests# to directly build index/delete requests
     bulkRequest.add(client.prepareIndex("demo", "jdbc", String.valueOf( i ))
             .setSource(json)
             );
        }
     
     BulkResponse bulkResponse = bulkRequest.execute().actionGet();
     if (bulkResponse.hasFailures()) {
         System.err.println(bulkResponse);
         // process failures by iterating through each bulk response item
     }
        client.close();
        
    }
    
    public static void main3( String[] args ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString( createPrelevementHorsSp(1) );
        System.out.println(json);
    }
    
    private static CommandeClient createPrelevementHorsSp( int sufix )
    {
        CommandeClient commandeClient = new CommandeClientBuilder().withType( "COMMANDE" ).withNumeroCommande( "123"+sufix )
                        //client
                        .withClient().withNom( "Nom" ).withNumeroClient( "12345" ).withPrenom( "prenom" ).endClient()
                        //ligne 1.
                        .withAddedLignesCommandeElement().withCodeArticle( "12345"+sufix ).withLibelleArticle( "Test Artcile "+sufix )
                            .withNumeroLigne( Integer.valueOf(sufix)).withPrixUnitaire( BigDecimal.valueOf( 10.5+sufix ) ).withQuantite( BigDecimal.valueOf( 1.0) ).endLignesCommandeElement()
                        //ligne 2
                       .withAddedLignesCommandeElement().withCodeArticle( "88888"+sufix ).withLibelleArticle( "Other Artcile "+sufix )
                            .withNumeroLigne( Integer.valueOf(sufix)).withPrixUnitaire( BigDecimal.valueOf( 5.5+sufix ) ).withQuantite( BigDecimal.valueOf( 7.0) ).endLignesCommandeElement()
    
                         .build();

        return commandeClient;
    }
}
