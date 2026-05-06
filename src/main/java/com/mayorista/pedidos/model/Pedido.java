package com.mayorista.pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @NotNull(message = "El Id del producto es OBLIGATORIO ")
    @Column(nullable = false)
    private Long idProducto;

    @NotNull(message = "La cantidad es OBLIGATORIA")
    @Min(value = 1, message = "Se debe pedir como minimo una unidad")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer TotalPagar;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}
