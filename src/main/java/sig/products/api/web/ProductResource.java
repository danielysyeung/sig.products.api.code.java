package sig.products.api.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Projections.excludeId;

@Path("/")
public class ProductResource {
	
	public String mongoUri = "mongodb://localhost";
	public String sigDb = "sigdb";
	
	public String httpHeaderAccessControlAllowOrigin = "*";
	public String httpHeaderAccessControlAllowMethods = "HEAD, GET, PUT, POST, DELETE, OPTIONS";
	public String httpHeaderAccessControlAllowHeaders = "Accept, Content-Type";
	public String about = "{ \"Name\": \"ProductsService\", \"Version\": \"0.1\", \"Framework\": \"Java+MongoDB\" }";
	public int dbQueryHardLimit = 100;
			
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@QueryParam("sku") final String sku) {
		System.out.println("GET request for /products");
		
		MongoClient client = null;
		MongoDatabase db = null;
		
		try {
			
			Bson fieldProjection = fields(include("sku", "name", "description", "lastUpdatedTimestamp"), excludeId());
			Bson queryBySku = new Document();			
			
			// TODO filtering, sorting, pagination		
			if (sku != null && !sku.trim().isEmpty()) {
				queryBySku = eq("sku", sku);
			}
			int queryLimit = dbQueryHardLimit;
						
			client = new MongoClient(new MongoClientURI(mongoUri)); 
			db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");
			
			FindIterable<Document> docs = collection.find(queryBySku).limit(queryLimit).projection(fieldProjection);
			
			List<Product> productList = new ArrayList<Product>();			
			if (docs != null && docs.iterator() != null) {				
				Iterator<Document> i = docs.iterator();
				while (i.hasNext()) {
					Document doc = i.next();
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
			return Response.status(200)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity(productList).build();
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	@GET
	@Path("{sku}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("sku") final String sku) {
		System.out.println("GET request for /products/");
		
		MongoClient client = null;
		MongoDatabase db = null;
		
		try {
			
			Bson queryBySku = eq("sku", sku);
			Bson fieldProjection = fields(include("sku", "name", "description", "lastUpdatedTimestamp"), excludeId());
			
			client = new MongoClient(new MongoClientURI(mongoUri)); 
			db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");
			
			Document doc = collection.find(queryBySku).projection(fieldProjection).first();
			client.close();
			
			if (doc == null) {
				return Response.status(404)
					.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
					.entity("").build();
			}				
					
			Product product = new Product();
			product.setSku(doc.getString("sku"));
			product.setName(doc.getString("name"));
			product.setDescription(doc.getString("description"));
			product.setLastUpdatedTimestamp(doc.getDate("lastUpdatedTimestamp"));			
			return Response.status(200)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity(product).build();
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(final Product product) {		
		System.out.println("POST request for /products");

		MongoClient client = null;
		MongoDatabase db = null;
		
		try {

			String productInJson = new ObjectMapper().writeValueAsString(product);		
			System.out.println(productInJson);

			if (product.getSku() == null || product.getSku().trim().isEmpty() || 
				product.getName() == null || product.getName().trim().isEmpty()) {
				return Response.status(400)
					.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)		
					.entity("").build();
			}
			
			Bson queryBySku = eq("sku", product.getSku());
			Document valueDoc = new Document()
				.append("sku", product.getSku())
				.append("name", product.getName())
				.append("description", product.getDescription());
						
			client = new MongoClient(new MongoClientURI(mongoUri)); 
			db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");

			collection.updateOne(
				queryBySku, 
				new Document("$set", valueDoc).append("$currentDate", new Document("lastUpdatedTimestamp", true)),
				new UpdateOptions().upsert(true));
			client.close();
			
			return Response.status(201)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.location(new URI("/products/" + product.getSku()))
				.entity("").build();						
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();
		} finally {
			if (client != null) {
				client.close();
			}			
		}
	}
	
	@PUT
	@Path("{sku}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response put(@PathParam("sku") final String sku, final Product product) {		
		System.out.println("PUT request for /products/");

		MongoClient client = null;
		MongoDatabase db = null;
		
		try {

			String productInJson = new ObjectMapper().writeValueAsString(product);		
			System.out.println(productInJson);

			if (product.getSku() == null || product.getSku().trim().isEmpty() || 
				product.getName() == null || product.getName().trim().isEmpty()) {
				return Response.status(400)
					.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
					.entity("").build();
			}
			
			Bson queryBySku = eq("sku", sku);
			Document valueDoc = new Document()
				.append("sku", product.getSku())
				.append("name", product.getName())
				.append("description", product.getDescription());
						
			client = new MongoClient(new MongoClientURI(mongoUri)); 
			db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");
			
			Document doc = collection.find(queryBySku).first();
			if (doc == null) {
				client.close();
				return Response.status(404)
					.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
					.entity("").build();
			}

			collection.updateOne(
				queryBySku, 
				new Document("$set", valueDoc).append("$currentDate", new Document("lastUpdatedTimestamp", true)),
				new UpdateOptions().upsert(true));
			client.close();
			
			return Response.status(204)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();						
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
	
	@DELETE
	@Path("{sku}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("sku") final String sku) {		
		System.out.println("DELETE request for /products/");

		MongoClient client = null; 
		MongoDatabase db = null;
		
		try {
			
			Bson queryBySku = eq("sku", sku);

			client = new MongoClient(new MongoClientURI(mongoUri)); 
			db = client.getDatabase(sigDb); 
			MongoCollection<Document> collection = db.getCollection("product");
			
			Document doc = collection.find(queryBySku).first();
			if (doc == null) {
				client.close();
				return Response.status(404)
					.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
					.entity("").build();
			}

			collection.deleteOne(queryBySku);
			client.close();
			
			return Response.status(200)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();						
			
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return Response.status(500)
				.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
				.entity("").build();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
	
	@OPTIONS
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response optionsAll() {
		System.out.println("OPTIONS request for /products");
		return Response.status(200)
			.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
			.header("Access-Control-Allow-Headers", httpHeaderAccessControlAllowHeaders)
			.header("Access-Control-Allow-Methods", httpHeaderAccessControlAllowMethods)
			.header("Allow", httpHeaderAccessControlAllowMethods)
			.entity("").build();
	}
	
	@OPTIONS
	@Path("{sku}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response options() {
		System.out.println("OPTIONS request for /products/");
		return Response.status(200)
			.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
			.header("Access-Control-Allow-Headers", httpHeaderAccessControlAllowHeaders)
			.header("Access-Control-Allow-Methods", httpHeaderAccessControlAllowMethods)
			.header("Allow", httpHeaderAccessControlAllowMethods)		
			.entity("").build();
	}
	
	@GET
	@Path("/service/about")
	@Produces(MediaType.APPLICATION_JSON)
	public Response about() {
		System.out.println("GET request for /products/service/about");
		return Response.status(200)
			.header("Access-Control-Allow-Origin", httpHeaderAccessControlAllowOrigin)
			.entity(about).build();
	}
}
