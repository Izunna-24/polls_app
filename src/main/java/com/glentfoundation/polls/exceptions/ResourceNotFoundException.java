package com.glentfoundation.polls.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
   private String resourceName;
   private String fieldName;
   private Object fieldValue;

   public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
       super(String.format("%s not found for field %s", resourceName, fieldName));
       this.resourceName = resourceName;
       this.fieldName = fieldName;
       this.fieldValue = fieldValue;
   }

}
