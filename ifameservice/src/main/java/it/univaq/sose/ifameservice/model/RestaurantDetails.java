package it.univaq.sose.ifameservice.model;

import java.util.List;

import it.univaq.sose.restaurantservice.FoodCategory;

public class RestaurantDetails {

    private Integer id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String street;
    private List<FoodCategory> foodCategories;
    
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public List<FoodCategory> getFoodCategories() {
		return foodCategories;
	}
	public void setFoodCategories(List<FoodCategory> foodCategories) {
		this.foodCategories = foodCategories;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
}
