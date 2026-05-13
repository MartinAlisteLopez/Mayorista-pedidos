package com.mayorista.pedidos.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long idProducto;
    private String nombre;
    private Integer precioMayorista;
}