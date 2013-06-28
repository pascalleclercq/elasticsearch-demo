package fr.opensagres.demo.es.commandeclient;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import fr.opensagres.demo.es.commandeclient.CommandeClient;

public class ESClient
{

    public static void main( String[] args )
        throws Exception
    {
        
        long start = System.currentTimeMillis();
        Client client = new TransportClient().addTransportAddress( new InetSocketTransportAddress( "localhost", 9300 ) );
        //client.prepareSearch( "jdbc" ).setSize( Integer.MAX_VALUE );
        SearchRequestBuilder searchRequest = client.prepareSearch( "demo" ).setSize(500).setQuery( QueryBuilders.queryString( "*10*" ) );

        SearchHits hits = client.search( searchRequest.request() ).actionGet().getHits();
        ObjectMapper mapper = new ObjectMapper();
        
        //ObjectMapper mapper =  RootObjectMapper.Defaults.
        System.out.println( hits.getTotalHits() );
        SearchHit[] hites= hits.getHits();
        List<CommandeClient> result = new ArrayList<CommandeClient>();
        for ( int i = 0; i < hites.length; i++ )
        {
            CommandeClient readValue = mapper.readValue( hites[i].getSourceAsString(), CommandeClient.class );
            System.out.println(readValue);
            result.add(  readValue);
        }
        // on shutdown
        System.out.println(result.size());
        client.close();
        System.out.println("Tps "+(System.currentTimeMillis()-start));
    }
}
