package com.mayorista.pedidos.service;

import com.mayorista.pedidos.dto.ProductoDTO;
import com.mayorista.pedidos.model.Pedido;
import com.mayorista.pedidos.repository.PedidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidosRepository pedidosRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String CATALOGO_URL = "http://localhost:8081/catalogo";

    public Pedido crearPedido(Pedido pedido) {
        try {
            ProductoDTO producto = webClientBuilder.build()
                    .get()
                    .uri(CATALOGO_URL + "/" + pedido.getIdProducto())
                    .retrieve()
                    .bodyToMono(ProductoDTO.class)
                    .block(); // .block() hace que espere en la línea hasta que el Catálogo responda

            if (producto != null) {
                //calculo
                Integer total = producto.getPrecioMayorista() * pedido.getCantidad();

                pedido.setTotalPagar(total);
                pedido.setFechaCreacion(LocalDateTime.now());

                //guardar en la base de datos de lospedidos
                return pedidosRepository.save(pedido);
            } else {
                throw new RuntimeException("ERROR! el producto no existe en el catálogo.");
            }

        } catch (WebClientResponseException.NotFound ex) {
            throw new RuntimeException("ERROR! El producto con ID " + pedido.getIdProducto() + " no fue encontrado en el Catálogo (Error 404).");
        } catch (Exception ex) {
            throw new RuntimeException("Error   de comunicación con el microservicio de Catálogo: " + ex.getMessage());
        }
    }

    public List<Pedido> listarPedidos() {
        return pedidosRepository.findAll();
    }
}