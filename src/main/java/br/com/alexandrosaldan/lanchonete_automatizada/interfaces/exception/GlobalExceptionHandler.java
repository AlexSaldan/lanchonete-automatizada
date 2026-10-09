package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.exception;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.ProdutoNaoEncontradoException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.SessaoNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.SubcomandaNaoEncontradaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler global de exceções da API REST.
 * 
 * Boas Práticas e Segurança:
 * - Centraliza o tratamento de erros HTTP seguindo o padrão de respostas padronizadas (RFC-7807/Custom JSON).
 * - Oculta stack traces e detalhes técnicos internos contra exposição indevida (Information Disclosure).
 * - Mapeia exceções de domínio específicas para status HTTP semânticos (404, 400, 409, 500).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MesaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleMesaNaoEncontrada(MesaNaoEncontradaException ex) {
        log.warn("Recurso Mesa não encontrado: {}", ex.getMessage());
        return construirResposta(HttpStatus.NOT_FOUND, "Recurso Não Encontrado", ex.getMessage());
    }

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProdutoNaoEncontrado(ProdutoNaoEncontradoException ex) {
        log.warn("Recurso Produto não encontrado: {}", ex.getMessage());
        return construirResposta(HttpStatus.NOT_FOUND, "Recurso Não Encontrado", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        log.warn("Violação de estado de negócio: {}", ex.getMessage());
        return construirResposta(HttpStatus.CONFLICT, "Conflito de Regra de Negócio", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> messages = new ArrayList<>();
        
        ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .forEach(messages::add);
                
        ex.getBindingResult().getGlobalErrors().stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : error.getObjectName())
                .forEach(messages::add);

        log.warn("Falha de validação Bean Validation na requisição: {}", messages);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Requisição Inválida");
        body.put("message", "Um ou mais campos estão inválidos");
        body.put("messages", messages);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Erro ao deserializar corpo da requisição JSON: {}", ex.getMessage());
        return construirResposta(
            HttpStatus.BAD_REQUEST, 
            "Requisição Inválida", 
            "O corpo da requisição (JSON) está malformado ou possui tipos de dados inválidos."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Exceção não tratada capturada pelo Handler Global: ", ex);
        return construirResposta(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Erro Interno do Servidor", 
            "Ocorreu um erro inesperado. Por favor, entre em contato com o suporte ou verifique os logs."
        );
    }

    private String formatFieldError(FieldError error) {
        String detail = error.getDefaultMessage() != null ? error.getDefaultMessage() : "valor inválido";
        return error.getField() + ": " + detail;
    }

    private ResponseEntity<Map<String, Object>> construirResposta(HttpStatus status, String erro, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", erro);
        body.put("message", mensagem);
        return new ResponseEntity<>(body, status);
    }
    
    @ExceptionHandler(SessaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSessaoNaoEncontrada(SessaoNaoEncontradaException ex) {
        log.warn("Sessão não encontrada: {}", ex.getMessage());
        return construirResposta(HttpStatus.NOT_FOUND, "Sessão Não Encontrada", ex.getMessage());
    }

    @ExceptionHandler(SubcomandaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSubcomandaNaoEncontrada(SubcomandaNaoEncontradaException ex) {
        log.warn("Subcomanda não encontrada: {}", ex.getMessage());
        return construirResposta(HttpStatus.NOT_FOUND, "Subcomanda Não Encontrada", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento inválido enviado: {}", ex.getMessage());
        return construirResposta(HttpStatus.BAD_REQUEST, "Requisição Inválida", ex.getMessage());
    }
}
