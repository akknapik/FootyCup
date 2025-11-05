import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { MyTournamentsComponent } from './pages/tournament/my-tournaments/my-tournaments.component';
import { AddTournamentComponent } from './pages/tournament/add-tournament/add-tournament.component';
import { TournamentDetailsComponent } from './pages/tournament/tournament-details/tournament-details.component';
import { TeamsComponent } from './pages/team/teams/teams.component';
import { AddTeamComponent } from './pages/team/add-team/add-team.component';
import { TeamDetailsComponent } from './pages/team/team-details/team-details.component';
import { AddPlayerComponent } from './pages/team/add-player/add-player.component';
import { FormatComponent } from './pages/format/format.component';
import { MatchComponent } from './pages/match/match.component';
import { ScheduleComponent } from './pages/schedule/schedule.component';
import { AuthGuard } from './guards/auth.guard';
import { ResultComponent } from './pages/result/result.component';
import { AdminUsersComponent } from './pages/admin-users/admin-users.component';
import { MatchEventsComponent } from './pages/match-events/match-events.component';
import { TacticsBoardComponent } from './pages/tactics-board/tactics-board.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';

const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'tournaments/my', component: MyTournamentsComponent, canActivate: [AuthGuard] },
  { path: 'tournaments/new', component: AddTournamentComponent, canActivate: [AuthGuard] },
  { path: 'tournaments/:tournamentId', component: TournamentDetailsComponent},
  { path: 'tournament/:tournamentId/teams', component: TeamsComponent},
  { path: 'tournament/:tournamentId/teams/new', component: AddTeamComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/teams/:teamId', component: TeamDetailsComponent},
  { path: 'tournament/:tournamentId/teams/:teamId/add-player', component: AddPlayerComponent, canActivate: [AuthGuard] },
  { path: 'tournament/:tournamentId/format', component: FormatComponent },
  { path: 'tournament/:tournamentId/matches', component: MatchComponent },
  { path: 'tournament/:tournamentId/matches/:matchId/events', component: MatchEventsComponent },
  { path: 'tournament/:tournamentId/matches/:matchId/tactics', component: TacticsBoardComponent },
  { path: 'tournament/:tournamentId/schedule', component: ScheduleComponent },
  { path: 'tournament/:tournamentId/results', component: ResultComponent },
  { path: 'admin/users', component: AdminUsersComponent, canActivate: [AuthGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
