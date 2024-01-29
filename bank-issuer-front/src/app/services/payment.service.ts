import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {PaymentRequestDto} from "../dto/payment-request-dto";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  // url: string = "http://localhost:8088";
  url: string = "http://89.216.102.70:8088";

  constructor(private _http: HttpClient) {
  }


  pay(cardInfo: PaymentRequestDto) {
    return this._http.post<any>(`${this.url}/processPayment`, cardInfo, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      }),
    });
  }
}
