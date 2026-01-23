package com.example.demo.service;

import com.example.demo.model.Reserva;
import com.example.demo.repository.ReservaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.Year;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    // ✅ BUSCAR POR ID (resolve seu erro no editar)
    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));
    }

    // =========================
    // SALVAR
    // =========================
    public Reserva salvar(Reserva reserva) {

        validarConflito(reserva, null);

        reserva.setVoucherNumero(gerarNumeroVoucher());
        calcularValorRestante(reserva);

        return reservaRepository.save(reserva);
    }

    // =========================
    // LISTAR
    // =========================
    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    // =========================
    // ATUALIZAR
    // =========================
    public Reserva atualizar(Long id, Reserva novaReserva) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));

        validarConflito(novaReserva, id);

        reserva.setNome(novaReserva.getNome());
        reserva.setTelefone(novaReserva.getTelefone());
        reserva.setCpfCnpj(novaReserva.getCpfCnpj());
        reserva.setNumeroQuarto(novaReserva.getNumeroQuarto());
        reserva.setCheckin(novaReserva.getCheckin());
        reserva.setCheckout(novaReserva.getCheckout());
        reserva.setTipoQuarto(novaReserva.getTipoQuarto());
        reserva.setQuantidadePessoas(novaReserva.getQuantidadePessoas());
        reserva.setValorTotal(novaReserva.getValorTotal());
        reserva.setValorPago(novaReserva.getValorPago());
        reserva.setStatus(novaReserva.getStatus());

        calcularValorRestante(reserva);

        return reservaRepository.save(reserva);
    }

    // =========================
    // EXCLUIR
    // =========================
    public void excluir(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada");
        }
        reservaRepository.deleteById(id);
    }

    // =========================
    // VOUCHER
    // =========================
    public byte[] gerarVoucher(Long id) {

        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // ⚠️ No Railway, "src/main/resources/..." não existe em runtime.
            // O correto é carregar via classpath. Mantive aqui simples, mas se der erro de logo, me fala.
            // Image logo = Image.getInstance("src/main/resources/static/logoHotel.jpeg");

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font texto = new Font(Font.HELVETICA, 11);
            Font negrito = new Font(Font.HELVETICA, 11, Font.BOLD);

            Paragraph header = new Paragraph(
                    "PILAR PALACE HOTEL\nVOUCHER DE RESERVA",
                    titulo
            );
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            doc.add(header);

            Paragraph voucher = new Paragraph(
                    "Voucher Nº: " + r.getVoucherNumero(),
                    negrito
            );
            voucher.setAlignment(Element.ALIGN_CENTER);
            voucher.setSpacingAfter(15);
            doc.add(voucher);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addLinha(table, "Nome", r.getNome(), negrito, texto);
            addLinha(table, "Telefone", r.getTelefone(), negrito, texto);
            addLinha(table, "CPF/CNPJ", r.getCpfCnpj() != null ? r.getCpfCnpj() : "-", negrito, texto);
            addLinha(table, "Quarto Nº", r.getNumeroQuarto() != null ? r.getNumeroQuarto().toString() : "-", negrito, texto);
            addLinha(table, "Tipo de Quarto", r.getTipoQuarto() != null ? r.getTipoQuarto() : "-", negrito, texto);
            addLinha(table, "Check-in", r.getCheckin() != null ? r.getCheckin().toString() : "-", negrito, texto);
            addLinha(table, "Check-out", r.getCheckout() != null ? r.getCheckout().toString() : "-", negrito, texto);
            addLinha(table, "Pessoas", r.getQuantidadePessoas() != null ? String.valueOf(r.getQuantidadePessoas()) : "-", negrito, texto);
            addLinha(table, "Valor Total", "R$ " + (r.getValorTotal() != null ? r.getValorTotal() : 0), negrito, texto);
            addLinha(table, "Valor Pago", "R$ " + (r.getValorPago() != null ? r.getValorPago() : 0), negrito, texto);
            addLinha(table, "Valor Restante", "R$ " + (r.getValorRestante() != null ? r.getValorRestante() : 0), negrito, texto);
            addLinha(table, "Status", r.getStatus() != null ? r.getStatus() : "-", negrito, texto);

            doc.add(table);

            Paragraph rodape = new Paragraph(
                    "\nApresente este voucher no check-in.\nObrigado por escolher o Pilar Palace Hotel!",
                    texto
            );
            rodape.setAlignment(Element.ALIGN_CENTER);
            doc.add(rodape);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar voucher");
        }
    }

    // =========================
    // 🔒 VALIDAÇÃO DE CONFLITO
    // =========================
    private void validarConflito(Reserva reserva, Long idIgnorar) {

        if (reserva.getNumeroQuarto() == null) return;
        if (reserva.getCheckin() == null || reserva.getCheckout() == null) return;

        List<Reserva> conflitos = reservaRepository.verificarConflito(
                reserva.getNumeroQuarto(),
                reserva.getCheckin(),
                reserva.getCheckout()
        );

        for (Reserva r : conflitos) {
            if (idIgnorar == null || !r.getId().equals(idIgnorar)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este quarto já está reservado neste período");
            }
        }
    }

    private void calcularValorRestante(Reserva r) {
        if (r.getValorTotal() != null && r.getValorPago() != null) {
            r.setValorRestante(r.getValorTotal() - r.getValorPago());
        }
    }

    private String gerarNumeroVoucher() {
        long total = reservaRepository.count() + 1;
        return "PP-" + Year.now().getValue() + "-" + String.format("%06d", total);
    }

    private void addLinha(PdfPTable table, String label, String valor, Font fLabel, Font fValor) {
        table.addCell(new Phrase(label, fLabel));
        table.addCell(new Phrase(valor, fValor));
    }
}