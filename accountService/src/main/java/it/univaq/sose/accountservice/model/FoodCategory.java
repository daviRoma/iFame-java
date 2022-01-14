package it.univaq.sose.accountservice.model;

public enum FoodCategory {
    
    FISH("fish"),
    MEAL("meal"),
    SUSHI("sushi"),
    PIZZA("pizza"),
    HAMBURGER("hamburger"),
    ETHNIC("ethnic");
	
	private String displayValue;
	
	private FoodCategory(String value) {
		this.displayValue = value;
	}
	
	public String getDisplayValue() {
		return this.displayValue;
	}
}
