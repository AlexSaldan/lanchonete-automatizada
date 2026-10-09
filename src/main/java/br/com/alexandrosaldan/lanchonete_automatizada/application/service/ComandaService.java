package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.*;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.*;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.*;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service responsável pela gestão de Comandas Compartilhadas,
 * Subcomandas e Lançamento de Itens.
 *
 * Regras de negócio:
 * - Garante atomicidade das operações com @Transactional.
 * - Valida o token QR Code na abertura da sessão.
 * - Impede lançamentos em sessões que não estejam abertas.
 * - Impede lançamentos em subcomandas cujo pagamento não esteja pendente.
 */
@Service
public class ComandaService {

    private static final Logger log =
            LoggerFactory.getLogger(ComandaService.class);

    private final MesaRepository mesaRepository;
    private final SessaoMesaRepository sessaoMesaRepository;
    private final SubcomandaRepository subcomandaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ComandaService(
            MesaRepository mesaRepository,
            SessaoMesaRepository sessaoMesaRepository,
            SubcomandaRepository subcomandaRepository,
            ProdutoRepository produtoRepository,
            ItemPedidoRepository itemPedidoRepository) {

        this.mesaRepository = mesaRepository;
        this.sessaoMesaRepository = sessaoMesaRepository;
        this.subcomandaRepository = subcomandaRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * Abre uma nova sessão em uma mesa disponível
     * e cria a primeira subcomanda individual.
     */
    @Transactional
    public ExtratoMesaResponse abrirSessao(AberturaSessaoRequest request) {

        log.info(
                "Tentativa de abertura de sessão para a mesa número: {}",
                request.numeroMesa()
        );

        Mesa mesa = mesaRepository.findByNumero(request.numeroMesa())
                .orElseThrow(() -> new MesaNaoEncontradaException(
                        "Mesa " + request.numeroMesa() + " não encontrada."
                ));

        if (!mesa.getTokenQrCode().equals(request.tokenQrCode())) {

            log.warn(
                    "Token QR Code inválido enviado para a mesa {}",
                    request.numeroMesa()
            );

            throw new IllegalArgumentException(
                    "Token do QR Code inválido para esta mesa."
            );
        }

        if (mesa.getStatus() == StatusMesa.OCUPADA) {
            throw new IllegalStateException(
                    "A mesa já possui uma sessão aberta. " +
                    "Adicione o cliente à sessão existente."
            );
        }

        mesa.setStatus(StatusMesa.OCUPADA);
        mesaRepository.save(mesa);

        SessaoMesa sessao = new SessaoMesa(mesa);

        Subcomanda subcomandaInicial = new Subcomanda(
                request.nomeCliente(),
                request.cpfCliente()
        );

        sessao.adicionarSubcomanda(subcomandaInicial);

        SessaoMesa sessaoSalva = sessaoMesaRepository.save(sessao);

        log.info(
                "Sessão ID {} aberta com sucesso para a mesa {}",
                sessaoSalva.getId(),
                mesa.getNumero()
        );

        return buscarExtratoMesa(sessaoSalva.getId());
    }

    /**
     * Adiciona um novo cliente a uma sessão existente.
     */
    @Transactional
    public ExtratoSubcomandaResponse adicionarCliente(
            AdicionarClienteRequest request) {

        log.info(
                "Adicionando cliente '{}' à sessão ID {}",
                request.nomeCliente(),
                request.sessaoId()
        );

        SessaoMesa sessao = sessaoMesaRepository.findById(request.sessaoId())
                .orElseThrow(() -> new SessaoNaoEncontradaException(
                        "Sessão não encontrada com ID: " + request.sessaoId()
                ));

        if (sessao.getStatus() != StatusSessao.ABERTA) {
            throw new IllegalStateException(
                    "Não é possível adicionar clientes a uma sessão " +
                    "encerrada ou em fechamento."
            );
        }

        Subcomanda novaSubcomanda = new Subcomanda(
                request.nomeCliente(),
                request.cpfCliente()
        );

        sessao.adicionarSubcomanda(novaSubcomanda);

        SessaoMesa sessaoSalva = sessaoMesaRepository.save(sessao);

        Subcomanda salva = sessaoSalva.getSubcomandas().getLast();

        return mapearSubcomandaParaResponse(salva);
    }

    /**
     * Adiciona um produto à subcomanda do cliente.
     *
     * Regras:
     * - A sessão deve estar ABERTA.
     * - O pagamento da subcomanda deve estar PENDENTE.
     * - O produto deve estar disponível.
     */
    @Transactional
    public ItemPedidoResponse lancarItem(ItemPedidoRequest request) {

        log.info(
                "Lançando produto ID {} na subcomanda ID {}",
                request.produtoId(),
                request.subcomandaId()
        );

        Subcomanda subcomanda = subcomandaRepository
                .findById(request.subcomandaId())
                .orElseThrow(() -> new SubcomandaNaoEncontradaException(
                        "Subcomanda não encontrada."
                ));

        // Regra 1: a sessão precisa estar aberta.
        if (subcomanda.getSessaoMesa().getStatus() != StatusSessao.ABERTA) {
            throw new IllegalStateException(
                    "Não é possível lançar itens em uma sessão " +
                    "que não está aberta."
            );
        }

        // Regra 2: a subcomanda precisa estar pendente de pagamento.
        if (subcomanda.getStatusPagamento() != StatusPagamento.PENDENTE) {
            throw new IllegalStateException(
                    "Não é possível lançar itens em uma subcomanda " +
                    "que não está pendente de pagamento."
            );
        }

        Produto produto = produtoRepository
                .findById(request.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(
                        "Produto não encontrado."
                ));

        // Regra 3: o produto precisa estar disponível.
        if (!produto.getDisponivel()) {
            throw new IllegalStateException(
                    "O produto '" + produto.getNome() +
                    "' não está disponível no momento."
            );
        }

        ItemPedido item = new ItemPedido(
                subcomanda,
                produto,
                request.quantidade(),
                request.observacao()
        );

        ItemPedido itemSalvo = itemPedidoRepository.save(item);

        // Atualiza o valor total da subcomanda.
        BigDecimal novoTotal = subcomanda
                .getValorTotal()
                .add(itemSalvo.getSubtotal());

        subcomanda.setValorTotal(novoTotal);

        subcomandaRepository.save(subcomanda);

        return new ItemPedidoResponse(
                itemSalvo.getId(),
                produto.getNome(),
                itemSalvo.getQuantidade(),
                itemSalvo.getPrecoUnitario(),
                itemSalvo.getSubtotal(),
                itemSalvo.getObservacao(),
                itemSalvo.getStatusPreparo()
        );
    }

    /**
     * Obtém o extrato completo da sessão,
     * incluindo as subcomandas e seus itens.
     */
    @Transactional(readOnly = true)
    public ExtratoMesaResponse buscarExtratoMesa(Long sessaoId) {

        SessaoMesa sessao = sessaoMesaRepository.findById(sessaoId)
                .orElseThrow(() -> new SessaoNaoEncontradaException(
                        "Sessão não encontrada com ID: " + sessaoId
                ));

        List<ExtratoSubcomandaResponse> subcomandasDto = sessao
                .getSubcomandas()
                .stream()
                .map(this::mapearSubcomandaParaResponse)
                .toList();

        BigDecimal totalGeralMesa = subcomandasDto
                .stream()
                .map(ExtratoSubcomandaResponse::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ExtratoMesaResponse(
                sessao.getId(),
                sessao.getMesa().getNumero(),
                sessao.getStatus(),
                totalGeralMesa,
                subcomandasDto
        );
    }

    /**
     * Converte uma subcomanda em DTO de resposta,
     * incluindo seus itens.
     */
    private ExtratoSubcomandaResponse mapearSubcomandaParaResponse(
            Subcomanda subcomanda) {

        List<ItemPedido> itens = itemPedidoRepository
                .findBySubcomandaId(subcomanda.getId());

        List<ItemPedidoResponse> itensDto = itens
                .stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal(),
                        item.getObservacao(),
                        item.getStatusPreparo()
                ))
                .toList();

        return new ExtratoSubcomandaResponse(
                subcomanda.getId(),
                subcomanda.getNomeCliente(),
                subcomanda.getStatusPagamento(),
                subcomanda.getValorTotal(),
                itensDto
        );
    }
}
