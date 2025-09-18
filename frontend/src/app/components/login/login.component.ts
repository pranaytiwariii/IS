import { Component } from "@angular/core";
import { AuthService, User } from "../../services/auth.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
})
export class LoginComponent {
  form: any = {
    username: null,
    password: null,
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = "";

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {}

  onSubmit(): void {
    const { username, password } = this.form;

    // Validate form inputs
    if (
      !username ||
      !password ||
      username.trim() === "" ||
      password.trim() === ""
    ) {
      this.errorMessage = "Username and password are required";
      this.isLoginFailed = true;
      return;
    }

    this.authService.login(username, password).subscribe({
      next: (data) => {
        console.log(data);
        this.isLoginFailed = false;
        this.isLoggedIn = true;

        // Extract user information from the login response
        const user: User = {
          username: data.username,
          email: data.email,
          role: data.role,
        };

        this.authService.setCurrentUser(user);

        alert(`Login successful! Welcome ${user.role}!`);

        // Redirect based on role (Inheritance pattern demonstration)
        this.redirectByRole(user.role || "STUDENT");
      },
      error: (err) => {
        console.error("Login error:", err);
        this.errorMessage =
          err.error?.message || "Login failed. Please try again.";
        this.isLoginFailed = true;
      },
    });
  }

  private redirectByRole(role: string) {
    switch (role) {
      case "STUDENT":
        this.router.navigate(["/dashboard/student"]);
        break;
      case "AUTHOR":
        this.router.navigate(["/dashboard/author"]);
        break;
      case "COMMITTEE":
        this.router.navigate(["/dashboard/committee"]);
        break;
      default:
        this.router.navigate(["/dashboard"]);
    }
  }

  reloadPage(): void {
    window.location.reload();
  }
}
