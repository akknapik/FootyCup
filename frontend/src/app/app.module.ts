import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { FormsModule } from '@angular/forms';
import { MyTournamentsComponent } from './pages/tournament/my-tournaments/my-tournaments.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { AddTournamentComponent } from './pages/tournament/add-tournament/add-tournament.component';
import { EditTournamentComponent } from './pages/tournament/edit-tournament/edit-tournament.component';
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


@NgModule({
  declarations: [
    AppComponent,
    UserListComponent,
    LoginComponent,
    RegisterComponent,
    MyTournamentsComponent,
    AddTournamentComponent,
    EditTournamentComponent,
    TournamentDetailsComponent,
    TeamsComponent,
    AddTeamComponent,
    TeamDetailsComponent,
    AddPlayerComponent,
    FormatComponent,
    MatchComponent,
    ScheduleComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    DragDropModule,
    RouterModule 
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
