import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PaymentRequestDto } from '../dto/payment-request-dto';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  url: string = environment.bank_url;

  constructor(private _http: HttpClient) {}

  pay(cardInfo: PaymentRequestDto) {
    return this._http.post<any>(`${this.url}/processPayment`, cardInfo, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      }),
    });
  }
}
