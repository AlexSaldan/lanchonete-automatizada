package br.com.alexandrosaldan.lanchonete_automatizada.infrastructure.config;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuração responsável por popular o banco de dados com dados iniciais (seed).
 * Executado apenas em perfis de desenvolvimento ou teste para facilitar a avaliação da API.
 */
@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    /**
     * Bean que executa a inicialização de dados após o contexto da aplicação estar pronto.
     *
     * @param mesaRepository Repositório para verificar e salvar as mesas
     * @return CommandLineRunner configurado
     */
    @Bean
    @Profile({"dev", "default", "test"}) // Garante que rode no ambiente local, mas pode ser desativado em prod
    public CommandLineRunner initData(MesaRepository mesaRepository) {
        return args -> {
            // Verifica se o banco já possui dados para evitar duplicidade em reinicializações
            if (mesaRepository.count() == 0) {
                log.info("🌱 Banco de dados vazio. Iniciando carga de dados iniciais (Seed)...");
                
                mesaRepository.save(new Mesa(1, 2)); // Mesa 1: 2 lugares
                mesaRepository.save(new Mesa(2, 4)); // Mesa 2: 4 lugares
                mesaRepository.save(new Mesa(3, 6)); // Mesa 3: 6 lugares (familiar)
                mesaRepository.save(new Mesa(4, 2)); // Mesa 4: 2 lugares
                mesaRepository.save(new Mesa(5, 8)); // Mesa 5: 8 lugares (grande)

                log.info("✅ 5 mesas iniciais cadastradas com sucesso!");
            } else {
                log.info("ℹ️ Banco de dados já possui dados. Carga inicial ignorada.");
            }
        };
    }
}
