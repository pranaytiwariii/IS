import { Component, OnInit } from "@angular/core";
import { AuthService, Paper } from "../../../services/auth.service";

@Component({
  selector: "app-student-dashboard",
  templateUrl: "./student-dashboard.component.html",
  styleUrls: ["./student-dashboard.component.css"],
})
export class StudentDashboardComponent implements OnInit {
  papers: Paper[] = [];
  searchQuery: string = "";
  searchResults: Paper[] = [];
  loading: boolean = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadAllPapers();
  }

  loadAllPapers() {
    this.loading = true;
    this.authService.getAllPapers().subscribe({
      next: (papers) => {
        this.papers = papers;
        this.searchResults = papers;
        this.loading = false;
      },
      error: (err) => {
        console.error("Error loading papers:", err);
        this.loading = false;
      },
    });
  }

  searchPapers() {
    if (this.searchQuery.trim()) {
      this.loading = true;
      this.authService.searchPapers(this.searchQuery).subscribe({
        next: (results) => {
          this.searchResults = results;
          this.loading = false;
        },
        error: (err) => {
          console.error("Search error:", err);
          this.loading = false;
        },
      });
    } else {
      this.searchResults = this.papers;
    }
  }

  logout() {
    this.authService.logout();
    window.location.href = "/login";
  }
}
