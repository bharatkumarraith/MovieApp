import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MoviesService } from '../service/movies.service';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit{
  typeof(arg0: any): any {
    throw new Error('Method not implemented.');
  }
  constructor(private movies: MoviesService, private router: Router) { }
  movieList: any = [];
  actionMovies: any = [];
  comedyMovies: any = [];
  animatedMovies: any = [];
  documentaryMovies: any = [];
  scienceFictionMovies: any = [];
  thrillerMovies: any = [];
  nameValue:any;
  showHeaderText:boolean = false;
  profileCheck:boolean = false;
  video!: HTMLVideoElement;

  ngOnInit() {
    this.getMovies();
    this.getActionMovies();
    this.getComedyMovies();
    this.getanimatedMovies();
    this.getdocumentaryMovies();
    this.getscienceFictionMovies();
    this.getThrillerMovies();
    this.nameValue = localStorage.getItem('name');
    if(this.nameValue != null || this.nameValue != undefined)
    {
      this.profileCheck= true;
    }

    setTimeout(() => {
      this.showHeaderText = true;
    }, 1000);

    this.video = document.getElementById('video') as HTMLVideoElement;
    this.video.muted = true;
  }

  getMovies() {
    this.movies.getMovies().subscribe(
      response => {
        this.movieList = response;
        this.movieList = this.movieList.results;
      }
    )
  }
  getActionMovies() {
    this.movies.fetchActionMovies().subscribe(
      response => {
        this.actionMovies = response;
        this.actionMovies = this.actionMovies.results;
        console.log(this.actionMovies);

      }
    )
  }
  go(item: any) {
    this.movies.item = item;
    this.router.navigateByUrl("movies");
  }
  getComedyMovies() {
    this.movies.fetchComedyMovies().subscribe(
      response => {
        this.comedyMovies = response;
        this.comedyMovies = this.comedyMovies.results;
      }
    )
  }
  getanimatedMovies() {
    this.movies.fetchAnimationMovies().subscribe(
      response => {
        this.animatedMovies = response;
        this.animatedMovies = this.animatedMovies.results;
      }
    )
  }
  getdocumentaryMovies() {
    this.movies.fetchDocumentaryMovies().subscribe(
      response => {
        this.documentaryMovies = response;
        this.documentaryMovies = this.documentaryMovies.results;
      }
    )
  }
  getscienceFictionMovies() {
    this.movies.fetchScienceFictionMovies().subscribe(
      response => {
        this.scienceFictionMovies = response;
        this.scienceFictionMovies = this.scienceFictionMovies.results;
      }
    )
  }
  getThrillerMovies() {
    this.movies.fetchThrillerMovies().subscribe(
      response => {
        this.thrillerMovies = response;
        this.thrillerMovies = this.thrillerMovies.results;
      }
    )
  }

  mute:boolean = true;
  toggleMute() {
    this.video.muted = !this.video.muted;
    if(this.video.muted == true){
      this.mute = true;
    }
    else{
      this.mute = false;
    }
  }

  }
