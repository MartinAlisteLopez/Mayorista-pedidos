package com.mayorista.pedidos.controller;

import com.mayorista.pedidos.model.Pedido;
import com.mayorista.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> crearPedido(@Valid @RequestBody Pedido pedido) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedido);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return new ResponseEntity<>(pedidoService.listarPedidos(), HttpStatus.OK);
    }
}