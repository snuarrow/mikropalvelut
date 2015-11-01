/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.configurationservice;

import com.google.gson.Gson;
import ohtu.domainlib.UrlCollection;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import static spark.SparkBase.port;

/**
 * new
 * @author hexvaara
 */
public class ConfMain
{
    public static void main(String[] args)
    {
        System.out.println("Configuration Service started!");
        
        port(4569);
        
        Gson gson = new Gson();
        
        UrlCollection uc;
        UrlCollection testUc;
        
        String session = "http://localhost:4567/session";
        String token = "http://localhost:4567/token";
        String persons = "http://Localhost:4567/persons";
        String products = "http://Localhost:4568/products";
        String mongourl = "mongodb://ohtu:ohtu@ds041651.mongolab.com:41651/kanta11";
        String testmongourl = "mongodb://ohtu:ohtu@ds035664.mongolab.com:35664/tests";
        
        uc = new UrlCollection(mongourl, token, persons, products, session);
        testUc = new UrlCollection(testmongourl, token, persons, products, session);
        
        String json = gson.toJson(uc);
        String testjson = gson.toJson(testUc);
        
        get("/running", (request, response) -> {
            preFilter(request);
            return "1";
        });
        
        get("/configurations", (request, response) ->
        {
            preFilter(request);
            return json;
        });
        
        get("/testconfigurations", (Request request, Response response) ->
        {
            preFilter(request);
            return testjson;
        });
    
    }
    public static void preFilter(Request request)
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