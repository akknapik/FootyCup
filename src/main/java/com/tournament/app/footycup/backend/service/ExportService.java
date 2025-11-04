package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.export.ExportDocument;
import com.tournament.app.footycup.backend.dto.match.MatchStatisticsResponse;
import com.tournament.app.footycup.backend.enums.ExportFormat;
import com.tournament.app.footycup.backend.enums.MatchEventType;
import com.tournament.app.footycup.backend.model.BracketNode;
import com.tournament.app.footycup.backend.model.Group;
import com.tournament.app.footycup.backend.model.GroupTeam;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.MatchEvent;
import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.BracketNodeRepository;
import com.tournament.app.footycup.backend.repository.GroupRepository;
import com.tournament.app.footycup.backend.repository.MatchEventRepository;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", DEFAULT_LOCALE);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", DEFAULT_LOCALE);
    private static final float PDF_MARGIN = 50f;
    private static final float PDF_FONT_SIZE = 12f;
    private static final float PDF_LEADING = 16f;

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final MatchEventService matchEventService;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final GroupRepository groupRepository;
    private final BracketNodeRepository bracketNodeRepository;
    private final AuthorizationService authorizationService;

    @Transactional(readOnly = true)
    public ExportDocument exportMatch(Long tournamentId, Long matchId, ExportFormat format, User requester) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        if (match.getTournament() == null || !match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }

        authorizationService.ensureCanViewMatch(match, requester);

        List<MatchEvent> events = new ArrayList<>(matchEventRepository.findByMatchIdOrderByMinuteDesc(match.getId()));
        events.sort(Comparator.comparing(MatchEvent::getMinute));

        MatchStatisticsResponse statistics = matchEventService.getStatistics(tournamentId, matchId, requester);

        Team homeTeam = reloadTeam(match.getTeamHome());
        Team awayTeam = reloadTeam(match.getTeamAway());
        List<Player> homePlayers = getSortedPlayers(homeTeam);
        List<Player> awayPlayers = getSortedPlayers(awayTeam);

        String baseFileName = buildMatchFileName(match);
        byte[] content = switch (format) {
            case PDF -> createPdf(buildMatchPdfLines(match, events, statistics, homePlayers, awayPlayers));
            case CSV -> buildMatchCsv(match, events, statistics, homePlayers, awayPlayers).getBytes();
        };

        return new ExportDocument(content, baseFileName + "." + format.fileExtension());
    }

    @Transactional(readOnly = true)
    public ExportDocument exportTournament(Long tournamentId, ExportFormat format, User requester) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        authorizationService.ensureCanViewTournament(tournament, requester);

        List<Team> teams = new ArrayList<>(teamRepository.findByTournamentId(tournamentId));
        teams.sort(Comparator.comparing(team -> StringUtils.hasText(team.getName()) ? team.getName() : "", String.CASE_INSENSITIVE_ORDER));

        List<Group> groups = new ArrayList<>(groupRepository.findByTournamentId(tournamentId));
        groups.sort(Comparator.comparing(group -> StringUtils.hasText(group.getName()) ? group.getName() : "", String.CASE_INSENSITIVE_ORDER));

        List<BracketNode> bracketNodes = new ArrayList<>(bracketNodeRepository.findByTournamentId(tournamentId));
        bracketNodes.sort(Comparator
                .comparing((BracketNode node) -> node.getRound() == null ? Integer.MAX_VALUE : node.getRound())
                .thenComparing(node -> node.getPosition() == null ? Integer.MAX_VALUE : node.getPosition()));

        List<Match> matches = new ArrayList<>(matchRepository.findByTournamentId(tournamentId));
        matches.sort(Comparator
                .comparing((Match m) -> m.getMatchDate() == null ? LocalDate.MAX : m.getMatchDate())
                .thenComparing(m -> m.getMatchTime() == null ? LocalTime.MAX : m.getMatchTime())
                .thenComparing(Match::getId));

        String baseFileName = buildTournamentFileName(tournament);
        byte[] content = switch (format) {
            case PDF -> createPdf(buildTournamentPdfLines(tournament, teams, groups, bracketNodes, matches));
            case CSV -> buildTournamentCsv(tournament, teams, groups, bracketNodes, matches).getBytes();
        };

        return new ExportDocument(content, baseFileName + "." + format.fileExtension());
    }

    private Team reloadTeam(Team team) {
        if (team == null || team.getId() == null) {
            return team;
        }
        return teamRepository.findById(team.getId()).orElse(team);
    }

    private List<Player> getSortedPlayers(Team team) {
        if (team == null) {
            return List.of();
        }
        List<Player> players = new ArrayList<>(team.getPlayerList());
        players.sort(Comparator
                .comparing((Player p) -> p.getNumber() == null ? Integer.MAX_VALUE : p.getNumber())
                .thenComparing(p -> StringUtils.hasText(p.getName()) ? p.getName() : "", String.CASE_INSENSITIVE_ORDER));
        return players;
    }

    private List<String> buildMatchPdfLines(Match match, List<MatchEvent> events, MatchStatisticsResponse statistics,
                                            List<Player> homePlayers, List<Player> awayPlayers) {
        List<String> lines = new ArrayList<>();
        lines.add("Match report");
        lines.add("");
        lines.add("Tournament: " + safeTournamentName(match.getTournament()));
        lines.add("Match: " + resolveMatchTitle(match));
        lines.add("Match ID: " + match.getId());
        lines.add("Date: " + formatDate(match.getMatchDate()));
        lines.add("Time: " + formatTime(match.getMatchTime()));
        lines.add("Status: " + safe(match.getStatus()));
        lines.add("Score: " + formatScore(match));
        if (match.getReferee() != null) {
            lines.add("Referee: " + formatUser(match.getReferee()));
        } else {
            lines.add("Referee: Not assigned");
        }
        lines.add("");
        lines.add("Events");
        if (events.isEmpty()) {
            lines.add("  No events recorded.");
        } else {
            for (MatchEvent event : events) {
                lines.add(formatEventLine(event));
            }
        }
        lines.add("");
        lines.add("Statistics");
        if (statistics != null) {
            lines.addAll(formatStatisticsSection(statistics));
        } else {
            lines.add("  Statistics are unavailable.");
        }
        lines.add("");
        lines.add("Players - " + safeTeamName(match.getTeamHome(), "Home"));
        if (homePlayers.isEmpty()) {
            lines.add("  No registered players.");
        } else {
            for (Player player : homePlayers) {
                lines.add("  " + formatPlayerLine(player));
            }
        }
        lines.add("");
        lines.add("Players - " + safeTeamName(match.getTeamAway(), "Away"));
        if (awayPlayers.isEmpty()) {
            lines.add("  No registered players.");
        } else {
            for (Player player : awayPlayers) {
                lines.add("  " + formatPlayerLine(player));
            }
        }
        return lines;
    }

    private String buildMatchCsv(Match match, List<MatchEvent> events, MatchStatisticsResponse statistics,
                                 List<Player> homePlayers, List<Player> awayPlayers) {
        StringBuilder sb = new StringBuilder();
        appendCsvLine(sb, "Section", "Field", "Value");
        appendCsvLine(sb, "Match", "Tournament", safeTournamentName(match.getTournament()));
        appendCsvLine(sb, "Match", "Match", resolveMatchTitle(match));
        appendCsvLine(sb, "Match", "Match ID", String.valueOf(match.getId()));
        appendCsvLine(sb, "Match", "Date", formatDate(match.getMatchDate()));
        appendCsvLine(sb, "Match", "Time", formatTime(match.getMatchTime()));
        appendCsvLine(sb, "Match", "Status", safe(match.getStatus()));
        appendCsvLine(sb, "Match", "Score", formatScore(match));
        appendCsvLine(sb, "Match", "Referee", match.getReferee() != null ? formatUser(match.getReferee()) : "Not assigned");
        sb.append('\n');

        appendCsvLine(sb, "Events", "Minute", "Type", "Team", "Player", "Secondary Player", "Description", "Recorded by");
        if (events.isEmpty()) {
            appendCsvLine(sb, "Events", "-", "-", "-", "-", "-", "-", "-");
        } else {
            for (MatchEvent event : events) {
                appendCsvLine(sb, "Events",
                        event.getMinute() != null ? event.getMinute().toString() : "-",
                        formatEnumName(event.getEventType()),
                        event.getTeam() != null ? safe(event.getTeam().getName()) : "-",
                        formatPlayerShort(event.getPlayer()),
                        formatPlayerShort(event.getSecondaryPlayer()),
                        safe(event.getDescription()),
                        event.getRecordedBy() != null ? formatUser(event.getRecordedBy()) : "-");
            }
        }
        sb.append('\n');

        appendCsvLine(sb, "Statistics", "Team", "Goals", "Yellow cards", "Red cards", "Substitutions", "Other events", "Total events");
        if (statistics != null && statistics.homeTeam() != null) {
            var home = statistics.homeTeam();
            appendCsvLine(sb, "Statistics", safeTeamName(match.getTeamHome(), "Home"),
                    String.valueOf(home.goals()),
                    String.valueOf(home.yellowCards()),
                    String.valueOf(home.redCards()),
                    String.valueOf(home.substitutions()),
                    String.valueOf(home.otherEvents()),
                    String.valueOf(home.totalEvents()));
        }
        if (statistics != null && statistics.awayTeam() != null) {
            var away = statistics.awayTeam();
            appendCsvLine(sb, "Statistics", safeTeamName(match.getTeamAway(), "Away"),
                    String.valueOf(away.goals()),
                    String.valueOf(away.yellowCards()),
                    String.valueOf(away.redCards()),
                    String.valueOf(away.substitutions()),
                    String.valueOf(away.otherEvents()),
                    String.valueOf(away.totalEvents()));
        }
        sb.append('\n');

        appendCsvLine(sb, "Players", "Team", "Number", "Name", "Birth date");
        if (homePlayers.isEmpty()) {
            appendCsvLine(sb, "Players", safeTeamName(match.getTeamHome(), "Home"), "-", "-", "-");
        } else {
            for (Player player : homePlayers) {
                appendCsvLine(sb, "Players", safeTeamName(match.getTeamHome(), "Home"),
                        player.getNumber() != null ? player.getNumber().toString() : "-",
                        safe(player.getName()),
                        formatDate(player.getBirthDate()));
            }
        }
        if (awayPlayers.isEmpty()) {
            appendCsvLine(sb, "Players", safeTeamName(match.getTeamAway(), "Away"), "-", "-", "-");
        } else {
            for (Player player : awayPlayers) {
                appendCsvLine(sb, "Players", safeTeamName(match.getTeamAway(), "Away"),
                        player.getNumber() != null ? player.getNumber().toString() : "-",
                        safe(player.getName()),
                        formatDate(player.getBirthDate()));
            }
        }
        return sb.toString();
    }

    private List<String> buildTournamentPdfLines(Tournament tournament, List<Team> teams, List<Group> groups,
                                                 List<BracketNode> bracketNodes, List<Match> matches) {
        List<String> lines = new ArrayList<>();
        lines.add("Tournament report");
        lines.add("");
        lines.add("Name: " + safe(tournament.getName()));
        lines.add("Tournament ID: " + tournament.getId());
        lines.add("Location: " + safe(tournament.getLocation()));
        lines.add("Dates: " + formatDate(tournament.getStartDate()) + " - " + formatDate(tournament.getEndDate()));
        lines.add("Status: " + safe(tournament.getStatus()));
        lines.add("System: " + safe(tournament.getSystem()));
        lines.add("Organizer: " + (tournament.getOrganizer() != null ? formatUser(tournament.getOrganizer()) : "N/A"));
        lines.add("Visibility: " + (tournament.isPublicVisible() ? "Public" : "Private"));
        if (!tournament.getReferees().isEmpty()) {
            lines.add("Referees: " + tournament.getReferees().stream()
                    .map(this::formatUser)
                    .collect(Collectors.joining(", ")));
        } else {
            lines.add("Referees: None");
        }
        lines.add("");

        lines.add("Teams");
        if (teams.isEmpty()) {
            lines.add("  No registered teams.");
        } else {
            for (Team team : teams) {
                lines.add("  " + safe(team.getName()) + formatTeamDetails(team));
                if (team.getPlayerList().isEmpty()) {
                    lines.add("    Players: none registered");
                } else {
                    lines.add("    Players:");
                    List<Player> players = getSortedPlayers(team);
                    for (Player player : players) {
                        lines.add("      " + formatPlayerLine(player));
                    }
                }
            }
        }
        lines.add("");

        lines.add("Groups");
        if (groups.isEmpty()) {
            lines.add("  No groups configured.");
        } else {
            for (Group group : groups) {
                lines.add("  " + safe(group.getName()));
                List<GroupTeam> standings = new ArrayList<>(group.getGroupTeams());
                standings.sort(Comparator
                        .comparing((GroupTeam gt) -> gt.getPosition() == null ? Integer.MAX_VALUE : gt.getPosition())
                        .thenComparing(gt -> gt.getTeam() != null && StringUtils.hasText(gt.getTeam().getName())
                                ? gt.getTeam().getName() : "", String.CASE_INSENSITIVE_ORDER));
                if (standings.isEmpty()) {
                    lines.add("    No teams assigned.");
                } else {
                    for (GroupTeam gt : standings) {
                        lines.add("    " + formatGroupTeamLine(gt));
                    }
                }
            }
        }
        lines.add("");

        lines.add("Bracket");
        if (bracketNodes.isEmpty()) {
            lines.add("  No bracket matches defined.");
        } else {
            Integer currentRound = null;
            for (BracketNode node : bracketNodes) {
                if (node.getRound() != null && !node.getRound().equals(currentRound)) {
                    currentRound = node.getRound();
                    lines.add("  Round " + currentRound);
                }
                lines.add("    " + formatBracketNode(node));
            }
        }
        lines.add("");

        lines.add("Matches");
        if (matches.isEmpty()) {
            lines.add("  No matches scheduled.");
        } else {
            for (Match match : matches) {
                lines.add("  " + formatMatchLine(match));
            }
        }
        return lines;
    }

    private String buildTournamentCsv(Tournament tournament, List<Team> teams, List<Group> groups,
                                      List<BracketNode> bracketNodes, List<Match> matches) {
        StringBuilder sb = new StringBuilder();
        appendCsvLine(sb, "Section", "Field", "Value");
        appendCsvLine(sb, "Overview", "Name", safe(tournament.getName()));
        appendCsvLine(sb, "Overview", "Tournament ID", String.valueOf(tournament.getId()));
        appendCsvLine(sb, "Overview", "Location", safe(tournament.getLocation()));
        appendCsvLine(sb, "Overview", "Start date", formatDate(tournament.getStartDate()));
        appendCsvLine(sb, "Overview", "End date", formatDate(tournament.getEndDate()));
        appendCsvLine(sb, "Overview", "Status", safe(tournament.getStatus()));
        appendCsvLine(sb, "Overview", "System", safe(tournament.getSystem()));
        appendCsvLine(sb, "Overview", "Organizer", tournament.getOrganizer() != null ? formatUser(tournament.getOrganizer()) : "N/A");
        appendCsvLine(sb, "Overview", "Visibility", tournament.isPublicVisible() ? "Public" : "Private");
        appendCsvLine(sb, "Overview", "Referees",
                tournament.getReferees().isEmpty()
                        ? "None"
                        : tournament.getReferees().stream().map(this::formatUser).collect(Collectors.joining("; ")));
        sb.append('\n');

        appendCsvLine(sb, "Teams", "Team", "Coach", "Country", "Players count");
        if (teams.isEmpty()) {
            appendCsvLine(sb, "Teams", "-", "-", "-", "0");
        } else {
            for (Team team : teams) {
                appendCsvLine(sb, "Teams",
                        safe(team.getName()),
                        team.getCoach() != null ? formatUser(team.getCoach()) : "N/A",
                        safe(team.getCountry()),
                        String.valueOf(team.getPlayerList().size()));
            }
        }
        sb.append('\n');

        appendCsvLine(sb, "Team players", "Team", "Number", "Name", "Birth date");
        if (teams.isEmpty()) {
            appendCsvLine(sb, "Team players", "-", "-", "-", "-");
        } else {
            for (Team team : teams) {
                List<Player> players = getSortedPlayers(team);
                if (players.isEmpty()) {
                    appendCsvLine(sb, "Team players", safe(team.getName()), "-", "-", "-");
                } else {
                    for (Player player : players) {
                        appendCsvLine(sb, "Team players", safe(team.getName()),
                                player.getNumber() != null ? player.getNumber().toString() : "-",
                                safe(player.getName()),
                                formatDate(player.getBirthDate()));
                    }
                }
            }
        }
        sb.append('\n');

        appendCsvLine(sb, "Groups", "Group", "Position", "Team", "Points", "Goals for", "Goals against");
        if (groups.isEmpty()) {
            appendCsvLine(sb, "Groups", "-", "-", "-", "-", "-", "-");
        } else {
            for (Group group : groups) {
                List<GroupTeam> standings = new ArrayList<>(group.getGroupTeams());
                standings.sort(Comparator
                        .comparing((GroupTeam gt) -> gt.getPosition() == null ? Integer.MAX_VALUE : gt.getPosition())
                        .thenComparing(gt -> gt.getTeam() != null && StringUtils.hasText(gt.getTeam().getName())
                                ? gt.getTeam().getName() : "", String.CASE_INSENSITIVE_ORDER));
                if (standings.isEmpty()) {
                    appendCsvLine(sb, "Groups", safe(group.getName()), "-", "-", "-", "-", "-");
                } else {
                    for (GroupTeam gt : standings) {
                        appendCsvLine(sb, "Groups", safe(group.getName()),
                                gt.getPosition() != null ? gt.getPosition().toString() : "-",
                                gt.getTeam() != null ? safe(gt.getTeam().getName()) : "-",
                                gt.getPoints() != null ? gt.getPoints().toString() : "0",
                                gt.getGoalsFor() != null ? gt.getGoalsFor().toString() : "0",
                                gt.getGoalsAgainst() != null ? gt.getGoalsAgainst().toString() : "0");
                    }
                }
            }
        }
        sb.append('\n');

        appendCsvLine(sb, "Bracket", "Round", "Position", "Match", "Home team", "Away team", "Score");
        if (bracketNodes.isEmpty()) {
            appendCsvLine(sb, "Bracket", "-", "-", "-", "-", "-", "-");
        } else {
            for (BracketNode node : bracketNodes) {
                Match match = node.getMatch();
                appendCsvLine(sb, "Bracket",
                        node.getRound() != null ? node.getRound().toString() : "-",
                        node.getPosition() != null ? node.getPosition().toString() : "-",
                        match != null ? resolveMatchTitle(match) : "-",
                        match != null ? safeTeamName(match.getTeamHome(), "Home") : "-",
                        match != null ? safeTeamName(match.getTeamAway(), "Away") : "-",
                        match != null ? formatScore(match) : "-");
            }
        }
        sb.append('\n');

        appendCsvLine(sb, "Matches", "Match ID", "Name", "Date", "Time", "Home team", "Away team", "Score", "Status", "Referee");
        if (matches.isEmpty()) {
            appendCsvLine(sb, "Matches", "-", "-", "-", "-", "-", "-", "-", "-", "-");
        } else {
            for (Match match : matches) {
                appendCsvLine(sb, "Matches",
                        String.valueOf(match.getId()),
                        resolveMatchTitle(match),
                        formatDate(match.getMatchDate()),
                        formatTime(match.getMatchTime()),
                        safeTeamName(match.getTeamHome(), "Home"),
                        safeTeamName(match.getTeamAway(), "Away"),
                        formatScore(match),
                        safe(match.getStatus()),
                        match.getReferee() != null ? formatUser(match.getReferee()) : "Not assigned");
            }
        }
        return sb.toString();
    }

    private byte[] createPdf(List<String> lines) {
        try (PDDocument document = new PDDocument()) {
            PDFont font = PDType1Font.HELVETICA;
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(font, PDF_FONT_SIZE);
            contentStream.setLeading(PDF_LEADING);
            float width = page.getMediaBox().getWidth() - 2 * PDF_MARGIN;
            float yPosition = page.getMediaBox().getHeight() - PDF_MARGIN;
            contentStream.beginText();
            contentStream.newLineAtOffset(PDF_MARGIN, yPosition);

            for (String line : lines) {
                List<String> wrapped = wrapLine(line, font, PDF_FONT_SIZE, width);
                if (wrapped.isEmpty()) {
                    wrapped = List.of("");
                }
                for (String segment : wrapped) {
                    if (yPosition <= PDF_MARGIN) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(font, PDF_FONT_SIZE);
                        contentStream.setLeading(PDF_LEADING);
                        yPosition = page.getMediaBox().getHeight() - PDF_MARGIN;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(PDF_MARGIN, yPosition);
                    }
                    if (!segment.isEmpty()) {
                        contentStream.showText(segment);
                    }
                    contentStream.newLine();
                    yPosition -= PDF_LEADING;
                }
            }

            contentStream.endText();
            contentStream.close();

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                document.save(outputStream);
                return outputStream.toByteArray();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to generate PDF export", ex);
        }
    }

    private List<String> wrapLine(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> segments = new ArrayList<>();
        if (!StringUtils.hasText(text)) {
            segments.add("");
            return segments;
        }
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (!StringUtils.hasText(word)) {
                continue;
            }
            String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (getStringWidth(font, candidate, fontSize) <= maxWidth) {
                currentLine = new StringBuilder(candidate);
            } else {
                if (currentLine.length() > 0) {
                    segments.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    segments.addAll(forceWrapWord(word, font, fontSize, maxWidth));
                    currentLine = new StringBuilder();
                }
            }
        }
        if (currentLine.length() > 0) {
            segments.add(currentLine.toString());
        }
        return segments;
    }

    private List<String> forceWrapWord(String word, PDFont font, float fontSize, float maxWidth) throws IOException {
        List<String> pieces = new ArrayList<>();
        int start = 0;
        while (start < word.length()) {
            int end = word.length();
            while (end > start) {
                String part = word.substring(start, end);
                if (getStringWidth(font, part, fontSize) <= maxWidth) {
                    pieces.add(part);
                    start = end;
                    break;
                }
                end--;
            }
            if (end == start) {
                end = Math.min(start + 1, word.length());
                pieces.add(word.substring(start, end));
                start = end;
            }
        }
        return pieces;
    }

    private float getStringWidth(PDFont font, String text, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    private void appendCsvLine(StringBuilder sb, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            String value = values[i] == null ? "" : values[i];
            sb.append('"').append(value.replace("\"", "\"\""))
                    .append('"');
        }
        sb.append('\n');
    }

    private String formatEventLine(MatchEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("  ");
        builder.append(event.getMinute() != null ? event.getMinute() : "-").append("' ");
        builder.append(formatEnumName(event.getEventType()));
        builder.append(" - ");
        builder.append(event.getTeam() != null ? safe(event.getTeam().getName()) : "Unknown team");
        if (event.getEventType() == MatchEventType.SUBSTITUTION) {
            builder.append(" | ");
            builder.append(event.getSecondaryPlayer() != null ? "↓ " + formatPlayerShort(event.getSecondaryPlayer()) : "");
            if (event.getPlayer() != null && event.getSecondaryPlayer() != null) {
                builder.append(" / ");
            }
            builder.append(event.getPlayer() != null ? "↑ " + formatPlayerShort(event.getPlayer()) : "");
        } else if (event.getPlayer() != null) {
            builder.append(" | ");
            builder.append(formatPlayerShort(event.getPlayer()));
        }
        if (StringUtils.hasText(event.getDescription())) {
            builder.append(" | ").append(event.getDescription());
        }
        if (event.getRecordedBy() != null) {
            builder.append(" | Recorded by: ").append(formatUser(event.getRecordedBy()));
        }
        return builder.toString();
    }

    private List<String> formatStatisticsSection(MatchStatisticsResponse statistics) {
        List<String> lines = new ArrayList<>();
        if (statistics.homeTeam() != null) {
            lines.add("  " + safe(statistics.homeTeam().teamName()) + ": " + statistics.homeTeam().goals() + " goals, "
                    + statistics.homeTeam().yellowCards() + " yellow, "
                    + statistics.homeTeam().redCards() + " red, "
                    + statistics.homeTeam().substitutions() + " substitutions, "
                    + statistics.homeTeam().otherEvents() + " other");
        }
        if (statistics.awayTeam() != null) {
            lines.add("  " + safe(statistics.awayTeam().teamName()) + ": " + statistics.awayTeam().goals() + " goals, "
                    + statistics.awayTeam().yellowCards() + " yellow, "
                    + statistics.awayTeam().redCards() + " red, "
                    + statistics.awayTeam().substitutions() + " substitutions, "
                    + statistics.awayTeam().otherEvents() + " other");
        }
        if (lines.isEmpty()) {
            lines.add("  Statistics are unavailable.");
        }
        return lines;
    }

    private String formatPlayerLine(Player player) {
        return "#" + (player.getNumber() != null ? player.getNumber() : "-") + " " + safe(player.getName())
                + " (DOB: " + formatDate(player.getBirthDate()) + ")";
    }

    private String formatTeamDetails(Team team) {
        List<String> parts = new ArrayList<>();
        if (team.getCoach() != null) {
            parts.add("Coach: " + formatUser(team.getCoach()));
        }
        if (StringUtils.hasText(team.getCountry())) {
            parts.add("Country: " + team.getCountry());
        }
        return parts.isEmpty() ? "" : " (" + String.join(", ", parts) + ")";
    }

    private String formatGroupTeamLine(GroupTeam groupTeam) {
        String teamName = groupTeam.getTeam() != null ? safe(groupTeam.getTeam().getName()) : "Unknown team";
        return String.format(Locale.ROOT, "%s. %s - %d pts (GF %d / GA %d)",
                groupTeam.getPosition() != null ? groupTeam.getPosition() : 0,
                teamName,
                groupTeam.getPoints() != null ? groupTeam.getPoints() : 0,
                groupTeam.getGoalsFor() != null ? groupTeam.getGoalsFor() : 0,
                groupTeam.getGoalsAgainst() != null ? groupTeam.getGoalsAgainst() : 0);
    }

    private String formatBracketNode(BracketNode node) {
        Match match = node.getMatch();
        if (match == null) {
            return "Match TBD";
        }
        return resolveMatchTitle(match) + " | Score: " + formatScore(match);
    }

    private String formatMatchLine(Match match) {
        List<String> parts = new ArrayList<>();
        parts.add("ID " + match.getId());
        parts.add(resolveMatchTitle(match));
        parts.add(formatDate(match.getMatchDate()));
        parts.add(formatTime(match.getMatchTime()));
        parts.add("Score " + formatScore(match));
        parts.add("Status " + safe(match.getStatus()));
        if (match.getReferee() != null) {
            parts.add("Referee " + formatUser(match.getReferee()));
        }
        if (match.getGroup() != null && StringUtils.hasText(match.getGroup().getName())) {
            parts.add("Group " + match.getGroup().getName());
        }
        return "  " + String.join(" | ", parts);
    }

    private String formatScore(Match match) {
        String home = match.getHomeScore() != null ? match.getHomeScore().toString() : "-";
        String away = match.getAwayScore() != null ? match.getAwayScore().toString() : "-";
        return home + " : " + away;
    }

    private String formatPlayerShort(Player player) {
        if (player == null) {
            return "-";
        }
        if (player.getNumber() != null) {
            return "#" + player.getNumber() + " " + safe(player.getName());
        }
        return safe(player.getName());
    }

    private String formatEnumName(Enum<?> value) {
        if (value == null) {
            return "-";
        }
        String lower = value.name().toLowerCase(DEFAULT_LOCALE).replace('_', ' ');
        return StringUtils.capitalize(lower);
    }

    private String safe(Object value) {
        return value == null ? "-" : value.toString();
    }

    private String safeTeamName(Team team, String fallback) {
        if (team == null || !StringUtils.hasText(team.getName())) {
            return fallback;
        }
        return team.getName();
    }

    private String safeTournamentName(Tournament tournament) {
        if (tournament == null || !StringUtils.hasText(tournament.getName())) {
            return "Unknown tournament";
        }
        return tournament.getName();
    }

    private String formatUser(User user) {
        if (user == null) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(user.getFirstname()) || StringUtils.hasText(user.getLastname())) {
            builder.append(StringUtils.trimWhitespace((user.getFirstname() != null ? user.getFirstname() : "")
                    + " " + (user.getLastname() != null ? user.getLastname() : "")));
        }
        if (StringUtils.hasText(user.getEmail())) {
            if (builder.length() > 0) {
                builder.append(" <").append(user.getEmail()).append('>');
            } else {
                builder.append(user.getEmail());
            }
        }
        return builder.length() == 0 ? "User " + user.getId() : builder.toString();
    }

    private String resolveMatchTitle(Match match) {
        if (match == null) {
            return "Match";
        }
        if (StringUtils.hasText(match.getName())) {
            return match.getName();
        }
        return safeTeamName(match.getTeamHome(), "Home") + " vs " + safeTeamName(match.getTeamAway(), "Away");
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return date.format(DATE_FORMATTER);
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            return "-";
        }
        return time.format(TIME_FORMATTER);
    }

    private String buildMatchFileName(Match match) {
        String base = sanitize(resolveMatchTitle(match));
        return "match-" + match.getId() + (base.isEmpty() ? "" : "-" + base);
    }

    private String buildTournamentFileName(Tournament tournament) {
        String base = sanitize(tournament.getName());
        return "tournament-" + tournament.getId() + (base.isEmpty() ? "" : "-" + base);
    }

    private String sanitize(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String sanitized = normalized.replaceAll("[^A-Za-z0-9]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return sanitized.toLowerCase(Locale.ROOT);
    }
}