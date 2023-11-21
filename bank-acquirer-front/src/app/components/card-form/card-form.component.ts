import {Component, OnInit} from '@angular/core';
import {PaymentRequestDto} from "../../dto/payment-request-dto";
import {ActivatedRoute, Router} from "@angular/router";
import {PaymentService} from "../../services/payment.service";

@Component({
  selector: 'app-card-form',
  templateUrl: './card-form.component.html',
  styleUrls: ['./card-form.component.css']
})
export class CardFormComponent implements OnInit{
  cardInfo:PaymentRequestDto = new PaymentRequestDto();
  expirationDate:string = '';

  id: string = "";

  constructor(private route: ActivatedRoute, private paymentService: PaymentService) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params['id'];
      console.log(this.id)
    });
  }


  pay(){
    this.cardInfo.expirationMonth =  this.expirationDate.substring(0, 2);
    this.cardInfo.expirationYear =  this.expirationDate.substring(2);
    this.cardInfo.paymentId = this.id;
    console.log(this.cardInfo);
    this.paymentService.pay(this.cardInfo).subscribe({next: response => {
        console.log("Responseee" + response)
        window.location.href = response.status
      }, error: err => {
      console.log(err)
      }})

  }
}
