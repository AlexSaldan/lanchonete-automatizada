# Migração do banco legado para Flyway

## Objetivo

Adaptar um banco MySQL legado, originalmente gerenciado pelo Hibernate, para uma estrutura compatível com as migrações Flyway V1, V2 e V3, preservando os dados existentes.

## Estado da validação

Procedimento testado em 30/10/2026 no banco de laboratório `lanchonete_script_teste`.

Resultados:
- Estrutura adaptada com sucesso.
- Compatibilidade validada pelo Hibernate (`ddl-auto=validate`).
- Baseline Flyway registrado na versão 3.
- Histórico validado pelo Flyway 12.4.0.
- Nove registros preservados nas cinco tabelas da aplicação.

## Arquivos

- `01_adaptar_schema_legado.sql`: adapta os tipos, as colunas de auditoria e os índices do esquema legado.
- `../../src/main/resources/db/migration/`: contém as migrações versionadas V1, V2 e V3.

## Procedimento seguro

1. Criar um backup completo do banco legado.
2. Restaurar o backup em um banco de laboratório separado.
3. Confirmar a integridade dos dados restaurados.
4. Revisar o esquema e confirmar que corresponde ao legado testado.
5. Executar `01_adaptar_schema_legado.sql` somente na cópia restaurada.
6. Comparar a estrutura adaptada com a estrutura criada pelas migrações Flyway V1-V3.
7. Validar a compatibilidade com Hibernate usando `ddl-auto=validate`.
8. Registrar explicitamente o baseline Flyway na versão 3.
9. Executar `flyway:validate`.
10. Conferir novamente os registros e a integridade dos dados.

## Cuidados obrigatórios

- Não executar o script diretamente no banco principal sem um plano de implantação e recuperação aprovado.
- O script não é idempotente: não deve ser executado duas vezes no mesmo banco.
- Não executar as migrações V1-V3 sobre as tabelas legadas existentes.
- Não habilitar `baselineOnMigrate` automaticamente.
- Não utilizar `flyway:clean` no banco principal.
- Não alterar os arquivos V1-V3 já versionados.
- Não incluir backups com dados reais, senhas ou tokens no Git.
- O baseline registra uma versão inicial; não substitui a validação da estrutura.
- O procedimento de laboratório não constitui autorização para migrar o banco principal.

## Próximas etapas

Antes de qualquer implantação fora do laboratório:
- Definir uma janela de manutenção e um plano de rollback.
- Revisar as configurações do Spring Boot, Hibernate e Flyway.
- Testar a inicialização completa da aplicação com Flyway habilitado.
- Repetir a validação em uma cópia atualizada do banco de destino.
