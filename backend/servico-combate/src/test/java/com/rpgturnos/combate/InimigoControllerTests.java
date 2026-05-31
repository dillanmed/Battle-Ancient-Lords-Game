package com.rpgturnos.combate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InimigoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarInimigosAtivosPorFaseComSpritesCoerentes() throws Exception {
        mockMvc.perform(get("/inimigos/fase/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[*].nome", containsInAnyOrder("Esqueleto", "Esqueleto Warrior", "Medusa")))
                .andExpect(jsonPath("$[*].ativo", everyItem(is(true))))
                .andExpect(jsonPath("$[?(@.nome == 'Esqueleto')].spriteKey", contains("esqueleto")))
                .andExpect(jsonPath("$[?(@.nome == 'Esqueleto Warrior')].spriteKey", contains("esqueleto warrior")))
                .andExpect(jsonPath("$[?(@.nome == 'Medusa')].spriteKey", contains("medusa")));
    }

    @Test
    void deveListarTodosInimigosAtivos() throws Exception {
        mockMvc.perform(get("/inimigos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(16)))
                .andExpect(jsonPath("$[*].ativo", everyItem(is(true))));
    }

    @Test
    void deveBuscarInimigoAtivoPorId() throws Exception {
        mockMvc.perform(get("/inimigos/fase/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Minotauro")))
                .andExpect(jsonPath("$[0].spriteKey", is("Minotauro")))
                .andExpect(jsonPath("$[0].boss", is(true)));
    }
}
