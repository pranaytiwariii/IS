import { Component } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-signup",
  templateUrl: "./signup.component.html",
  styleUrls: ["./signup.component.css"],
})
export class SignupComponent {
  form: any = {
    username: null,
    email: null,
    password: null,
    role: "STUDENT",
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = "";

  roles = [
    { value: "STUDENT", label: "Student - Search and View Papers" },
    { value: "AUTHOR", label: "Author - Write and Submit Papers" },
    { value: "COMMITTEE", label: "Committee - Review and Publish Papers" },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {}

  onSubmit(): void {
    const { username, email, password, role } = this.form;

    // Validate form inputs
    if (
      !username ||
      !email ||
      !password ||
      !role ||
      username.trim() === "" ||
      email.trim() === "" ||
      password.trim() === ""
    ) {
      this.errorMessage = "All fields are required";
      this.isSignUpFailed = true;
      return;
    }

    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      this.errorMessage = "Please enter a valid email address";
      this.isSignUpFailed = true;
      return;
    }

    this.authService.register(username, email, password, role).subscribe({
      next: (data) => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        alert(`${role} registered successfully! Please login to continue.`);
        setTimeout(() => {
          this.router.navigate(["/login"]);
        }, 2000);
      },
      error: (err) => {
        console.error("Signup error:", err);
        this.errorMessage =
          err.error?.message || "Registration failed. Please try again.";
        this.isSignUpFailed = true;
      },
    });
  }
}
