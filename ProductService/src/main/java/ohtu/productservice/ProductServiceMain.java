/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.productservice;

/**
 * new
 * @author hexvaara
 */
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import ohtu.domainlib.DateTimeToken;
import ohtu.domainlib.JsonTransformer;
import ohtu.domainlib.Error;
import ohtu.domainlib.Kryptoniter;
import ohtu.domainlib.Product;
import ohtu.domainlib.Token;
import ohtu.domainlib.TokenConverter;
import ohtu.domainlib.UrlCollection;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.SparkBase.port;

public class ProductServiceMain
{
    public static void main(String args[]) throws IOException
    {
        port(4568); // env variable here!
        Gson gson = new Gson();
        
        // encapsulate somehow, makes only UrlCollection
        String configurationsUrl = System.getenv("CONF_API");;
        HttpResponse hrConf = Request.Get(configurationsUrl).execute().returnResponse();
        String responseAsJson = IOUtils.toString(hrConf.getEntity().getContent() , Charset.forName("UTF-8"));
        UrlCollection urlCollection = gson.fromJson(responseAsJson, UrlCollection.class);
        
        // encapsulate, makes only datastore
        MongoClientURI uri = new MongoClientURI(urlCollection.mongourl());
        Morphia morphia = new Morphia();
        MongoClient mongo = new MongoClient(uri);
        morphia.mapPackage("ohtu.domainlib");
        Datastore datastore = morphia.createDatastore(mongo, "kanta11");
        
        // jos käytät lokaalia mongoa, luo client seuraavasti
        //MongoClient mongo = new MongoClient();
        
        get("/running", (request, response) -> {
            preFilter(request);
            return "1";
        });
        
        get("/ping", (request, response) -> {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String dir = System.getProperty("user.dir");

            return "{ \"name\": \""+name+"\", \"dir\": \""+dir+"\" }";
        });
        
        before("/products", (request, response) ->
        {
            preFilter(request);
            
            DateTimeToken decrypted_auth_token = new TokenConverter()
                    .toDateTime(new Kryptoniter(System.getenv("OHTU_KRYPTO"))
                    .decryptedToken(new Token(request.headers("Authorization"))));
            
            if (decrypted_auth_token.isExpired())
                halt(401, gson.toJson(Error.withCause("expired token")));
        });
        
        get("/products", (request, response) -> 
        {
            preFilter(request);
            
            return datastore.find(Product.class).asList();
        }, new JsonTransformer());
        
        post("/products", (request, response) ->
        {
            preFilter(request);
            
            Product product = gson.fromJson(request.body(), Product.class);
            
            if (product == null)
                halt(401, gson.toJson(Error.withCause("invalid credenials")));
            
            if (datastore.createQuery(Product.class)
                    .field("name")
                    .equal(product.name())
                    .get() != null)
                halt(400, gson.toJson(Error.withCause("name must be unique")));
            
            datastore.save(product);
            return product;
        }, new JsonTransformer());
    }
    
    public static void preFilter(spark.Request request)
    {
        System.out.println("----------------------------------------------");
        System.out.println(request.requestMethod());
        for (String line : request.headers())
            System.out.println(line+" "+request.headers(line));
        
        System.out.println(request.body());
    }
}
