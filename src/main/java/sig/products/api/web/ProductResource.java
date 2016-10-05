package sig.products.api.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import sig.products.api.dto.Product;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Projections.excludeId;

@Path("/")
public class ProductResource {
	
	public String mongoUri = "mongodb://localhost";
	public String sigDb = "sigdb";
		
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Response getRoot() {
		System.out.println("GET request for /");
		return Response.status(200).entity("<html><body><h2>SIG Products API</h2></body></html>").build();
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@QueryParam("sku") final String sku) {
		System.out.println("GET request for /products");
		
		try {
			
			Bson fieldProjection = fields(include("sku", "name", "description", "lastUpdatedTimestamp"), excludeId());
			Bson queryBySku = null;
			
			// TODO filtering, sorting, pagination		
			if (sku != null && !sku.trim().isEmpty()) {
				queryBySku = eq("sku", sku);
			}
						
			MongoClient client = new MongoClient(new MongoClientURI(mongoUri)); 
			MongoDatabase db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");
			
			FindIterable<Document> docs = collection.find(queryBySku).projection(fieldProjection);
			
			List<Product> productList = new ArrayList<Product>();
			if (docs != null && docs.iterator() != null) {
				while (docs.iterator().hasNext()) {
					Document doc = docs.iterator().next();
					if (doc != null) {
						Product product = new Product();
						product.setSku(doc.getString("sku"));
						product.setName(doc.getString("name"));
						product.setDescription(doc.getString("description"));
						product.setLastUpdatedTimestamp(doc.getDate("lastUpdatedTimestamp"));
						productList.add(product);
					}
				}
			}							
			client.close();
			
			return Response.status(200).entity(productList).build();
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500).entity("").build();
		}
	}

	@GET
	@Path("{sku}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("sku") final String sku) {
		System.out.println("GET request for /products/");
		
		try {
			
			Bson queryBySku = eq("sku", sku);
			Bson fieldProjection = fields(include("sku", "name", "description", "lastUpdatedTimestamp"), excludeId());
			
			MongoClient client = new MongoClient(new MongoClientURI(mongoUri)); 
			MongoDatabase db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");
			
			Document doc = collection.find(queryBySku).projection(fieldProjection).first();
			client.close();
			
			if (doc == null) {
				return Response.status(404).entity("").build();
			}				
					
			Product product = new Product();
			product.setSku(doc.getString("sku"));
			product.setName(doc.getString("name"));
			product.setDescription(doc.getString("description"));
			product.setLastUpdatedTimestamp(doc.getDate("lastUpdatedTimestamp"));			
			return Response.status(200).entity(product).build();
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500).entity("").build();
		}
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)	
	public Response post(final Product product) {		
		System.out.println("POST request for /products");

		try {

			String productInJson = new ObjectMapper().writeValueAsString(product);		
			System.out.println(productInJson);

			if (product.getSku() == null || product.getSku().trim().isEmpty() || 
				product.getName() == null || product.getName().trim().isEmpty()) {
				return Response.status(400).entity("").build();
			}
			
			Bson queryBySku = eq("sku", product.getSku());
			Document valueDoc = new Document().append("sku", product.getSku()).append("name", product.getName()).append("description", product.getDescription());
						
			MongoClient client = new MongoClient(new MongoClientURI(mongoUri)); 
			MongoDatabase db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");

			collection.updateOne(queryBySku, new Document("$set", valueDoc).append("$currentDate", new Document("lastUpdatedTimestamp", true)),	new UpdateOptions().upsert(true));
			client.close();
			
			return Response.status(201).location(new URI("/products/" + product.getSku())).entity("").build();						
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500).entity("").build();
		}
	}
			
}
