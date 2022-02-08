package com.gt.backend.components;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gt.backend.primeng.Paginator;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PaginatorConverter implements Converter<String, Paginator> {

    @Override
    public Paginator convert(String paginatorJson) {
        Paginator paginator = null;
        try {
            paginator = new ObjectMapper().readValue(paginatorJson, Paginator.class);
        } catch (JsonProcessingException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al convertir el json a Paginator", e);
        }
        return paginator;
    }
    
}
