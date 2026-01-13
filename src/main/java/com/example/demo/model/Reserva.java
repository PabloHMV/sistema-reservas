package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================
    // DADOS DO CLIENTE
    // =====================
    private String nome;
    private String telefone;

    // CPF ou CNPJ (opcional)
    private String cpfCnpj;

    // =====================
    // DADOS DA RESERVA
    // =====================
    private LocalDate checkin;
    private LocalDate checkout;

    private String tipoQuarto;

    // Número do quarto (opcional)
    private Integer numeroQuarto;

    private Integer quantidadePessoas;

    // =====================
    // VALORES
    // =====================
    private Double valorTotal;
    private Double valorPago;
    private Double valorRestante;

    // =====================
    // STATUS
    // =====================
    private String status;

    // =====================
    // VOUCHER
    // =====================
    @Column(unique = true)
    private String voucherNumero;

    // =====================
    // GETTERS E SETTERS
    // =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public LocalDate getCheckin() {
        return checkin;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public LocalDate getCheckout() {
        return checkout;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }

    public String getTipoQuarto() {
        return tipoQuarto;
    }

    public void setTipoQuarto(String tipoQuarto) {
        this.tipoQuarto = tipoQuarto;
    }

    public Integer getNumeroQuarto() {
        return numeroQuarto;
    }

    public void setNumeroQuarto(Integer numeroQuarto) {
        this.numeroQuarto = numeroQuarto;
    }

    public Integer getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double getValorPago() {
        return valorPago;
    }

    public void setValorPago(Double valorPago) {
        this.valorPago = valorPago;
    }

    public Double getValorRestante() {
        return valorRestante;
    }

    public void setValorRestante(Double valorRestante) {
        this.valorRestante = valorRestante;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVoucherNumero() {
        return voucherNumero;
    }

    public void setVoucherNumero(String voucherNumero) {
        this.voucherNumero = voucherNumero;
    }
}
