package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Tournament;

public interface IStructureGenerationService {
    void generateGroupStructure(Tournament tournament, int groupCount, int teamsPerGroup);
    void generateBracketStructure(Tournament tournament, int totalTeams);
    void generateMixedStructure(Tournament tournament, int groupCount, int teamsPerGroup, int advancing);
}
