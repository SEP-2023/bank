import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class QrCodeService {
  // url: string = 'http://localhost:8088';
  url: string = 'http://89.216.102.70:8088';

  constructor(private _http: HttpClient) {}

  getQrCode(id: string) {
    return this._http.get<any>(`${this.url}/getQrCode/` + id);
  }
}
