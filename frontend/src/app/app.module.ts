import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { FormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { RouterModule, Routes } from "@angular/router";

import { AppComponent } from "./app.component";
import { LoginComponent } from "./components/login/login.component";
import { SignupComponent } from "./components/signup/signup.component";
import { StudentDashboardComponent } from "./components/dashboard/student/student-dashboard.component";
import { AuthorDashboardComponent } from "./components/dashboard/author/author-dashboard.component";
import { CommitteeDashboardComponent } from "./components/dashboard/committee/committee-dashboard.component";

const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "signup", component: SignupComponent },
  { path: "dashboard/student", component: StudentDashboardComponent },
  { path: "dashboard/author", component: AuthorDashboardComponent },
  { path: "dashboard/committee", component: CommitteeDashboardComponent },
  { path: "", redirectTo: "/login", pathMatch: "full" },
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    StudentDashboardComponent,
    AuthorDashboardComponent,
    CommitteeDashboardComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
