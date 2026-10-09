package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import java.math.BigDecimal;
import java.util.List;

public record ExtratoMesaResponse(
    Long sessaoId,
    Integer numeroMesa,
    StatusSessao statusSessao,
    BigDecimal valorTotalMesa,
    List<ExtratoSubcomandaResponse> subcomandas
) {}
