package ec.com.company.core.microservices.microserviciocalculodesahucio;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ec.com.company.core.microservices.microserviciocalculodesahucio.comparators.ResultadoCalculoComparator;
import ec.com.company.core.microservices.microserviciocalculodesahucio.dtos.response.ResultadoCalculo;
import ec.com.company.core.microservices.microserviciocalculodesahucio.models.MicroserviceResponse;
import ec.com.company.core.microservices.microserviciocalculodesahucio.utils.GsonFormatterUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Comparator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MicroservicioCalculoDesahucioApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @ParameterizedTest
    @CsvSource({
            "P111725_request.json, P111725_response.json", // Empresa nueva
            "P111463_request.json, P111463_response.json", // Empresa antigua
            "P111884_request.json, P111884_response.json", // Empresa antigua
            "P112091_request.json, P112091_response.json" // Empresa inicia operaciones
    }
    )
    public void testMicroserviceVscoreResults(String requestFilename, String responseFilename) throws Exception {
        byte[] requestContent = loadResourceContent(requestFilename);
        String microserviceResponse = performRequest(requestContent);

        ResultadoCalculo resultadoCalculocore = parsecoreResponseJson(loadResourceContentAsString(responseFilename));
        ResultadoCalculo resultadoCalculoMicroservicio = parseMicroserviceResponseJson(microserviceResponse);

        compareResultadosCalculo(resultadoCalculoMicroservicio, resultadoCalculocore);
    }

    // empresa con 1 activo debe tener obd actual 0

    private byte[] loadResourceContent(String filename) throws IOException {
        Resource resource = new ClassPathResource(filename);
        return Files.readAllBytes(resource.getFile().toPath());
    }

    private String loadResourceContentAsString(String filename) throws IOException {
        return new String(loadResourceContent(filename));
    }

    private String performRequest(byte[] requestContent) throws Exception {
        MvcResult microserviceResult = mockMvc.perform(post("/v1")
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return microserviceResult.getResponse().getContentAsString();
    }

    private ResultadoCalculo parseMicroserviceResponseJson(String jsonContent) {
        Gson gson = GsonFormatterUtil.getInstance().getGson();
        Type responseType = new TypeToken<MicroserviceResponse<ResultadoCalculo>>() {
        }.getType();
        MicroserviceResponse<?> microserviceResponse = gson.fromJson(jsonContent, responseType);

        Object content = microserviceResponse.getContent();
        if (content instanceof ResultadoCalculo) {
            return (ResultadoCalculo) content;
        } else {
            // Handle the case when content is not of type ResultadoCalculo
            throw new IllegalStateException("Unexpected content type");
        }
    }

    private ResultadoCalculo parsecoreResponseJson(String jsonContent) {
        Gson gson = GsonFormatterUtil.getInstance().getGson();
        return gson.fromJson(jsonContent, ResultadoCalculo.class);
    }

    private void compareResultadosCalculo(ResultadoCalculo resultadoCalculoMicroservicio, ResultadoCalculo resultadoCalculocore) {
        Comparator<ResultadoCalculo> comparator = new ResultadoCalculoComparator();
        int areEqual = comparator.compare(resultadoCalculoMicroservicio, resultadoCalculocore);
        Assertions.assertEquals(0, areEqual);
    }
}