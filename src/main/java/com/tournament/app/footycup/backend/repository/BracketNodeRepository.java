package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.BracketNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BracketNodeRepository extends JpaRepository<BracketNode, Long> {
}
