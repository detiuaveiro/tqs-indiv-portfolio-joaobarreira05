package pt.zeromonos.garbagecollection.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class GeoApiService {

    private static final Logger logger = LoggerFactory.getLogger(GeoApiService.class);
    private static final String GEOAPI_URL = "https://geoapi.pt/municipios";

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getMunicipalities() {
        try {
            // Por agora, para não bloquear o desenvolvimento, vamos usar uma lista fixa.
            // Mais tarde podemos implementar a lógica para extrair os dados da resposta real da API.
            List<String> municipalities = List.of("Lisboa", "Porto", "Coimbra", "Faro", "Braga", "Aveiro", "Sintra");
            logger.info("Using mock list of {} municipalities.", municipalities.size());
            return municipalities;

        } catch (Exception e) {
            logger.error("Failed to fetch municipalities from GeoAPI", e);
        }
        // Em caso de erro, retorna uma lista vazia para não quebrar a aplicação.
        return Collections.emptyList();
    }
}