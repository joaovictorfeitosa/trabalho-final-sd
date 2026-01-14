#  Cliente em Linguagem 1 (Python)
import requests
import json
import sys

URL = "http://localhost:8080/api/votacao"
meu_login = None

def enviar_requisicao(payload):
    try:
        headers = {'Content-Type': 'application/json'}
        response = requests.post(URL, data=json.dumps(payload), headers=headers)
        if response.status_code == 200:
            return response.json()
        else:
            print(f"Erro HTTP: {response.status_code}")
            return None
    except Exception as e:
        print(f"Erro de conexão: {e}")
        return None

def main():
    global meu_login
    print("--- Cliente de Votação (Python) ---")
    
    while True:
        print("\n1. Login")
        print("2. Listar Candidatos")
        print("3. Votar")
        print("4. Adicionar Candidato (Admin)")
        print("9. Sair")
        opcao = input("Opção: ")

        payload = {}

        if opcao == "1":
            login = input("Login: ")
            senha = input("Senha: ")
            payload = {"comando": "LOGIN", "login": login, "senha": senha}
            resp = enviar_requisicao(payload)
            if resp and resp.get("status") == "OK":
                meu_login = login
                print(f"Login com sucesso! Tipo: {resp.get('tipoUsuario')}")
            else:
                print(f"Erro: {resp.get('mensagem') if resp else 'Falha'}")

        elif opcao == "2":
            payload = {"comando": "LISTAR"}
            resp = enviar_requisicao(payload)
            if resp and resp.get("status") == "OK":
                candidatos = resp.get("candidatos", [])
                print("--- Candidatos ---")
                for c in candidatos:
                    print(f"{int(c['numero'])} - {c['nome']} ({int(c['votos'])} votos)")

        elif opcao == "3":
            if not meu_login:
                print("Faça login primeiro!")
                continue
            try:
                num = int(input("Número do candidato: "))
                payload = {"comando": "VOTAR", "numero": num, "loginVotante": meu_login}
                resp = enviar_requisicao(payload)
                print(f"Resultado: {resp.get('mensagem')}")
            except:
                print("Número inválido")

        elif opcao == "4":
            if not meu_login:
                print("Faça login primeiro!")
                continue
            num = int(input("Novo Número: "))
            nome = input("Nome do Candidato: ")
            payload = {"comando": "ADDCANDIDATO", "numero": num, "nome": nome, "loginAdmin": meu_login}
            resp = enviar_requisicao(payload)
            print(f"Resultado: {resp.get('mensagem')}")

        elif opcao == "9":
            break

if __name__ == "__main__":
    main()