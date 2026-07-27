package com.shopsphere.product_service.exception;

public class ProductLockedException extends RuntimeException{
    public ProductLockedException(String message){
        super(message);
    }
    
}
