import { Component, OnInit } from "@angular/core";
import { AuthService, Paper } from "../../../services/auth.service";

@Component({
  selector: "app-author-dashboard",
  templateUrl: "./author-dashboard.component.html",
  styleUrls: ["./author-dashboard.component.css"],
})
export class AuthorDashboardComponent implements OnInit {
  myPapers: Paper[] = [];
  showCreateForm: boolean = false;
  loading: boolean = false;

  newPaper = {
    title: "",
    abstract: "",
    content: "",
    tags: "",
  };

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadMyPapers();
  }

  loadMyPapers() {
    this.loading = true;
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      console.error("No user logged in");
      this.loading = false;
      return;
    }

    this.authService.getMyPapers(currentUser.username).subscribe({
      next: (papers) => {
        this.myPapers = papers;
        this.loading = false;
      },
      error: (err) => {
        console.error("Error loading papers:", err);
        this.loading = false;
      },
    });
  }

  toggleCreateForm() {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.resetForm();
    }
  }

  createPaper() {
    if (this.newPaper.title && this.newPaper.abstract) {
      const currentUser = this.authService.getCurrentUser();
      if (!currentUser) {
        alert("Error: No user logged in");
        return;
      }

      const paperData = {
        ...this.newPaper,
        tags: this.newPaper.tags
          .split(",")
          .map((tag) => tag.trim())
          .filter((tag) => tag),
      };

      this.authService.createPaper(paperData, currentUser.username).subscribe({
        next: (response) => {
          alert(
            "Paper created successfully! This demonstrates One-to-Many relationship (Author → Papers)"
          );
          this.resetForm();
          this.showCreateForm = false;
          this.loadMyPapers();
        },
        error: (err) => {
          alert("Error creating paper: " + err.error.message);
        },
      });
    } else {
      alert("Please fill in required fields (Title and Abstract)");
    }
  }

  resetForm() {
    this.newPaper = {
      title: "",
      abstract: "",
      content: "",
      tags: "",
    };
  }

  logout() {
    this.authService.logout();
    window.location.href = "/login";
  }
}
