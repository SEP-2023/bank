import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class QrCodeService {
  // url: string = "http://localhost:8087";
  url: string = 'http://192.168.0.15:8087';

  constructor(private _http: HttpClient) {}

  getQrCode(id: string) {
    return this._http.get<any>(`${this.url}/getQrCode/` + id);
  }
}
