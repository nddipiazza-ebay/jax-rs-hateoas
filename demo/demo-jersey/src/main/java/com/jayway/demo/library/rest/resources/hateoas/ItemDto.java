package com.jayway.demo.library.rest.resources.hateoas;

public class ItemDto {

	public ItemDto(String name, String id, String condition,
			String listingType, String itemURL, double price, 
			String sellerName, String category) {
		super();
		this.name = name;
		this.id = id;
		this.condition = condition;
		this.listingType = listingType;
		this.itemURL = itemURL;
		this.price = price;
		this.sellerName = sellerName;
		this.setCategory(category);
	}

	public ItemDto() {
	}

	private String name;
	private String id;
	private String condition;
	private String listingType;
	private String itemURL;
	private double price;
	private String sellerName;
	private String category;

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}


	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getListingType() {
		return listingType;
	}

	public void setListingType(String listingType) {
		this.listingType = listingType;
	}

	public String getItemURL() {
		return itemURL;
	}

	public void setItemURL(String itemURL) {
		this.itemURL = itemURL;
	}


	public ItemDto(String id2) {
		this.id = id2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}
}
