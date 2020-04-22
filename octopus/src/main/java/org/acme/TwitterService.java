package org.acme;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Set;
import javax.ws.rs.core.MediaType;


@Path("/twitter")
@RegisterRestClient(configKey="twitter-api")
public interface TwitterService {

    @GET
    @Path("/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    javax.ws.rs.core.Response getByTopic(@PathParam String topic);
}