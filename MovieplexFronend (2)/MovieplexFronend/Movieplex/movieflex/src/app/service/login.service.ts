import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  public flag: boolean = false;
  public guard: boolean = false;
  public name: string = "";

  constructor(private http: HttpClient) { }

  baseUrl = "http://localhost:8804/movie/api/v1";
  url = "http://localhost:8082/api/v1/login";


  register(file: FormData):Observable<any> {
    const endpoint = 'http://localhost:8804/movie/api/v1/addUser';
  
    return this.http.post(endpoint, file);
     
  }


  //latest update one
  updateUserWithEmail(file: File, userEmail: string, userData: any): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('email', userEmail);
    formData.append('userData', JSON.stringify(userData));

    let httpHeaders = new HttpHeaders({
        'Authorization': 'Bearer ' + localStorage.getItem('jwtKey')
    });
    let requestOptions = { headers: httpHeaders };

    return this.http.put(`${this.baseUrl}/updateWithEmail`, formData, requestOptions);
}




loginCheck(data: any) {
  return this.http.post(this.url, data);
}


getuser() {
  let httpHeaders = new HttpHeaders({
    'Authorization': 'Bearer ' + localStorage.getItem('jwtKey')
  });
  let requestOptions = { headers: httpHeaders }
  return this.http.get(this.baseUrl + "/get", requestOptions);
}



updateuser(user:any)
{

  let httpHeaders = new HttpHeaders({
    'Authorization': 'Bearer ' + localStorage.getItem('jwtKey')
  });
  let requestOptions = { headers: httpHeaders }

  return this.http.put(this.baseUrl + "/update", user,requestOptions);
}




getUserByEmail(email: string) {
  return this.http.get(this.baseUrl + "/getBy/" + email);
}

// updateuser(recorddata: any) {
//   let httpHeaders = new HttpHeaders({
//     'Authorization': 'Bearer ' + localStorage.getItem('jwtKey')
//   });
//   let requestOptions = { headers: httpHeaders }
//   return this.http.put(this.baseUrl + "/update", recorddata, requestOptions);
// }


// update user new one



// updateUser(email: string, userInfo: any, file?: File) {
//   const formData = new FormData();
//   formData.append('userInfo', JSON.stringify(userInfo));

//   if (file) {
//     formData.append('file', file, file.name);
//   }

//   const httpHeaders = new HttpHeaders({
//     'Authorization': 'Bearer ' + localStorage.getItem('jwtKey')
//   });

//   const requestOptions = {
//     headers: httpHeaders
//   };

//   const url = `${this.baseUrl}/update`;

//   return this.http.put(url, formData, requestOptions);
// }


// login.service.ts










resetPassword(email: string, password: string) {
  let httpHeaders = new HttpHeaders({
    'Authorization': 'Bearer ' + localStorage.getItem('jwtKey')
  });
  let requestOptions = { headers: httpHeaders }
  return this.http.post("http://localhost:8804/movie/api/v1/updatePassword/" + email + "/" + password, requestOptions, { responseType: 'text' });
}


}