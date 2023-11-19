import { Component } from '@angular/core';
import {PaymentRequestDto} from "../../dto/payment-request-dto";

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css']
})
export class CardFormComponent {
  cardInfo:PaymentRequestDto = new PaymentRequestDto();
  expirationDate:string = '';

  pay(){
    this.cardInfo.expirationMonth =  this.expirationDate.split('/')[0];
    this.cardInfo.expirationYear =  this.expirationDate.split('/')[1];
    console.log(this.cardInfo);
  }
}
