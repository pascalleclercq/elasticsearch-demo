package fr.opensagres.demo.es.commandeclient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.opensagres.demo.es.commandeclient.builder.CommandeClientBuilder;

public class BulkUpdateAndSearchTest
{
    private static final int NB_MAX_TO_FETCH = 500;
    private static final String DEMOINDEX = "demoindex";
    private static final int NB_ITEMS = 10000;
    ObjectMapper mapper = new ObjectMapper();

    private static CommandeClient createCommandeClient( int sufix )
    {
        //@formatter:off
        CommandeClient commandeClient =
            new CommandeClientBuilder()
            //en-tête
            .withType( "COMMANDE" ).withNumeroCommande( "123" + sufix )
            .withDateCreation( DateTime.now().minusHours( sufix ).toDate() )
            .withDateModification( DateTime.now().plusHours( sufix ).toDate() )
            // client
            .withClient().withNom( "Nom" ).withNumeroClient( "12345" ).withPrenom( "prenom" ).endClient()
            // ligne 1.
                .withAddedLignesCommandeElement().withCodeArticle( "12345" + sufix ).withLibelleArticle( "Test Article "+ sufix )
                .withNumeroLigne( Integer.valueOf( sufix ) ).withPrixUnitaire( BigDecimal.valueOf( 10.5 + sufix ) )
                .withQuantite( BigDecimal.valueOf( 1.0 ) ).endLignesCommandeElement()
            // ligne 2
                .withAddedLignesCommandeElement().withCodeArticle( "88888" + sufix ).withLibelleArticle( "Other Article " + sufix )
                .withNumeroLigne( Integer.valueOf( sufix ) ).withPrixUnitaire( BigDecimal.valueOf( 5.5 + sufix ) )
                .withQuantite( BigDecimal.valueOf( 7.0 ) ).endLignesCommandeElement()

            .build();

        return commandeClient;
    }


    
    private static Client createNewClient()
    {
       //remote client : requires elasticsearch to run in the background... 
       // Client client = new TransportClient().addTransportAddress( new InetSocketTransportAddress( "localhost", 9300 ) );
        Node  node = NodeBuilder.nodeBuilder().node();
        Client client = node.client();
        return client;
    }

    @BeforeClass
    public static  void performBulkIndex()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Client client = createNewClient();
        long start = System.currentTimeMillis();
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for ( int i = 0; i < NB_ITEMS; i++ )
        {
            CommandeClient commandeClient = createCommandeClient( i );
            String json = mapper.writeValueAsString( commandeClient  );
            // either use client#prepare, or use Requests# to directly build index/delete requests
            bulkRequest.add( client.prepareIndex( DEMOINDEX, "customerOrder", commandeClient.getNumeroCommande() ).setSource( json ) );
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if ( bulkResponse.hasFailures() )
        {
            System.err.println( bulkResponse );
            // process failures by iterating through each bulk response item
        }
        System.out.println("Time spent to index "+NB_ITEMS+" items "+(System.currentTimeMillis()-start)+" ms");
        client.close();
    }

    @Test
    public void queryString()
        throws Exception
    {
        Client client = createNewClient();
        long start = System.currentTimeMillis();
        SearchRequestBuilder searchRequest =
            client.prepareSearch( DEMOINDEX ).setSize( NB_MAX_TO_FETCH )
            .setQuery( QueryBuilders.queryString( "*12345123*" ) );
        List<CommandeClient> result = searchAndDeserialize( client, searchRequest );
        System.out.println("Time spent to search and deserialize "+result.size()+" items "+(System.currentTimeMillis()-start)+" ms");
        Assert.assertEquals( 11, result.size() );
        // on shutdown
        client.close();
    }
    
    @Test
    public void searchById()
        throws Exception
    {
        Client client = createNewClient();
        long start = System.currentTimeMillis();
        SearchRequestBuilder searchRequest =
            client.prepareSearch( DEMOINDEX ).setSize( NB_MAX_TO_FETCH )
            .setQuery( QueryBuilders.idsQuery().ids("12310") );
        
        List<CommandeClient> result = searchAndDeserialize( client, searchRequest );
        System.out.println("Time spent to search and deserialize "+result.size()+" items "+(System.currentTimeMillis()-start)+" ms");
        Assert.assertEquals( 1, result.size() );
        // on shutdown
        client.close();
        
    }
    
    @Test
    public void rangeQuery()
        throws Exception
    {
        Client client = createNewClient();
        long start = System.currentTimeMillis();
        //search
        Date from = DateTime.now().minusDays( 10 ).toDate();
        Date to = DateTime.now().plusDays( 10 ).toDate();
        SearchRequestBuilder searchRequest =
            client.prepareSearch( DEMOINDEX ).setSize( NB_MAX_TO_FETCH )
            .setQuery( QueryBuilders.rangeQuery( "dateCreation" ).from( from.getTime() ).to(to.getTime()) );
        List<CommandeClient> result = searchAndDeserialize( client, searchRequest );
        System.out.println("Time spent to search and deserialize "+result.size()+" items "+(System.currentTimeMillis()-start)+" ms");
        Assert.assertEquals( 240, result.size() );
        // on shutdown
        client.close();
        
    }



    public List<CommandeClient> searchAndDeserialize( Client client, SearchRequestBuilder searchRequest )
        throws IOException, JsonParseException, JsonMappingException
    {
        SearchHits hits = client.search( searchRequest.request() ).actionGet().getHits();
        SearchHit[] hites = hits.getHits();
        List<CommandeClient> result = new ArrayList<CommandeClient>(hites.length);
        for ( int i = 0; i < hites.length; i++ )
        {
            CommandeClient readValue = mapper.readValue( hites[i].getSourceAsString(), CommandeClient.class );
            System.out.println( readValue.getNumeroCommande() );
            result.add( readValue );
        }
        return result;
    }
}
