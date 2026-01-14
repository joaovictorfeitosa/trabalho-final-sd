const readline = require('readline');

// URL da API Java
const URL_API = "http://localhost:8080/api/votacao";

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

let meuLogin = null;

// Função auxiliar para fazer perguntas no terminal
const perguntar = (query) => new Promise((resolve) => rl.question(query, resolve));

// Função para enviar dados para a API
async function enviarRequisicao(dados) {
    try {
        const response = await fetch(URL_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });
        return await response.json();
    } catch (erro) {
        console.error("Erro de conexão com a API:", erro.message);
        return null;
    }
}

async function menu() {
    console.log("\n--- Cliente de Votação (Node.js) ---");
    console.log("1. Login");
    console.log("2. Listar Candidatos");
    console.log("3. Votar");
    console.log("4. Adicionar Candidato");
    console.log("9. Sair");

    const opcao = await perguntar("Escolha: ");

    if (opcao === "1") {
        const login = await perguntar("Login: ");
        const senha = await perguntar("Senha: ");
        const resp = await enviarRequisicao({ comando: "LOGIN", login, senha });
        
        if (resp && resp.status === "OK") {
            meuLogin = login;
            console.log(`Logado com sucesso! Tipo: ${resp.tipoUsuario}`);
        } else {
            console.log(`Erro: ${resp ? resp.mensagem : 'Falha na rede'}`);
        }
    } 
    else if (opcao === "2") {
        const resp = await enviarRequisicao({ comando: "LISTAR" });
        if (resp && resp.candidatos) {
            console.log("--- Lista de Candidatos ---");
            resp.candidatos.forEach(c => {
                console.log(`${c.numero} - ${c.nome} (${c.votos} votos)`);
            });
        }
    } 
    else if (opcao === "3") {
        if (!meuLogin) { console.log("Faça login antes!"); } 
        else {
            const num = await perguntar("Número do candidato: ");
            const resp = await enviarRequisicao({ 
                comando: "VOTAR", 
                numero: parseFloat(num), 
                loginVotante: meuLogin 
            });
            console.log("Servidor:", resp.mensagem);
        }
    }
    else if (opcao === "4") {
        if (!meuLogin) { console.log("Faça login antes!"); }
        else {
            const num = await perguntar("Número: ");
            const nome = await perguntar("Nome: ");
            const resp = await enviarRequisicao({
                comando: "ADDCANDIDATO",
                numero: parseFloat(num),
                nome: nome,
                loginAdmin: meuLogin
            });
            console.log("Servidor:", resp.mensagem);
        }
    }
    else if (opcao === "9") {
        rl.close();
        return; // Sai da função, encerra o loop
    }

    // Chama o menu novamente (Loop recursivo)
    menu();
}

// Inicia o programa
menu();