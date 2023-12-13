package com.github.phillbarber.conductor.facade;



import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/order")
public class OrderResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String createOrder() {
        return "YAY";

    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> helloList() {

        List<String> list = new ArrayList<>();


        list.add("xfsdfsdf");
        list.add("xfsdfsdf");
        list.add("xfsdfsdf");

        return list;

    }

}