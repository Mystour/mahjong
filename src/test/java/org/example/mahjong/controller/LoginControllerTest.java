package org.example.mahjong.controller;

import org.example.mahjong.player.PlayerInfo;
import org.example.mahjong.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "testUser")
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }
}
