package com.epam.resource_processor.util;

public class CustomUtility {

    public static Integer toInteger(String value){

        try{
            return Integer.valueOf(value);
        } catch (NumberFormatException e){
            throw new NumberFormatException(e.getLocalizedMessage());
        }
    }
}
