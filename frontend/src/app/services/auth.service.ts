import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable, BehaviorSubject } from "rxjs";

const httpOptions = {
  headers: new HttpHeaders({ "Content-Type": "application/json" }),
};

export interface User {
  id?: number;
  username: string;
  email: string;
  role?: string;
}

export interface Paper {
  id?: number;
  title: string;
  abstract: string;
  content: string;
  authorId?: number;
  authorName?: string;
  publishedBy?: string;
  published?: boolean;
  createdAt?: string;
  tags?: string[];
}

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private AUTH_API = "http://localhost:8080/api/auth/";
  private PAPER_API = "http://localhost:8080/api/papers/";

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is stored in localStorage
    const savedUser = localStorage.getItem("currentUser");
    if (savedUser) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  login(username: string, password: string): Observable<any> {
    // Validate input parameters to avoid sending null/undefined values
    if (
      !username ||
      !password ||
      username.trim() === "" ||
      password.trim() === ""
    ) {
      throw new Error("Username and password are required");
    }

    return this.http.post(
      this.AUTH_API + "login",
      {
        username: username.trim(),
        password: password.trim(),
      },
      httpOptions
    );
  }

  register(
    username: string,
    email: string,
    password: string,
    role: string
  ): Observable<any> {
    // Validate input parameters
    if (
      !username ||
      !email ||
      !password ||
      !role ||
      username.trim() === "" ||
      email.trim() === "" ||
      password.trim() === "" ||
      role.trim() === ""
    ) {
      throw new Error("All fields are required");
    }

    return this.http.post(
      this.AUTH_API + "signup",
      {
        username: username.trim(),
        email: email.trim(),
        password: password.trim(),
        role: role.trim().toUpperCase(),
      },
      httpOptions
    );
  }

  setCurrentUser(user: User) {
    localStorage.setItem("currentUser", JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  logout() {
    localStorage.removeItem("currentUser");
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return this.getCurrentUser() !== null;
  }

  getUserRole(): string | null {
    const user = this.getCurrentUser();
    return user ? user.role || null : null;
  }

  // Paper-related methods
  getAllPapers(): Observable<Paper[]> {
    return this.http.get<Paper[]>(this.PAPER_API + "all", httpOptions);
  }

  searchPapers(query: string): Observable<Paper[]> {
    return this.http.get<Paper[]>(
      `${this.PAPER_API}search?keyword=${encodeURIComponent(query)}`,
      httpOptions
    );
  }

  createPaper(paper: any, authorUsername: string): Observable<any> {
    return this.http.post(
      `${this.PAPER_API}create?authorUsername=${authorUsername}`,
      paper,
      httpOptions
    );
  }

  getMyPapers(username: string): Observable<Paper[]> {
    return this.http.get<Paper[]>(
      `${this.PAPER_API}author/${username}`,
      httpOptions
    );
  }

  publishPaper(paperId: number, committeeUsername: string): Observable<any> {
    return this.http.post(
      `${this.PAPER_API}publish/${paperId}?committeeUsername=${committeeUsername}`,
      {},
      httpOptions
    );
  }

  getPendingPapers(): Observable<Paper[]> {
    return this.http.get<Paper[]>(`${this.PAPER_API}unpublished`, httpOptions);
  }

  getPublishedPapers(): Observable<Paper[]> {
    return this.http.get<Paper[]>(`${this.PAPER_API}published`, httpOptions);
  }
}
