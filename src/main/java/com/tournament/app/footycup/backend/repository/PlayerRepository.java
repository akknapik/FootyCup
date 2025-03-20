package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
