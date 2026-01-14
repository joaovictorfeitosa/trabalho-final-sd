$URL = "http://localhost:8080/api/votacao"
$meuLogin = $null

function Enviar-Requisicao {
    param ($payload)
    try {
        $jsonPayload = $payload | ConvertTo-Json -Depth 10
        $response = Invoke-RestMethod -Uri $URL -Method Post -Body $jsonPayload -ContentType "application/json" -ErrorAction Stop
        return $response
    } catch {
        Write-Host "Erro na conexão: $_" -ForegroundColor Red
        return $null
    }
}

while ($true) {
    Write-Host "`n--- Cliente de Votação (PowerShell) ---" -ForegroundColor Cyan
    Write-Host "1. Login"
    Write-Host "2. Listar Candidatos"
    Write-Host "3. Votar"
    Write-Host "4. Adicionar Candidato (Admin)"
    Write-Host "9. Sair"
    $opcao = Read-Host "Escolha"

    if ($opcao -eq "1") {
        $l = Read-Host "Login"
        $s = Read-Host "Senha"
        $payload = @{ comando = "LOGIN"; login = $l; senha = $s }
        $resp = Enviar-Requisicao $payload
        
        if ($resp.status -eq "OK") {
            $meuLogin = $l
            Write-Host "Login realizado! Tipo: $($resp.tipoUsuario)" -ForegroundColor Green
        } else {
            Write-Host "Erro: $($resp.mensagem)" -ForegroundColor Red
        }
    }
    elseif ($opcao -eq "2") {
        $payload = @{ comando = "LISTAR" }
        $resp = Enviar-Requisicao $payload
        if ($resp.status -eq "OK") {
            Write-Host "--- Candidatos ---" -ForegroundColor Yellow
            foreach ($c in $resp.candidatos) {
                Write-Host "$($c.numero) - $($c.nome) ($($c.votos) votos)"
            }
        }
    }
    elseif ($opcao -eq "3") {
        if (-not $meuLogin) { Write-Host "Faça login antes!" -ForegroundColor Red; continue }
        $num = Read-Host "Número do Candidato"
        $payload = @{ comando = "VOTAR"; numero = [int]$num; loginVotante = $meuLogin }
        $resp = Enviar-Requisicao $payload
        Write-Host "Servidor: $($resp.mensagem)" -ForegroundColor Cyan
    }
    elseif ($opcao -eq "4") {
        if (-not $meuLogin) { Write-Host "Faça login antes!" -ForegroundColor Red; continue }
        $num = Read-Host "Novo Número"
        $nome = Read-Host "Nome"
        $payload = @{ comando = "ADDCANDIDATO"; numero = [int]$num; nome = $nome; loginAdmin = $meuLogin }
        $resp = Enviar-Requisicao $payload
        Write-Host "Servidor: $($resp.mensagem)" -ForegroundColor Cyan
    }
    elseif ($opcao -eq "9") { break }
}