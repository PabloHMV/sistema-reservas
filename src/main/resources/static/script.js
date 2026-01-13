const API = "http://localhost:8080/reservas";

function carregarReservas() {
    const lista = document.getElementById("listaReservas");
    if (!lista) return;

    fetch(API)
        .then(r => r.json())
        .then(dados => {
            lista.innerHTML = "";
            dados.forEach(r => {
                lista.innerHTML += `
                <tr class="border-t">
                    <td class="p-2">${r.nome}</td>
                    <td class="p-2">${r.numeroQuarto || "-"}</td>
                    <td class="p-2">${r.checkin}</td>
                    <td class="p-2">${r.checkout}</td>
                    <td class="p-2">${r.status}</td>
                    <td class="p-2">
                        <button onclick="excluir(${r.id})" class="text-red-600">Excluir</button>
                    </td>
                </tr>`;
            });
        });
}

const form = document.getElementById("formReserva");
if (form) {
    form.addEventListener("submit", e => {
        e.preventDefault();

        const reserva = {
            nome: nome.value,
            telefone: telefone.value,
            checkin: checkin.value,
            checkout: checkout.value,
            placaCarro: placaCarro.value,
            numeroQuarto: numeroQuarto.value,
            tipoQuarto: tipoQuarto.value,
            quantidadePessoas: quantidadePessoas.value,
            valorTotal: valorTotal.value,
            valorPago: valorPago.value,
            status: status.value
        };

        fetch(API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(reserva)
        }).then(() => window.location.href = "lista.html");
    });
}

function excluir(id) {
    fetch(`${API}/${id}`, { method: "DELETE" })
        .then(() => carregarReservas());
}

carregarReservas();
