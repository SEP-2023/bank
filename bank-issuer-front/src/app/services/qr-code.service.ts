import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class QrCodeService {
  url: string = environment.bank_url;

  constructor(private _http: HttpClient) {}

  getQrCode(id: string) {
    return this._http.get<any>(`${this.url}/getQrCode/` + id);
  }
}
