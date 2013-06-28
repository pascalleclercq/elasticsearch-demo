package fr.opensagres.demo.es.commandeclient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.opensagres.demo.es.commandeclient.builder.CommandeClientBuilder;

public class BulkUpdateAndSearch
{
    private static final int NB_ITEMS = 10000;

    @Test
    public void bulkIndexNbItems()
        throws Exception
    {
        long start = System.currentTimeMillis();
        performBulkIndex();
        System.out.println("Time spent to index "+NB_ITEMS+" items "+(System.currentTimeMillis()-start)+" ms");
        start = System.currentTimeMillis();
        List<CommandeClient> result=performSimpleSearch();
        System.out.println("Time spent to search and deserialize "+result.size()+" items "+(System.currentTimeMillis()-start)+" ms");
    }

    public List<CommandeClient> performSimpleSearch()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        List<CommandeClient> result = new ArrayList<CommandeClient>();
        Client client = createNewClient();
        SearchRequestBuilder searchRequest =
            client.prepareSearch( "demo" ).setSize( 500 ).setQuery( QueryBuilders.queryString( "*1000*" ) );

        SearchHits hits = client.search( searchRequest.request() ).actionGet().getHits();
        

        // ObjectMapper mapper = RootObjectMapper.Defaults.
        System.out.println( hits.getTotalHits() );
        SearchHit[] hites = hits.getHits();
        
        for ( int i = 0; i < hites.length; i++ )
        {
            CommandeClient readValue = mapper.readValue( hites[i].getSourceAsString(), CommandeClient.class );
            System.out.println( readValue.getNumeroCommande() );
            result.add( readValue );
        }
        // on shutdown
        client.close();
        return result;

    }

    public void performBulkIndex()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Client client = createNewClient();
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for ( int i = 0; i < NB_ITEMS; i++ )
        {
            String json = mapper.writeValueAsString( createPrelevementHorsSp( i ) );
            // either use client#prepare, or use Requests# to directly build index/delete requests
            bulkRequest.add( client.prepareIndex( "demo", "jdbc", String.valueOf( i ) ).setSource( json ) );
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if ( bulkResponse.hasFailures() )
        {
            System.err.println( bulkResponse );
            // process failures by iterating through each bulk response item
        }
        client.close();
    }

    public Client createNewClient()
    {
        Client client = new TransportClient().addTransportAddress( new InetSocketTransportAddress( "localhost", 9300 ) );
        return client;
    }

    @Ignore( "invoke when necessary..." )
    @Test
    public void jsonSerialization()
        throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString( createPrelevementHorsSp( 1 ) );
        String expected =
            "{\"type\":\"COMMANDE\",\"numeroCommande\":\"1231\",\"client\":{\"numeroClient\":\"12345\",\"prenom\":\"prenom\",\"nom\":\"Nom\"},\"lignesCommande\":[{\"numeroLigne\":1,\"commande\":null,\"codeArticle\":\"123451\",\"libelleArticle\":\"Test Artcile 1\",\"quantite\":1.0,\"prixUnitaire\":11.5},{\"numeroLigne\":1,\"commande\":null,\"codeArticle\":\"888881\",\"libelleArticle\":\"Other Artcile 1\",\"quantite\":7.0,\"prixUnitaire\":6.5}]}";
        Assert.assertEquals( expected, json );
    }

    private static CommandeClient createPrelevementHorsSp( int sufix )
    {
        //@formatter:off
        CommandeClient commandeClient =
            new CommandeClientBuilder().withType( "COMMANDE" ).withNumeroCommande( "123" + sufix )
            // client
            .withClient().withNom( "Nom" ).withNumeroClient( "12345" ).withPrenom( "prenom" ).endClient()
            // ligne 1.
                .withAddedLignesCommandeElement().withCodeArticle( "12345" + sufix ).withLibelleArticle( "Test Artcile "+ sufix )
                .withNumeroLigne( Integer.valueOf( sufix ) ).withPrixUnitaire( BigDecimal.valueOf( 10.5 + sufix ) )
                .withQuantite( BigDecimal.valueOf( 1.0 ) ).endLignesCommandeElement()
            // ligne 2
                .withAddedLignesCommandeElement().withCodeArticle( "88888" + sufix ).withLibelleArticle( "Other Artcile " + sufix )
                .withNumeroLigne( Integer.valueOf( sufix ) ).withPrixUnitaire( BigDecimal.valueOf( 5.5 + sufix ) )
                .withQuantite( BigDecimal.valueOf( 7.0 ) ).endLignesCommandeElement()

            .build();

        return commandeClient;
    }
}
