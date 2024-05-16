package org.example.mahjong.controller;

import org.example.mahjong.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private SimpMessagingTemplate template;

    @Test
    @WithMockUser(username = "testUser")
    public void testCreateRoom() throws Exception {
        String roomCode = "testRoom";
        String username = "testUser";

        // Assume that the gameService always returns a non-null room code
        when(gameService.createRoom(roomCode, username)).thenReturn(roomCode);

        mockMvc.perform(post("/createRoom")
                        .param("roomCode", roomCode)
                        .param("username", username))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome/" + roomCode));
    }

    // Add more tests for other methods in GameController
}
