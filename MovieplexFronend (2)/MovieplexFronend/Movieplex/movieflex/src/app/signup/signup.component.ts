import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../service/login.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import * as emailjs from 'emailjs-com';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html',
    styleUrls: ['./signup.component.css']
})

export class SignupComponent {

    name: string = "";
    email: string = "";
    password: string = "";
    confirmPassword: string = "";
    phoneNo: string = "";
    responseData: any;
    public uploadedImage: any = File;

    constructor(private fb: FormBuilder, private login: LoginService, private _sanckBar: MatSnackBar, private router: Router) { }

    registrationForm = this.fb.group({
        email: [""],
        password: ["",],
        confirmPassword: [""],
        name: [""],
        phoneNo: [""]
    }
    )


    public onImageUpload(event: any) {
        const userImg = event.target.files[0];
        this.uploadedImage = userImg;
    }

    addUser() {
        const userData = this.registrationForm.value;
        const fData = new FormData();
        // form data alway supports string file
        fData.append('userData', JSON.stringify(userData));
        fData.append('file', this.uploadedImage);

        this.login.register(fData).subscribe(
            next => {
                
                const emailParams = {
                    from_name: 'MovieApp',
                    to_name: this.registrationForm.value.name,
                    email: this.registrationForm.value.email,
                    message: 'Our adaptive video streaming technology guarantees the best possible video quality,adjusting automatically based on your available bandwidth. Whether you are on mobile networks or WiFi, enjoy a flawless video experience. Movieplex optimizes video playback for mobile networks with inconsistent throughput, ensuring a great experience even on lower bandwidths. Additionally, you have the flexibility to manually select the video quality that suits your preferences.Navigating through our vast content library is a breeze, thanks to our user-friendly interface. Movieplex organizes content thoughtfully, ensuring you are not overwhelmed with the breadth of options. Our blend of algorithms and human curation means you will discover exciting content at every stage of your interaction with Movieplex. Watch your experience evolve over time as our platform learns and adapts to your preferences.',
                   
                  };
            
                  // Email.js send function
                  emailjs
                    .send('service_2dgzkm6', 'template_c48nvsl', emailParams,'IDOYLH02X1Mm3NeIi')
                    .then((response) => {
                   
                      console.log('Email sent successfully:', response);
                    })
                    .catch((error) => {
                      console.error('Error sending email:', error);
                    });
                    // alert('Welcome To MoviePlex ' + this.registrationForm.value.name);
                    Swal.fire({
                        title: `Welcome To MoviePlex, ${this.registrationForm.value.name}`,
                        icon: 'success'
                      });
            },
            error => {
                alert(JSON.stringify(error));
            })
    }

}


