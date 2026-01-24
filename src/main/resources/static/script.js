// ====== CONFIG ======
const API = "/reservas";

// Se suas páginas estão na raiz:
const LOGIN_PAGE = "/login.html";
const LISTA_PAGE = "/lista.html";

// ====== HELPERS ======
async function fetchAuth(url, options = {}) {
  const res = await fetch(url, {
    ...options,
    credentials: "include", // ✅ manda/recebe cookie de sessão
    headers: {
      ...(options.headers || {}),
    },
  });

  // Se backend mandou redirect pro login, manda o usuário pro login
  // (res.redirected costuma vir true; e a URL final costuma conter /login)
  if (res.redirected || (res.url && res.url.includes("/login"))) {
    window.location.href = LOGIN_PAGE;
    throw new Error("Redirected to login");
  }

  // 401/403 => não autenticado
  if (res.status === 401 || res.status === 403) {
    window.location.href = LOGIN_PAGE;
    throw new Error("Not authenticated");
  }

  return res;
}

async function resToJsonSafe(res) {
  const ct = (res.headers.get("content-type") || "").toLowerCase();

  // Se não for JSON, provavelmente veio HTML (login page) ou erro
  if (!ct.includes("application/json")) {
    const txt = await res.text().catch(() => "");
    // Se veio login, redireciona
    if (txt.includes("login") || txt.includes("Entrar")) {
      window.location.href = LOGIN_PAGE;
    }
    throw new Error("Resposta não é JSON (provável login/erro).");
  }

  return res.json();
}

function el(id) {
  return document.getElementById(id);
}

// ====== LISTAR ======
async function carregarReservas() {
  const lista = el("listaReservas");
  if (!lista) return;

  try {
    const res = await fetchAuth(API);
    const dados = await resToJsonSafe(res);

    lista.innerHTML = "";
    dados.forEach((r) => {
      lista.innerHTML += `
        <tr class="border-t">
          <td class="p-2">${r.nome ?? "-"}</td>
          <td class="p-2">${r.numeroQuarto || "-"}</td>
          <td class="p-2">${r.checkin ?? "-"}</td>
          <td class="p-2">${r.checkout ?? "-"}</td>
          <td class="p-2">${r.status ?? "-"}</td>
          <td class="p-2">
            <button onclick="excluir(${r.id})" class="text-red-600">Excluir</button>
          </td>
        </tr>`;
    });
  } catch (e) {
    console.error(e);
    // Se quiser mostrar uma mensagem na tela, crie um <div id="msg"></div>
    const msg = el("msg");
    if (msg) msg.textContent = "Erro ao carregar reservas. Verifique login/servidor.";
  }
}

// ====== CRIAR ======
const form = el("formReserva");
if (form) {
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const reserva = {
      nome: el("nome")?.value?.trim() || "",
      telefone: el("telefone")?.value?.trim() || "",
      checkin: el("checkin")?.value || "",
      checkout: el("checkout")?.value || "",
      placaCarro: el("placaCarro")?.value?.trim() || "",
      numeroQuarto: el("numeroQuarto")?.value?.trim() || "",
      tipoQuarto: el("tipoQuarto")?.value?.trim() || "",
      quantidadePessoas: Number(el("quantidadePessoas")?.value || 0),
      valorTotal: Number(el("valorTotal")?.value || 0),
      valorPago: Number(el("valorPago")?.value || 0),
      status: el("status")?.value || "reservado",
    };

    try {
      const res = await fetchAuth(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reserva),
      });

      if (!res.ok) {
        const txt = await res.text().catch(() => "");
        throw new Error(`Falha ao salvar. Status ${res.status}. ${txt}`);
      }

      window.location.href = LISTA_PAGE;
    } catch (err) {
      console.error(err);
      alert("Não foi possível salvar a reserva. Verifique se está logado e se o backend está online.");
    }
  });
}

// ====== EXCLUIR ======
async function excluir(id) {
  try {
    const res = await fetchAuth(`${API}/${id}`, { method: "DELETE" });

    if (!res.ok) {
      const txt = await res.text().catch(() => "");
      throw new Error(`Falha ao excluir. Status ${res.status}. ${txt}`);
    }

    await carregarReservas();
  } catch (e) {
    console.error(e);
    alert("Não foi possível excluir. Verifique login/servidor.");
  }
}

// Deixa a função global pro onclick funcionar
window.excluir = excluir;

// ====== INIT ======
carregarReservas();