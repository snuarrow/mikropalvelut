/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.personservice;

import ohtu.domainlib.Error;
import ohtu.domainlib.DateTimeToken;
import ohtu.domainlib.Kryptoniter;
import ohtu.domainlib.Token;
import ohtu.domainlib.TokenConverter;
import ohtu.domainlib.JsonTransformer;
import ohtu.domainlib.UrlCollection;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import org.apache.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import static spark.Spark.*;

//new

public class PersonServiceMain {
    
    
    
    public static void main(String[] args) throws IOException {
        
        port(4567); // env variable here!
        Gson gson = new Gson();
        
        // encapsulate somehow, makes only UrlCollection
        String configurationsUrl = System.getenv("CONF_API");
        System.out.println("conf api: "+configurationsUrl);
        HttpResponse hrConf = Request.Get(configurationsUrl).execute().returnResponse();
        String responseAsJson = IOUtils.toString(hrConf.getEntity().getContent() , Charset.forName("UTF-8"));
        UrlCollection urlCollection = gson.fromJson(responseAsJson, UrlCollection.class);
        
        // encapsulate, makes only datastore
        MongoClientURI uri = new MongoClientURI(urlCollection.mongourl());
        Morphia morphia = new Morphia();
        MongoClient mongo = new MongoClient(uri);
        morphia.mapPackage("ohtu.domainlib");
        String mongoKey = System.getenv("MONGO_KEY");
        Datastore datastore = morphia.createDatastore(mongo, mongoKey);

        // jos käytät lokaalia mongoa, luo client seuraavasti
        //MongoClient mongo = new MongoClient();

        get("/running", (request, response) -> {
            preFilter(request);
            return "1";
        });
        
        get("/ping", (request, response) -> {
            preFilter(request);
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");

            return "{ \"name\": \""+name+"\", \"dir\": \""+dir+"\" }";
        });

        before("/persons", (request, response) -> {
            preFilter(request);
            
            
            
            if (request.requestMethod().equals("GET"))
            {
                DateTimeToken authtoken = new TokenConverter()
                    .toDateTime(new Kryptoniter(System.getenv("OHTU_KRYPTO"))
                    .decryptedToken(new Token(request.headers("Authorization"))));
                
                if (authtoken == null)
                    halt(401, gson.toJson(Error.withCause("invalid token")));
                
                if (authtoken.isExpired())
                    halt(401, gson.toJson(Error.withCause("expired token")));
            }
        });
        
        get("/persons", (request, response) -> {
            preFilter(request);
            return datastore.find(ohtu.domainlib.Person.class).asList();
        }, new JsonTransformer());

        post("/persons", (request, response) -> {
            preFilter(request);
            ohtu.domainlib.Person person = gson.fromJson(request.body(), ohtu.domainlib.Person.class);

            if ( person == null || !person.valid()) {
                halt(400, gson.toJson(Error.withCause("all fields must have a value")));
            }

            if ( datastore.createQuery(ohtu.domainlib.Person.class).field("username").equal(person.username()).get() != null ){
                halt(400, gson.toJson(Error.withCause("username must be unique")));
            }

            datastore.save(person);
            return person;
        }, new JsonTransformer());

        post("/session", (request, response) -> {
            preFilter(request);
            ohtu.domainlib.Person dataInRequest = gson.fromJson(request.body(), ohtu.domainlib.Person.class);

            ohtu.domainlib.Person person = datastore.createQuery(ohtu.domainlib.Person.class).field("username").equal(dataInRequest.username()).get();

            if ( person==null || !person.password().equals(dataInRequest.password()) ) {
                halt(401, gson.toJson(Error.withCause( "invalid credentials")));
            }

            return new Kryptoniter(System.getenv("OHTU_KRYPTO")).encryptedToken(Token.generate());
        }, new JsonTransformer());

        after((request, response) -> {
            response.type("application/json");
        });
    }
    
    public static void preFilter(spark.Request request)
    {
        System.out.println("----------------------------------------------");
        System.out.println(request.requestMethod());
        for (String line : request.headers())
        {
            System.out.println(line+" "+request.headers(line));
        }
        System.out.println(request.body());
    }
}
