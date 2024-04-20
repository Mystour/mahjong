package org.example.mahjong.player;

import org.example.mahjong.score.Scorable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
public class Player implements Playable, Scorable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private int score;

    public Player() {}

    // getters and setters

    @Override
    public void drawTile() {}

    @Override
    public void discardTile() {}

    @Override
    public void declareChow() {}

    @Override
    public void declarePung() {}

    @Override
    public void declareKong() {}

    @Override
    public void declareMahjong() {}

    @Override
    public void calculateScore() {}
}