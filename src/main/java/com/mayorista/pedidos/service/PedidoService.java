package com.mayorista.pedidos.service;

import com.mayorista.pedidos.dto.FacturaDTO;
import com.mayorista.pedidos.dto.InventarioDTO;
import com.mayorista.pedidos.dto.ProductoDTO;
import com.mayorista.pedidos.model.Pedido;
import com.mayorista.pedidos.repository.PedidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidosRepository pedidosRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String CATALOGO_URL = "http://localhost:8081/api/v1/catalogo";

    public Pedido crearPedido(Pedido pedido) {
        try {
            ProductoDTO producto = webClientBuilder.build()
                    .get()
                    .uri(CATALOGO_URL + "/" + pedido.getIdProducto())
                    .retrieve()
                    .bodyToMono(ProductoDTO.class)
                    .block();

            if (producto != null) {
                String inventarioUrl = "http://localhost:8084/api/v1/inventario";
                InventarioDTO inventario = WebClient.create()
                        .get()
                        .uri(inventarioUrl + "/" + pedido.getIdProducto())
                        .headers(headers -> headers.setBasicAuth("admin", "12346"))
                        .retrieve()
                        .bodyToMono(InventarioDTO.class)
                        .block();

                if (inventario != null && inventario.getCantidadStock() >= pedido.getCantidad()) {
                    inventario.setCantidadStock(inventario.getCantidadStock() - pedido.getCantidad());
                    WebClient.create()
                            .put()
                            .uri(inventarioUrl + "/" + pedido.getIdProducto())
                            .headers(headers -> headers.setBasicAuth("admin", "12346"))
                            .bodyValue(inventario)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .block();
                } else {
                    throw new RuntimeException("ERROR: no hay stock suficiente en el Inventario.");
                }

                Integer total = producto.getPrecioMayorista() * pedido.getCantidad();
                pedido.setTotalPagar(total);
                pedido.setFechaCreacion(LocalDateTime.now());
                Pedido pedidoGuardado = pedidosRepository.save(pedido);

                try {
                    FacturaDTO facturaDTO = new FacturaDTO();
                    facturaDTO.setIdPedido(pedidoGuardado.getIdPedido());
                    facturaDTO.setIdCliente(1L);
                    facturaDTO.setMontoFinal(Double.valueOf(pedidoGuardado.getTotalPagar()));

                    WebClient.create("http://localhost:8083")
                            .post()
                            .uri("/api/v1/facturas/generar")
                            .bodyValue(facturaDTO)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .block();
                    System.out.println("Factura generada remotamente.");
                } catch (Exception ex) {
                    System.out.println("Pedido guardado, pero falló la conexión con Facturas: " + ex.getMessage());
                }

                try {
                    WebClient.create("http://localhost:8087")
                            .post()
                            .uri("/api/v1/logistica/generar/" + pedidoGuardado.getIdPedido())
                            .retrieve()
                            .bodyToMono(Object.class)
                            .block();
                    System.out.println(" Logística notificada.");
                } catch (Exception ex) {
                    System.out.println(" Pedido guardado, pero falló la conexión con Logística: " + ex.getMessage());
                }

                return pedidoGuardado;

            } else {
                throw new RuntimeException("ERROR: El producto no existe en el catálogo.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en el proceso de compra: " + e.getMessage());
        }
    }

    public List<Pedido> listarPedidos() {
        return pedidosRepository.findAll();
    }
}