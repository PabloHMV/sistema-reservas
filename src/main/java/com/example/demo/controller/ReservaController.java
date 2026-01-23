package com.example.demo.controller;

import com.example.demo.model.Reserva;
import com.example.demo.service.ReservaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@CrossOrigin
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // ✅ CADASTRAR RESERVA
    @PostMapping
    public Reserva salvar(@RequestBody Reserva reserva) {
        return reservaService.salvar(reserva);
    }

    // ✅ LISTAR RESERVAS
    @GetMapping
    public List<Reserva> listar() {
        return reservaService.listar();
    }

    // ✅ BUSCAR RESERVA POR ID (ADICIONADO)
    @GetMapping("/{id}")
    public Reserva buscarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id);
    }

    // ✅ ATUALIZAR
    @PutMapping("/{id}")
    public Reserva atualizar(@PathVariable Long id, @RequestBody Reserva reserva) {
        return reservaService.atualizar(id, reserva);
    }

    // ✅ EXCLUIR
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        reservaService.excluir(id);
    }

    // ✅ GERAR VOUCHER PDF
    @GetMapping("/voucher/{id}")
    public ResponseEntity<byte[]> gerarVoucher(@PathVariable Long id) {

        byte[] pdf = reservaService.gerarVoucher(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=voucher-reserva-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}