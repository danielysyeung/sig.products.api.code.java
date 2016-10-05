package sig.products.api.dto;

import java.util.Date;

public class Product {

	private String sku;
	private String name;
	private String description;
	private Date lastUpdatedTimestamp;
	
	public Product() {
		
	}
	
	public String getSku() {
		return sku;
	}
	
	public Product setSku(String sku) {
		this.sku = sku;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Product setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Product setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Date getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}
	
	public Product setLastUpdatedTimestamp(Date lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
		return this;
	}
}
