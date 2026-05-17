package com.mayorista.pedidos.dto;

public class InventarioDTO {
    private Long id;
    private String nombreProducto;
    private Integer cantidadStock;
    private Double precio;
    private String categoria;
    private Long idProveedor;

    public Long getId() { return id; }
    public String getNombreProducto() { return nombreProducto; }
    public Integer getCantidadStock() { return cantidadStock; }
    public Double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public Long getIdProveedor() { return idProveedor; }

    public void setId(Long id) { this.id = id; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public void setCantidadStock(Integer cantidadStock) { this.cantidadStock = cantidadStock; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }
}