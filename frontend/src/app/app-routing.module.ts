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

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'tournaments/my', component: MyTournamentsComponent },
  { path: 'tournaments/new', component: AddTournamentComponent },
  { path: 'tournaments/:tournamentId/edit', component: EditTournamentComponent },
  { path: 'tournaments/:tournamentId', component: TournamentDetailsComponent },
  { path: 'tournament/:tournamentId/teams', component: TeamsComponent },
  { path: 'tournament/:tournamentId/teams/new', component: AddTeamComponent },
  { path: 'tournament/:tournamentId/teams/:teamId', component: TeamDetailsComponent },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
