package com.github.phillbarber.conductor.facade;



import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/order")
public class OrderResource {


    private final Map mapOfStuff;

    public OrderResource(String message) {
        mapOfStuff = new HashMap();
        mapOfStuff.put("message", message);

        System.out.println("NOICE");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map createOrder() {
        return mapOfStuff;

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map helloList() {
        return mapOfStuff;

    }

    public void stuff(){

    }

}