import { Component, OnInit } from "@angular/core";
import { AuthService, Paper } from "../../../services/auth.service";

@Component({
  selector: "app-committee-dashboard",
  templateUrl: "./committee-dashboard.component.html",
  styleUrls: ["./committee-dashboard.component.css"],
})
export class CommitteeDashboardComponent implements OnInit {
  pendingPapers: Paper[] = [];
  allPapers: Paper[] = [];
  loading: boolean = false;
  activeTab: string = "pending";

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadPendingPapers();
    this.loadAllPapers();
  }

  loadPendingPapers() {
    this.loading = true;
    this.authService.getPendingPapers().subscribe({
      next: (papers) => {
        this.pendingPapers = papers;
        this.loading = false;
      },
      error: (err) => {
        console.error("Error loading pending papers:", err);
        this.loading = false;
      },
    });
  }

  loadAllPapers() {
    this.authService.getAllPapers().subscribe({
      next: (papers) => {
        this.allPapers = papers;
      },
      error: (err) => {
        console.error("Error loading papers:", err);
      },
    });
  }

  publishPaper(paperId: number) {
    if (
      confirm(
        "Are you sure you want to publish this paper? This demonstrates One-to-Many relationship (Committee → Published Papers)"
      )
    ) {
      const currentUser = this.authService.getCurrentUser();
      if (!currentUser) {
        alert("Error: No user logged in");
        return;
      }

      this.authService.publishPaper(paperId, currentUser.username).subscribe({
        next: (response) => {
          alert(
            "Paper published successfully! This shows Committee can publish multiple papers (One-to-Many)"
          );
          this.loadPendingPapers();
          this.loadAllPapers();
        },
        error: (err) => {
          alert("Error publishing paper: " + err.error.message);
        },
      });
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  logout() {
    this.authService.logout();
    window.location.href = "/login";
  }
}
