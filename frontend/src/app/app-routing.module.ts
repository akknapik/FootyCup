import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListComponent } from './components/user-list/user-list.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { MyTournamentsComponent } from './pages/tournament/my-tournaments/my-tournaments.component';
import { AddTournamentComponent } from './pages/tournament/add-tournament/add-tournament.component';
import { TournamentDetailsComponent } from './pages/tournament/tournament-details/tournament-details.component';
import { EditTournamentComponent } from './pages/tournament/edit-tournament/edit-tournament.component';
import { TeamsComponent } from './pages/team/teams/teams.component';
import { AddTeamComponent } from './pages/team/add-team/add-team.component';
import { TeamDetailsComponent } from './pages/team/team-details/team-details.component';
import { AddPlayerComponent } from './pages/team/add-player/add-player.component';
import { FormatComponent } from './pages/format/format.component';
import { MatchComponent } from './pages/match/match.component';
import { ScheduleComponent } from './pages/schedule/schedule.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'tournaments/my', component: MyTournamentsComponent, canActivate: [AuthGuard] },
  { path: 'tournaments/new', component: AddTournamentComponent, canActivate: [AuthGuard] },
  { path: 'tournaments/:tournamentId/edit', component: EditTournamentComponent, canActivate: [AuthGuard] },
  { path: 'tournaments/:tournamentId', component: TournamentDetailsComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/teams', component: TeamsComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/teams/new', component: AddTeamComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/teams/:teamId', component: TeamDetailsComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/teams/:teamId/add-player', component: AddPlayerComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/format', component: FormatComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/matches', component: MatchComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/schedule', component: ScheduleComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
