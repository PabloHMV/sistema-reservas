package com.example.demo.controller;

import com.example.demo.model.Reserva;
import com.example.demo.repository.ReservaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    private final ReservaRepository reservaRepository;

    public VoucherController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/{id}")
    public void gerarVoucher(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=voucher.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font texto = new Font(Font.HELVETICA, 12);

        document.add(new Paragraph("VOUCHER DE RESERVA", titulo));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Nome: " + reserva.getNome(), texto));
        document.add(new Paragraph("Telefone: " + reserva.getTelefone(), texto));
        document.add(new Paragraph("Check-in: " + reserva.getCheckin(), texto));
        document.add(new Paragraph("Check-out: " + reserva.getCheckout(), texto));
        document.add(new Paragraph("Valor Total: R$ " + reserva.getValorTotal(), texto));
        document.add(new Paragraph("Valor Pago: R$ " + reserva.getValorPago(), texto));
        document.add(new Paragraph("Valor Restante: R$ " + reserva.getValorRestante(), texto));
        document.add(new Paragraph("Status: " + reserva.getStatus(), texto));

        document.close();
    }
}
