import { Component, Inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DialogRef, DIALOG_DATA } from '@angular/cdk/dialog';
import { LoginService } from '../service/login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {

  

  constructor(public dialogRef: DialogRef, @Inject(DIALOG_DATA) private router: Router, private login : LoginService,
    private fb: FormBuilder) { }

  loginForm!: FormGroup
  email: string = "";
  tokenData: any;
  check:string = "true";

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      'email': new FormControl(null, [Validators.required, Validators.email]),
      'password': new FormControl(null, [Validators.required])
    })
  }

  onSubmit() {
    this.login.loginCheck(this.loginForm.value).subscribe( 
      response => { 
        this.tokenData = response;
      
        localStorage.setItem('jwtKey', this.tokenData.token);  
        localStorage.setItem('loginCheck', this.check);
        console.log('Token:', this.tokenData.token);
      
        alert("Login Success");
         location.reload();
        //  alert(JSON.stringify(this.tokenData.token));
    },
    error=>
      alert(JSON.stringify(error))
    )
  }

}
