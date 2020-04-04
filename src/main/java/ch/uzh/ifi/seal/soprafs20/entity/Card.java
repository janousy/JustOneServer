package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Card {

    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    private Game game;
}
