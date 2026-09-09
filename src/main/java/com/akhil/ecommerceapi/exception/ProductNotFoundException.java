package com.akhil.ecommerceapi.exception;

public class ProductNotFoundException extends RuntimeException{

	public ProductNotFoundException(String message) {
        super(message);
    }

}
