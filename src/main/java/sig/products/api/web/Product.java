package sig.products.api.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Product {
	
	@GET
	@Path("{sku}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProduct(@PathParam("sku") String sku) {
		System.out.println("GET request for /products/" + sku);
		return Response.ok().entity("hello1").build();
		/*
		console.log("GET request for /products/");
		  var queryBySku = { "sku":req.params.sku };  
		  var fieldProjection = { "_id":0, "sku":1, "name":1, "description":1, "lastUpdatedTimestamp":1 };
		  var queryResult;
		  var collection = db.collection("product", function(err, collection) {
		    if (err) {
			  console.log("Error accessing collection: ", err);
		      res.status(500);
		      return res.send("");
			} 
			collection.findOne(queryBySku, fieldProjection, function(err, doc) {
		      if (err) {
		        console.log("Error querying document: ", err);
		        res.status(500);
		        return res.send("");
			  } 
			  if (!doc) {
				res.status(404);
				return res.send("");
		      } 
			  res.status(200);
			  return res.send(doc);	
			});
		*/
	}
	
	/*
	@GET
	@Path("{sku}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvent() {
		System.out.println("GET request for /products/");
		return Response.ok().entity("hello1").build();
		
		
		
		console.log("");
		  var queryBySku = { "sku":req.params.sku };  
		  var fieldProjection = { "_id":0, "sku":1, "name":1, "description":1, "lastUpdatedTimestamp":1 };
		  var queryResult;
		  var collection = db.collection("product", function(err, collection) {
		    if (err) {
			  console.log("Error accessing collection: ", err);
		      res.status(500);
		      return res.send("");
			} 
			collection.findOne(queryBySku, fieldProjection, function(err, doc) {
		      if (err) {
		        console.log("Error querying document: ", err);
		        res.status(500);
		        return res.send("");
			  } 
			  if (!doc) {
				res.status(404);
				return res.send("");
		      } 
			  res.status(200);
			  return res.send(doc);	
			});
			*/
		
	// }
	/*
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postEvent(final String body) {
		System.out.println("DEBUG getEvent()");
		return Response.ok().entity("hello2").build();
	}
	*/
}
