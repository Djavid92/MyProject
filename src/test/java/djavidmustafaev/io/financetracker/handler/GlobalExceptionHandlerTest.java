package djavidmustafaev.io.financetracker.handler;

import org.hibernate.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn400_onBusinessException() throws Exception {
        mockMvc.perform(get("/test/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("бизнес ошибка"));
    }

    @Test
    void shouldReturn400_onIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn400_onDataIntegrityViolationException() throws Exception {
        mockMvc.perform(get("/test/integrity"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка целостности данных"));
    }

    @Test
    void shouldReturn500_onDataAccessException() throws Exception {
        mockMvc.perform(get("/test/dataaccess"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ошибка при работе с базой данных"));
    }

    @Test
    void shouldReturn500_onMappingException() throws Exception {
        mockMvc.perform(get("/test/mapping"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ошибка преобразования данных"));
    }

    @Test
    void shouldReturn500_onNullPointerException() throws Exception {
        mockMvc.perform(get("/test/npe"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Внутренняя ошибка: обнаружены null значения"));
    }

    @Test
    void shouldReturn500_onUnexpectedException() throws Exception {
        mockMvc.perform(get("/test/unknown"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Внутренняя ошибка сервиса"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/business")
        public void business() { throw new BusinessException("бизнес ошибка"); }

        @GetMapping("/test/illegal")
        public void illegal() { throw new IllegalArgumentException("плохой аргумент"); }

        @GetMapping("/test/integrity")
        public void integrity() { throw new DataIntegrityViolationException("constraint"); }

        @GetMapping("/test/dataaccess")
        public void dataAccess() { throw new DataAccessException("db error") {}; }

        @GetMapping("/test/mapping")
        public void mapping() { throw new MappingException("mapping error"); }

        @GetMapping("/test/npe")
        public void npe() { throw new NullPointerException(); }

        @GetMapping("/test/unknown")
        public void unknown() { throw new RuntimeException("unexpected"); }
    }
}
