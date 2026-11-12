# ADR 001 — Controle transacional na liberação de mesas

**Status:** Aceito e implementado
**Data:** 2026-11-12

## Contexto

A liberação de uma mesa envolve entidades relacionadas:
`Mesa`, `SessaoMesa` e `Subcomanda`.

Operações concorrentes de pagamento, encerramento de sessão
e liberação de mesa podem comprometer a consistência dos dados.

Uma mesa não deve ficar disponível enquanto existir uma sessão
aberta com subcomandas não regularizadas.

## Decisão

Utilizar transações gerenciadas pelo Spring e consultas com
bloqueio pessimista (`PESSIMISTIC_WRITE`) nas operações críticas.

O método `MesaService.liberarMesa()`:

1. Obtém a mesa com bloqueio pessimista.
2. Verifica se ela está ocupada.
3. Consulta a existência de uma sessão aberta.
4. Permite liberar uma ocupação manual sem sessão aberta.
5. Quando existe sessão, obtém a sessão e as subcomandas com bloqueio.
6. Impede o encerramento quando existem subcomandas não regularizadas.
7. Encerra a sessão e libera a mesa.

A regularização das subcomandas é verificada pelo método
`Subcomanda.estaRegularizadaParaEncerramento()`.

## Alternativas consideradas

### Apenas validar os estados antes de salvar

Uma validação sem bloqueios poderia permitir alterações
concorrentes entre a consulta e a persistência.

### Bloqueio otimista

O bloqueio otimista é uma alternativa possível, mas exigiria
tratamento específico de conflitos e tentativas de repetição.

### Bloqueio pessimista

Foi escolhido para serializar o acesso aos registros críticos
durante as operações transacionais.

## Consequências

### Benefícios

- Maior proteção contra alterações concorrentes.
- Preservação das regras de encerramento.
- Consistência entre mesa, sessão e subcomandas.
- Centralização das regras de negócio na camada de serviço.

### Custos e limitações

- Bloqueios podem aumentar a espera entre transações.
- Operações concorrentes exigem atenção à ordem dos bloqueios.
- O comportamento depende do banco de dados e do isolamento transacional.
- Testes adicionais de concorrência continuam necessários.
- A solução não substitui autenticação e autorização.

## Verificação

O projeto possui testes automatizados das regras de negócio
e testes de integração com MySQL para operações concorrentes.

Na execução local de `./mvnw clean test`, foram aprovados
95 testes, sem falhas ou erros.

Essa execução não representa uma nova validação dos testes
de integração MySQL.

## Referências internas

- `MesaService`
- `Subcomanda.estaRegularizadaParaEncerramento()`
- `docs/desafios-tecnicos/001-liberacao-segura-mesas.md`
