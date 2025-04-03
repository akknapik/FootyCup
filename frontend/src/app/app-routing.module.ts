import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListComponent } from './components/user-list/user-list.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { MyTournamentsComponent } from './pages/my-tournaments/my-tournaments.component';
import { AddTournamentComponent } from './pages/add-tournament/add-tournament.component';
import { EditTournamentComponent } from './pages/edit-tournament/edit-tournament.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'tournaments/my', component: MyTournamentsComponent },
  { path: 'tournaments/new', component: AddTournamentComponent },
  { path: 'tournaments/:id/edit', component: EditTournamentComponent },
  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
