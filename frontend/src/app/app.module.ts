import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { FormsModule } from '@angular/forms';
import { MyTournamentsComponent } from './pages/tournament/my-tournaments/my-tournaments.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { AddTournamentComponent } from './pages/tournament/add-tournament/add-tournament.component';
import { TournamentDetailsComponent } from './pages/tournament/tournament-details/tournament-details.component';
import { TeamsComponent } from './pages/team/teams/teams.component';
import { AddTeamComponent } from './pages/team/add-team/add-team.component';
import { TeamDetailsComponent } from './pages/team/team-details/team-details.component';
import { AddPlayerComponent } from './pages/team/add-player/add-player.component';
import { FormatComponent } from './pages/format/format.component';
import { MatchComponent } from './pages/match/match.component';
import { ScheduleComponent } from './pages/schedule/schedule.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { RouterModule } from '@angular/router';
import { ErrorService } from './interceptors/error.service';
import { CommonModule } from '@angular/common';
import { ResultComponent } from './pages/result/result.component';
import { AdminUsersComponent } from './pages/admin-users/admin-users.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    MyTournamentsComponent,
    AddTournamentComponent,
    TournamentDetailsComponent,
    TeamsComponent,
    AddTeamComponent,
    TeamDetailsComponent,
    AddPlayerComponent,
    FormatComponent,
    MatchComponent,
    ScheduleComponent,
    ResultComponent,
    AdminUsersComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    DragDropModule,
    CommonModule,
    RouterModule,
    BrowserAnimationsModule,
    MatSnackBarModule
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true
  },
  {
    provide: HTTP_INTERCEPTORS,
    useClass: ErrorService,
    multi: true
}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
