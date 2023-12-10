package com.github.phillbarber.conductor.facade;



import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/hello")
public class MyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Jersey Jetty example.";
    }

    @Path("/{username}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello(@PathParam("username") String name) {

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